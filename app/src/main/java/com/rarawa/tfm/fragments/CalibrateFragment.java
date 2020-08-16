package com.rarawa.tfm.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.utils.Constants;


public class CalibrateFragment extends Fragment implements View.OnClickListener {

    int totalCalibrate = 0;

    SqliteHandler db;
    boolean registered;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new SqliteHandler(getContext());
        registered = db.userInfoExists();
        View rootView = inflater.inflate(R.layout.fragment_calibrate, container, false);

        Button btnSleep = rootView.findViewById(R.id.btnCalibrateSleep);
        Button btnExercise = rootView.findViewById(R.id.btnCalibrateExercise);
        TextView textCounter = rootView.findViewById(R.id.calibrate_counter);

        int calibrateSleep = db.calibrateSleepExists();
        int calibrateExercise = db.calibrateExerciseExists();

        //Configuration of button calibrateSleep
        if(calibrateSleep == 0){
            btnSleep.setEnabled(true);
            btnSleep.setOnClickListener(this);
        } else if(calibrateSleep == 3){
            btnSleep.setEnabled(false);
            btnSleep.setText("Calibrar durante el sueño (hecho)");
            totalCalibrate++;
        }

        //Configuration of button calibrateExercise
        if(calibrateExercise < 3){
            btnExercise.setEnabled(true);
            btnExercise.setOnClickListener(this);
        } else {
            btnExercise.setEnabled(false);
            btnExercise.setText("Calibrar durante el ejercicio físico (hecho)");
            totalCalibrate++;
        }

        //Configurate literal total calibrate
        if(totalCalibrate == 0){
            textCounter.setText("0/2");
            textCounter.setTextColor(getResources().getColor(R.color.red));
        } else if(totalCalibrate == 1){
            textCounter.setText("1/2");
            textCounter.setTextColor(getResources().getColor(R.color.orange));
        } else{
            textCounter.setText("2/2");
            textCounter.setTextColor(getResources().getColor(R.color.md_green_700));
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCalibrateSleep:
                ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_CALIBRATE_SLEEP);
                break;
            case R.id.btnCalibrateExercise:
                ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_CALIBRATE_EXERCISE);
                break;
        }

    }
}
