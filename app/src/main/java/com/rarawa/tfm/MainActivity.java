package com.rarawa.tfm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rarawa.tfm.fragments.CalibrateExerciseFragment;
import com.rarawa.tfm.fragments.CalibrateFragment;
import com.rarawa.tfm.fragments.CalibrateSleepFragment;
import com.rarawa.tfm.fragments.EpisodesHistoryFragment;
import com.rarawa.tfm.fragments.MainFragment;
import com.rarawa.tfm.fragments.MainFragment_0;
import com.rarawa.tfm.fragments.MainFragment_1;
import com.rarawa.tfm.fragments.MainFragment_2;
import com.rarawa.tfm.fragments.MainFragment_3;
import com.rarawa.tfm.fragments.MainNotCalibratedFragment;
import com.rarawa.tfm.fragments.RegisterFragment;
import com.rarawa.tfm.services.GenerateEpisodesService;
import com.rarawa.tfm.services.UpdateWidgetService;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.sqlite.models.UserInfo;
import com.rarawa.tfm.utils.ApiRest;
import com.rarawa.tfm.utils.CalibrateMeasurements;
import com.rarawa.tfm.utils.Constants;
import com.rarawa.tfm.utils.InsertDebugRegisters;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.rarawa.tfm.utils.Constants.AUTOMATIC_REGISTER;
import static com.rarawa.tfm.utils.Constants.EXECUTION_MODE;
import static com.rarawa.tfm.utils.Constants.EXECUTION_MODE_BLE;
import static com.rarawa.tfm.utils.Constants.EXECUTION_MODE_SIMULATE_DB;
import static com.rarawa.tfm.utils.Constants.EXECUTION_MODE_SIMULATE_EPISODE;
import static com.rarawa.tfm.utils.Constants.LOG_TAG;
import static com.rarawa.tfm.utils.Constants.SENSOR_ACC;
import static com.rarawa.tfm.utils.Constants.SENSOR_EDA;
import static com.rarawa.tfm.utils.Constants.SENSOR_HR;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_CALIBRATE_STATE_EXERCISE;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATED_EXERCISE;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATED_SLEEP;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATING_EXERCISE;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATING_SLEEP;

public class MainActivity extends AppCompatActivity {

    private static CoordinatorLayout coordinatorLayout;
    private static DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBar actionBar;
    MenuItem summaryItem;

    private static SqliteHandler db;
    boolean registered;
    boolean calibrated;
    private UserInfo userInfo = null;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bleGatt;
    private boolean scanning;

    GenerateEpisodesService mGenerateEpisodesService;
    boolean mServiceBound = false;
    BroadcastReceiver receiver;

    UpdateWidgetService mUpdateWidgetService;
    boolean mServiceBound2 = false;

