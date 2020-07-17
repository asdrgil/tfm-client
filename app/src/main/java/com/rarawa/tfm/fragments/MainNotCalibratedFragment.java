package com.rarawa.tfm.fragments;

import android.os.Bundle;
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


public class MainNotCalibratedFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_not_calibrated, container,
                false);

        Button btnRegister = rootView.findViewById(R.id.btnRegister);
        Button btnCalibrate = rootView.findViewById(R.id.btnCalibrate);
        TextView textCounter = rootView.findViewById(R.id.calibrate_main);

        SqliteHandler db = new SqliteHandler(getContext());
        boolean registered = db.userInfoExists();
        int calibrateSleep = db.calibrateSleepExists();
        int calibrateExercise = db.calibrateExerciseExists();

        //User not registered
        if(!registered){
            btnRegister.setEnabled(true);
            btnCalibrate.setEnabled(false);
            btnRegister.setOnClickListener(this);

            textCounter.setText("0/2");
            textCounter.setTextColor(getResources().getColor(R.color.red));

        //Registered but not calibrated
        } else if(registered && (calibrateSleep + calibrateExercise < 6)) {
            btnRegister.setEnabled(false);
            btnCalibrate.setEnabled(true);
            btnCalibrate.setOnClickListener(this);

            btnRegister.setText(
                    (getResources().getString(R.string.main_not_registered_info2))
                            .concat((" (hecho)")));

            textCounter.setText("1/2");
            textCounter.setTextColor(getResources().getColor(R.color.orange));
        } else if(registered && (calibrateSleep + calibrateExercise == 6)){
            ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_MAIN);

        }

        return rootView;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_REGISTER);
                break;
            case R.id.btnCalibrate:
                ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_CALIBRATE);
                break;
        }
    }
}
