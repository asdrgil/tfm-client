package com.rarawa.tfm.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.utils.CalibrateMeasurements;
import com.rarawa.tfm.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.rarawa.tfm.utils.Constants.EXECUTION_MODE;
import static com.rarawa.tfm.utils.Constants.EXECUTION_MODE_BLE;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_CALIBRATE_STATE_EXERCISE;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATED_EXERCISE;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATED_SLEEP;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATING_SLEEP;
import static com.rarawa.tfm.utils.Constants.STATUS_NOT_CALIBRATING_SLEEP;


public class CalibrateSleepFragment extends Fragment implements View.OnClickListener {

    TextView textInfo;
    SqliteHandler db;
    Button btnSleep;
    Button btnWakeUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_calibrate_sleep, container, false);

        btnSleep = rootView.findViewById(R.id.btnCalibrateSleep1);
        btnWakeUp = rootView.findViewById(R.id.btnCalibrateSleep2);

        db = new SqliteHandler(getContext());

        btnSleep.setOnClickListener(this);
        btnWakeUp.setOnClickListener(this);

        textInfo = rootView.findViewById(R.id.calibrate_sleep_text_info);

        updateBtnTxt();

        return rootView;
    }

    //Update btns literals and texts depending on the calibration status
    public void updateBtnTxt(){
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        int sleepCalibrateStatus = sharedPref.getInt(SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP, 0);

        //Neither sleep nor wakeup
        if(sleepCalibrateStatus == STATUS_NOT_CALIBRATING_SLEEP){
            btnSleep.setText(getResources().getText(R.string.calibrate_sleep_btn1));
            btnWakeUp.setEnabled(false);
            textInfo.setVisibility(View.GONE);

        //Sleep calibrated. Missing: wake up.
        } else if(db.calibrateSleepExists() == STATUS_CALIBRATING_SLEEP){
            btnSleep.setText("Cancelar");
            btnWakeUp.setEnabled(true);

            //Get current timestamp and format it to hh:mm to show it to the user
            long currentTimestamp = System.currentTimeMillis();
            Date date = new Date(currentTimestamp);
            String strTimeFormat = "HH:mm";
            DateFormat timeFormat = new SimpleDateFormat(strTimeFormat);
            String formattedTime= timeFormat.format(date);

            String text = getResources().getText(R.string.calibrate_sleep_info3).toString()
                    .replace("[horas]", formattedTime);
            textInfo.setText(text);
            textInfo.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //Button for indicating the sleep time
            case R.id.btnCalibrateSleep1:
                if(btnSleep.getText().equals("Cancelar")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Aviso")
                            .setMessage(getResources().getText(R.string.calibrate_sleep_alert))
                            .setCancelable(true)
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.deleteCalibrateSleep();
                                    updateBtnTxt();

                                    SharedPreferences sharedPref = getContext().getSharedPreferences(
                                            Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

                                    sharedPrefEditor.putInt(SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP,
                                            STATUS_NOT_CALIBRATING_SLEEP);
                                    sharedPrefEditor.commit();

                                    db.deleteMinimumMeasurementsSensor();

                                    MainActivity.snackbar(
                                            getResources().getText(
                                            R.string.calibrate_sleep_snackbar1).toString(),
                                            Snackbar.LENGTH_LONG);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.snackbar(
                                            getResources().getText(
                                                    R.string.snackbar_cancel).toString(),
                                            Snackbar.LENGTH_SHORT);
                                }
                            });

                    //Creating dialog box
                    AlertDialog dialog  = builder.create();
                    dialog.show();

                //Start calibrating
                } else {
                    db.insertSleepCalibrate();

                    SharedPreferences sharedPref = getContext().getSharedPreferences(
                            Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

                    sharedPrefEditor.putInt(SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP, STATUS_CALIBRATING_SLEEP);
                    sharedPrefEditor.commit();

                    updateBtnTxt();
                }

                break;

            case R.id.btnCalibrateSleep2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Aviso")
                        .setMessage(getResources().getText(R.string.calibrate_wakeup_alert))
                        .setCancelable(true)
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int result = db.insertWakeUpCalibrate();

                                //The timelapse from wakeup time is lower than
                                // Constants.MINIMUM_SLEEP_CALIBRATE_TIME
                                if(result == -2){
                                    MainActivity.snackbar(
                                            getResources().getText(
                                                    R.string.calibrate_wakeup_snackbar1).toString(),
                                            Snackbar.LENGTH_LONG);

                                //Sleep time is not defined
                                } else if(result == -1){
                                    MainActivity.snackbar(
                                            getResources().getText(
                                                    R.string.calibrate_wakeup_snackbar2).toString(),
                                            Snackbar.LENGTH_LONG);

                                //Everything went ok
                                } else {

                                    SharedPreferences sharedPref = getContext().getSharedPreferences(
                                            Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

                                    sharedPrefEditor.putInt(SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP,
                                            STATUS_CALIBRATED_SLEEP);
                                    sharedPrefEditor.commit();

                                    int calibrateStateExercise = sharedPref.getInt(SHAREDPREFERENCES_CALIBRATE_STATE_EXERCISE, 0);

                                    if(calibrateStateExercise != STATUS_CALIBRATED_EXERCISE) {
                                        ((MainActivity) getActivity()).setFragment(
                                                Constants.FRAGMENT_CALIBRATE, getResources().getText(
                                                        R.string.calibrate_wakeup_snackbar3).toString(),
                                                Snackbar.LENGTH_LONG);
                                    } else {


                                        if(EXECUTION_MODE == EXECUTION_MODE_BLE) {
                                            CalibrateMeasurements.calibrate(getContext(), db);
                                        }

                                        sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_MESSAGE_CALIBRATED, 1);
                                        sharedPrefEditor.commit();

                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.snackbar(
                                        getResources().getText(
                                                R.string.snackbar_cancel).toString(),
                                        Snackbar.LENGTH_SHORT);
                            }
                        });

                //Creating dialog box
                AlertDialog dialog  = builder.create();
                dialog.show();

                break;
        }

    }
}