    private static NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        db = new SqliteHandler(this);
        registered = db.userInfoExists();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.activity_main_layout);

        if(EXECUTION_MODE == EXECUTION_MODE_BLE) {
            startBLEStreaming();

        } else if(EXECUTION_MODE == EXECUTION_MODE_SIMULATE_DB) {
            if(!db.userInfoExists()) {
                InsertDebugRegisters.insertRegisters(getApplicationContext());
            }

        } else if(AUTOMATIC_REGISTER){
            if(!db.userInfoExists()) {
                db.updgradeDB();

                InsertDebugRegisters.insertSleepCalibrate(getApplicationContext());
                InsertDebugRegisters.insertExerciseCalibrate(getApplicationContext());

                InsertDebugRegisters.insertUser(db);
                InsertDebugRegisters.insertPatterns(db);
            }
        }

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        int calibrateStateSleep = sharedPref.getInt(SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP, 0);
        int calibrateStateExercise = sharedPref.getInt(SHAREDPREFERENCES_CALIBRATE_STATE_EXERCISE, 0);

        calibrated = calibrateStateSleep == STATUS_CALIBRATED_SLEEP
                && calibrateStateExercise == STATUS_CALIBRATED_EXERCISE;
        boolean registered = db.userInfoExists();

        updateNavigationView();

        if(registered){
            //Get updated patterns for the patient periodically
            Handler handler = new Handler();

            //Review periodically if there are new pattern changes on the web that
            //need to be updated on the Android device
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    ApiRest.callUpdatePatternsPatient(getApplicationContext());
                    ApiRest.callUpdateDisplayPatterns(getApplicationContext());
                    handler.postDelayed(this, Constants.UPDATE_PATTERNS_FREQUENCY);
                }
            };

            handler.post(runnableCode);

            if(calibrated) {

                int showSnackbarCalibrated =
                        sharedPref.getInt(Constants.SHAREDPREFERENCES_MESSAGE_CALIBRATED, 0);

                if(showSnackbarCalibrated > 0){
                    SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                    sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_MESSAGE_CALIBRATED, 0);
                    sharedPrefEditor.commit();

                    snackbar(getResources().getText(R.string.calibrate_exercise_info3).toString(),
                            Snackbar.LENGTH_LONG);
                }

                if(EXECUTION_MODE == EXECUTION_MODE_SIMULATE_EPISODE) {
                    receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String message = intent.getStringExtra("MESSAGE");
                            //Log.d(LOG_TAG, "Received string: " + message);

                            String messsageArr[] = message.split(",");
                            int currentAngerLevel = Integer.parseInt(messsageArr[0]);

                            updateMainSubFragment(context, currentAngerLevel);
                            updateThermometer(db, findViewById(R.id.thermoIcon),
                                    findViewById(R.id.textLevel), currentAngerLevel);
                        }
                    };
                }
            }
        }
    }

    //TODO: ver si es necesario este método (creo que no, que se puede llamar a fragment_main y
    //ahí ya coge los datos.
    private void updateMainSubFragment(Context context, int currentAngerLevel){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        int mainFragmentPref = sharedPref.getInt(Constants.SHAREDPREFERENCES_FRAGMENT_MAIN, 0);

        //Timestamp in seconds
        long firstTmpPlateau =
                sharedPref.getLong(Constants.SHAREDPREFERENCES_FIRST_ZERO_LEVEL_PLATEAU, 0);

        boolean isOneMinuteRest = System.currentTimeMillis()/1000 - firstTmpPlateau >=
                Constants.TIME_EPISODE_IS_OBSOLETE;

        Log.d(LOG_TAG, "firstTmpPlateau: " + firstTmpPlateau);

        if(currentAngerLevel == 0 && firstTmpPlateau == 0){
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putLong(Constants.SHAREDPREFERENCES_FIRST_ZERO_LEVEL_PLATEAU,
                    System.currentTimeMillis()/1000);
            sharedPrefEditor.commit();
        }

        //Load mainFragment_1
        if(currentAngerLevel > 0){

            if(firstTmpPlateau > 0) {
                SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putLong(Constants.SHAREDPREFERENCES_FIRST_ZERO_LEVEL_PLATEAU, 0);
                sharedPrefEditor.commit();
            }

            if(mainFragmentPref == 0) {
                Log.d(LOG_TAG, "currentAngerLevel > 0 && mainFragmentPref == 0");
                setSubFragment(Constants.SUBFRAGMENT_MAIN.get(1));
            }

        //If there has been a whole minute without an episode and the user has not finished
        //interacting with the previous episode => Discard the episode and upload the main fragment
        //used for the case when there are no episodes.
        } else if (mainFragmentPref > 0 && isOneMinuteRest){
            Log.d(LOG_TAG, "isOneMinuteRest clause");

            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putString(Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");
            sharedPrefEditor.apply();

            setSubFragment(Constants.SUBFRAGMENT_MAIN.get(0));
        }

    }

    private void updateThermometer(SqliteHandler db, ImageView thermoIcon, TextView thermoText,
                                   int currentAngerLevel){
        Log.d(LOG_TAG, "updateThermometer");

        Map.Entry<Integer, String> currentEntry =
                Constants.ANGER_LEVEL_UI_MAP.get(currentAngerLevel);

        //If the thermometer UI element is not updated, update it
        if(!currentEntry.getValue().equals(thermoText)){

            thermoText.setText(currentEntry.getValue());
            thermoIcon.setImageResource(currentEntry.getKey());
        }

        AngerLevel lastAngerLevel = db.getLastAngerLevel();
        AngerLevel penultimateAngerLevel = db.getPenultimateAngerLevel();

        //Start of an episode
        if(penultimateAngerLevel.getAngerLevel() == 0 && lastAngerLevel.getAngerLevel() > 0){
            db.insertReasonAnger(lastAngerLevel.getId(), 0);

        //End of an episode
        } else if(penultimateAngerLevel.getAngerLevel() > 0 && lastAngerLevel.getAngerLevel() == 0){
            db.updateLastReasonAnger(lastAngerLevel.getId());
        }


    }

    /** BLE variables and methods **/
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "connection established, discovering services");
                        // Once we have connection start BLE service discovery
                        gatt.discoverServices();
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            enableStreamingNotification(gatt);
                        }
                    });
        }

        private void enableStreamingNotification(BluetoothGatt gatt) {
            Log.d(LOG_TAG, "services discovered, enabling notifications");
            // Get the MM service
            BluetoothGattService mmService = gatt.getService(Constants.mmServiceUUID);
            // Get the streaming characteristic
            BluetoothGattCharacteristic streamingCharacteristic =
                    mmService.getCharacteristic(Constants.streamingCharacteristicUUID);
            // Enable notifications on the streaming characteristic
            gatt.setCharacteristicNotification(streamingCharacteristic, true);
            // For some reason the above does not tell the ring that it should
            // start sending notifications, so we have to do it explicitly
            BluetoothGattDescriptor cccDescriptor =
                    streamingCharacteristic.getDescriptor(Constants.clientCharacteristicConfigurationUUID);
            cccDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(cccDescriptor);
        }

        @Override
        public void onCharacteristicChanged(
                BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] payload = characteristic.getValue();
            // Decode payload
            int status = payload[0] & 0xff;
            int mm = payload[1] & 0xff;
            // Instant EDA value is in payload bytes 2 and 3 in big-endian format
            int eda = ((payload[2] & 0xff) << 8) | (payload[3] & 0xff);
            // Acceleration in x, y and z directions
            int ax = payload[4] & 0xff;
            int ay = payload[5] & 0xff;
            int az = payload[6] & 0xff;
            // Acceleration magnitude
            double acc = Math.sqrt(ax*ax + ay*ay + az*az);
            Log.d(LOG_TAG, String.format("st:%02x\tmm:%d\teda:%d\ta:%.1f", status, mm, eda, acc));

            SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                    Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
            int calibrateStateSleep = sharedPref.getInt(SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP, 0);
            int calibrateStateExercise = sharedPref.getInt(SHAREDPREFERENCES_CALIBRATE_STATE_EXERCISE, 0);

            long heartRateValue = new Random().nextInt(90) + 60;

            if(calibrateStateSleep == STATUS_CALIBRATING_SLEEP){
                //Minimum ACC and HR is not required
                //db.setMinimumMeasurementsSensor(SENSOR_ACC, (long) acc);
                //db.setMinimumMeasurementsSensor(SENSOR_HR, (long) heartRateValue);
                db.setMinimumMeasurementsSensor(SENSOR_EDA, (long) eda);

            } else if(calibrateStateExercise == STATUS_CALIBRATING_EXERCISE){
                db.setMaximumMeasurementsSensor(SENSOR_ACC, (long) acc);
                db.setMaximumMeasurementsSensor(SENSOR_HR, (long) heartRateValue);
                db.setMaximumMeasurementsSensor(SENSOR_EDA, (long) eda);

            } else if (calibrateStateSleep == STATUS_CALIBRATED_SLEEP
                    && calibrateStateExercise == STATUS_CALIBRATED_EXERCISE){

                int currentAngerLevel  =
                        CalibrateMeasurements.measurementsToRangeLevel(
                                (long) acc, heartRateValue, eda, getBaseContext());

                long currentTimestamp = System.currentTimeMillis();
                db.insertAngerLevel(currentTimestamp, currentAngerLevel);

                updateMainSubFragment(getBaseContext(), currentAngerLevel);
                updateThermometer(db, findViewById(R.id.thermoIcon),
                        findViewById(R.id.textLevel), currentAngerLevel);
            }
        }
    };

    private final BluetoothAdapter.LeScanCallback scanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.d(LOG_TAG, String.format("found ring %s, rssi: %d", device.getAddress(), rssi));
                    stopScanning();
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(LOG_TAG, String.format("connecting with %s", device.getAddress()));
                                    // Establish a connection with the ring.
                                    bleGatt = device.connectGatt(MainActivity.this, false, gattCallback);
                                }
                            });
                }
            };

    // Called after user has allowed or rejected our request to enable bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                startScanning();
            } else {
                Log.d(LOG_TAG, "bluetooth not enabled");
            }
        }
    }

    private void startScanning() {
        if (scanning) return;
        scanning = true;
        Log.d(LOG_TAG, "scanning for rings");
        // Start scanning for devices that support the Moodmetric service
        bluetoothAdapter.startLeScan(new UUID[] { Constants.mmServiceUUID }, scanCallback);
    }

    private void stopScanning() {
        if (!scanning) return;
        scanning = false;
        Log.d(LOG_TAG, "scanning stopped");
        bluetoothAdapter.stopLeScan(scanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanning) stopScanning();
        if (bleGatt != null) {
            bleGatt.close();
        }
    }

    private void startBLEStreaming(){
        // Get the bluetooth manager from Android
        final BluetoothManager bluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Log.d(LOG_TAG, "bluetooth service not available");
            return;
        }
        // Get a reference to the bluetooth adapter of the device
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.d(LOG_TAG, "bluetooth not supported");
            return;
        }
        // Check that bluetooth is enabled. If not, ask the user to enable it.
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
            return;
        }
        // If bluetooth was enabled start scanning for rings.
        startScanning();
    }

    private void updateNavigationView(){
        NavigationView navigationView = findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);

            if(registered && !calibrated){
                Log.d(LOG_TAG, "registered && !calibrated");

                Menu menu = navigationView.getMenu();

                MenuItem item_summary = menu.findItem(R.id.item_navigation_drawer_summary);
                item_summary.setEnabled(false);

                MenuItem item_register = menu.findItem(R.id.item_navigation_drawer_register);
                item_register.setTitle("Registrar dispositivo (hecho)");
                item_register.setEnabled(false);

                MenuItem item_calibrate = menu.findItem(R.id.item_navigation_drawer_calibrate);
                item_calibrate.setEnabled(true);

                setFragment(Constants.FRAGMENT_INDEX_NOT_CALIBRATED);

            } else if(!registered) {
                Log.d(LOG_TAG, "!registered");

                Menu menu = navigationView.getMenu();

                MenuItem item_summary = menu.findItem(R.id.item_navigation_drawer_summary);
                item_summary.setEnabled(false);

                MenuItem item_register = menu.findItem(R.id.item_navigation_drawer_register);
                item_register.setEnabled(true);

                MenuItem item_calibrate = menu.findItem(R.id.item_navigation_drawer_calibrate);
                item_register.setTitle("Calibrar dispositivo (hecho)");
                item_calibrate.setEnabled(false);

                setFragment(Constants.FRAGMENT_INDEX_NOT_CALIBRATED);

            } else if (registered && calibrated) {
                Log.d(LOG_TAG, "registered && calibrated");

                Menu menu = navigationView.getMenu();

                MenuItem item_summary = menu.findItem(R.id.item_navigation_drawer_summary);
                item_summary.setEnabled(true);

                MenuItem item_register = menu.findItem(R.id.item_navigation_drawer_register);
                item_register.setTitle("Registrar dispositivo (hecho)");
                item_register.setEnabled(false);

                MenuItem item_calibrate = menu.findItem(R.id.item_navigation_drawer_calibrate);
                item_register.setTitle("Calibrar dispositivo (hecho)");
                item_calibrate.setEnabled(false);

                setFragment(Constants.FRAGMENT_MAIN);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(registered){
            setNameLiteralMenu(findViewById(R.id.menu_username));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_index:
                                menuItem.setChecked(true);

                                if(!registered | (registered && userInfo.getCalibrated() == 0)){
                                    setFragment(Constants.FRAGMENT_INDEX_NOT_CALIBRATED);
                                } else {
                                    setFragment(Constants.FRAGMENT_MAIN);
                                }

                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;

                            case R.id.item_navigation_drawer_register:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                setFragment(Constants.FRAGMENT_REGISTER);
                                return true;

                            case R.id.item_navigation_drawer_calibrate:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                setFragment(Constants.FRAGMENT_CALIBRATE);
                                return true;

                            case R.id.item_navigation_drawer_summary:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(Gravity.START);
                                setFragment(Constants.FRAGMENT_HISTORY);
                                return true;
                        }

                        return true;
                    }
                });
    }

    public static void setDrawableRegister(){
        if(!db.userInfoExists()){
            return;
        }

        //Set calibrate as enabled
        Menu menu = navigationView.getMenu();

        MenuItem item_calibrate = menu.findItem(R.id.item_navigation_drawer_calibrate);
        item_calibrate.setEnabled(true);
    }


    public static void setNameLiteralMenu(TextView username){

        if(!db.userInfoExists()){
            return;
        }

        UserInfo userInfo = db.getUserInfo();

        String name = userInfo.getName();
        String surname1 = userInfo.getSurname1();
        String surname2 = userInfo.getSurname2();

        //Set literal of the drawable
        String literalName;

        if(name.length() + surname1.length() + surname2.length() > 20){
            literalName = String.format("%s %s. %s.",
                    name, surname1.substring(0, 1), surname2.substring(0, 1));
        } else {
            literalName = String.format("%s %s %s", name, surname1, surname2);
        }

        username.setText(literalName);
    }

    public void setFragment(String id) {
        Log.d(LOG_TAG, "setFragment: " + id);
        setFragment(id, "", 0);
    }

    public void setFragment(String id, String snackbar, int length) {
        //Hide keyboard before loading the next fragment so that if a snackbar is being
        //displayed, it can be correctly seeing. In general, this improves a bit the UX
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        TextView fragmentTitle = findViewById(R.id.fragmentTitle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        switch (id) {
            case Constants.FRAGMENT_MAIN:
                fragmentTitle.setText("Página principal");
                MainFragment mainFragment = new MainFragment();
                fragmentTransaction.replace(R.id.fragment, mainFragment);
                break;
            case Constants.FRAGMENT_INDEX_NOT_CALIBRATED:
                fragmentTitle.setText("Primeros pasos");
                MainNotCalibratedFragment mainNotCalibratedFragment =
                        new MainNotCalibratedFragment();
                fragmentTransaction.replace(R.id.fragment, mainNotCalibratedFragment);
                break;
            case Constants.FRAGMENT_REGISTER:
                fragmentTitle.setText("Registrar dispositivo");
                RegisterFragment registerFragment = new RegisterFragment();
                fragmentTransaction.replace(R.id.fragment, registerFragment);
                break;
            case Constants.FRAGMENT_CALIBRATE:
                fragmentTitle.setText("Calibrar dispositivo");
                CalibrateFragment calibrateFragment = new CalibrateFragment();
                fragmentTransaction.replace(R.id.fragment, calibrateFragment);
                break;
            case Constants.FRAGMENT_CALIBRATE_SLEEP:
                fragmentTitle.setText("Calibrar durante el sueño");
                CalibrateSleepFragment calibrateSleepFragment = new CalibrateSleepFragment();
                fragmentTransaction.replace(R.id.fragment, calibrateSleepFragment);
                break;
            case Constants.FRAGMENT_CALIBRATE_EXERCISE:
                fragmentTitle.setText("Calibrar durante el ejercicio físico");
                CalibrateExerciseFragment calibrateExerciseFragment = new CalibrateExerciseFragment();
                fragmentTransaction.replace(R.id.fragment, calibrateExerciseFragment);
                break;

            case Constants.FRAGMENT_HISTORY:
                fragmentTitle.setText("Historial de episodios");
                EpisodesHistoryFragment episodesHistoryFragment = new EpisodesHistoryFragment();
                fragmentTransaction.replace(R.id.fragment, episodesHistoryFragment);
                break;
        }

        fragmentTransaction.commit();

        if(snackbar.length() > 0){
            snackbar(snackbar, length);
        }
    }

    public void setSubFragment(String id) {
        Log.d(LOG_TAG, "setSubFragment: " + id);
        setSubFragment(id, "", 0);
    }

    public void setSubFragment(String id, String snackbar, int length) {

        TextView fragmentTitle = findViewById(R.id.fragmentTitle);
        fragmentTitle.setText("Gestión de un episodio");

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(id == Constants.SUBFRAGMENT_MAIN.get(1)){
            MainFragment_1 mainFragment_1 = new MainFragment_1();
            fragmentTransaction.replace(R.id.subfragment_main, mainFragment_1);
            sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_FRAGMENT_MAIN, 1);

        } else if(id == Constants.SUBFRAGMENT_MAIN.get(2)){
            MainFragment_2 mainFragment_2 = new MainFragment_2();
            fragmentTransaction.replace(R.id.subfragment_main, mainFragment_2);
            sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_FRAGMENT_MAIN, 2);

        } else if(id == Constants.SUBFRAGMENT_MAIN.get(3)){
            MainFragment_3 mainFragment_3 = new MainFragment_3();
            fragmentTransaction.replace(R.id.subfragment_main, mainFragment_3);
            sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_FRAGMENT_MAIN, 3);
        } else {
            fragmentTitle.setText("Página principal");

            MainFragment_0 mainFragment_0 = new MainFragment_0();
            fragmentTransaction.replace(R.id.subfragment_main, mainFragment_0);
            sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_FRAGMENT_MAIN, 0);
        }

        sharedPrefEditor.commit();
        fragmentTransaction.commit();

        if(snackbar.length() > 0){
            snackbar(snackbar, length);
        }
    }

    public static void snackbar(String text, int length){
        Log.d(LOG_TAG, "Inside snackbar");

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, text, length);

        snackbar.show();
    }

    public static void snackbarProgressBar(String text, int length, Context context){
        Snackbar bar = Snackbar.make(coordinatorLayout, text, length);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        final ProgressBar progressBar = new ProgressBar(context);

        contentLay.addView(progressBar);
        bar.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(registered && calibrated) {
            LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                    new IntentFilter("RESULT")
            );

            //Start generate episodes
            if(EXECUTION_MODE == EXECUTION_MODE_SIMULATE_EPISODE) {
                Intent intent = new Intent(this, GenerateEpisodesService.class);
                startService(intent);
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }

            Intent intent2 = new Intent(this, UpdateWidgetService.class);
            startService(intent2);
            bindService(intent2, mServiceConnection2, Context.BIND_IMPORTANT);
        }


    }

    @Override
    protected void onStop() {

        if(registered && calibrated) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            if (mServiceBound) {
                if(EXECUTION_MODE == EXECUTION_MODE_SIMULATE_EPISODE) {
                    unbindService(mServiceConnection);
                }
                unbindService(mServiceConnection2);
                mServiceBound = false;
            }
        }

        super.onStop();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GenerateEpisodesService.MyBinder myBinder = (GenerateEpisodesService.MyBinder) service;
            mGenerateEpisodesService = myBinder.getService();
            mServiceBound = true;
        }
    };

    private ServiceConnection mServiceConnection2 = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound2 = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UpdateWidgetService.MyBinder myBinder = (UpdateWidgetService.MyBinder) service;
            mUpdateWidgetService = myBinder.getService();
            mServiceBound2 = true;
        }
    };


}