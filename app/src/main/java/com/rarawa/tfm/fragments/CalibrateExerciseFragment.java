package com.rarawa.tfm.fragments;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.utils.Constants;

import static com.rarawa.tfm.utils.Constants.MINIMUM_EXERCISE_CALIBRATE_TIME;


public class CalibrateExerciseFragment extends Fragment implements View.OnClickListener {

    TextView textInfo;
    SqliteHandler db;
    boolean registered;
    Button btnStart;
    ProgressBar progressBar;

    Chronometer chronometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new SqliteHandler(getContext());
        registered = db.userInfoExists();
        final View rootView = inflater.inflate(R.layout.fragment_calibrate_exercise, container, false);

        btnStart = rootView.findViewById(R.id.btnStartExercise);

        textInfo = rootView.findViewById(R.id.calibrate_sleep_text_info);

        chronometer =  rootView.findViewById(R.id.exercise_chronometer);
        progressBar = rootView.findViewById(R.id.progressBar);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {

                //progressBar.setProgress();

                long chronoTimeSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase())
                        /1000;
                long limitTimeSeconds = MINIMUM_EXERCISE_CALIBRATE_TIME/1000;
                long progressValue = chronoTimeSeconds*100/limitTimeSeconds;

                progressBar.setProgress((int) progressValue);

                //Time limit reached
                if(limitTimeSeconds - chronoTimeSeconds <= 0){
                    chronometer.stop();
                    chronometer.setTextColor(getResources().getColor(R.color.md_green_700));
                    TextView maxTimeTxt = rootView.findViewById(R.id.maxTimeTxt);
                    maxTimeTxt.setTextColor(getResources().getColor(R.color.md_green_700));


                    KeyguardManager myKM = (KeyguardManager) getContext()
                            .getSystemService(Context.KEYGUARD_SERVICE);

                    //If screen is locked, send a push notification in order to assure that the user
                    //sees that  the exercise calibration was done correctly
                    if( myKM.isKeyguardLocked()) {
                        //Create channel
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel1 = new NotificationChannel(
                                    "CHANNEL_1_ID",
                                    "Channel 1",
                                    NotificationManager.IMPORTANCE_HIGH
                            );
                            channel1.setDescription("This is Channel 1");
                            channel1.setImportance(NotificationManager.IMPORTANCE_HIGH);
                            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                            channel1.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                            //TODO: tick sound
                            //channel1.setSound();

                            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel1);
                        }


                        Notification notification = new NotificationCompat.Builder(getContext(), "CHANNEL_1_ID")
                                .setSmallIcon(R.drawable.ic_thermometer_3)
                                .setContentTitle("Termómetro de la ira")
                                .setContentText(getResources().getText(R.string.calibrate_exercise_info3).toString())
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                        notificationManager.notify(1, notification);
                    }

                    db.insertEndExercise();

                    ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_MAIN, getResources().getText(R.string.calibrate_exercise_info3).toString(), Snackbar.LENGTH_LONG);
                }
            }
        });

        btnStart.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(final View view) {
        if(btnStart.getText().equals("Cancelar")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Aviso")
                    .setMessage("¿Quieres cancelar la calibración del ejercicio?")
                    .setCancelable(true)
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.undoStartExercise();
                            chronometer.setBase(SystemClock.elapsedRealtime());

                            btnStart.setText("Iniciar ejercicio");
                            MainActivity.snackbar(
                                    getResources().getText(
                                            R.string.snackbar_cancel).toString(),
                                    Snackbar.LENGTH_SHORT);

                            progressBar.setProgress(0);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            //Creating dialog box
            AlertDialog dialog  = builder.create();
            dialog.show();
        } else {

            db.insertStartExercise();

            btnStart.setText("Cancelar");

            chronometer.start();
        }
    }

}

