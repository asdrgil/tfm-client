package com.rarawa.tfm.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.rarawa.tfm.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CalibrateSleepFragment extends Fragment implements View.OnClickListener {

    TextView textInfo;
    SqliteHandler db;
    Button btnSleep;
    Button btnWakeUp;

    //Update btns literals and texts depending on the calibration status
    public void updateBtnTxt(){
        int sleepCalibrateStatus = db.calibrateSleepExists();

        //Neither sleep nor wakeup
        if(sleepCalibrateStatus == 0){
            btnSleep.setText(getResources().getText(R.string.calibrate_sleep_btn1));
            btnWakeUp.setEnabled(false);
            textInfo.setVisibility(View.GONE);

        //Sleep calibrated. Missing: wake up.
        } else if(db.calibrateSleepExists() == 1){
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
                } else {
                    db.insertSleepCalibrate();
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
                                    ((MainActivity) getActivity()).setFragment(
                                            Constants.FRAGMENT_CALIBRATE, getResources().getText(
                                                    R.string.calibrate_wakeup_snackbar3).toString(),
                                            Snackbar.LENGTH_LONG);
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
