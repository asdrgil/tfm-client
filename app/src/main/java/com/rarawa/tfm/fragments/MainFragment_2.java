package com.rarawa.tfm.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.ApiRest;
import com.rarawa.tfm.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

import static com.rarawa.tfm.MainActivity.snackbarProgressBar;
import static com.rarawa.tfm.utils.Constants.MEASUREMENT_VALID_TIME;
import static com.rarawa.tfm.utils.Constants.REASON_ANGER;


public class MainFragment_2 extends Fragment implements View.OnClickListener {

    public View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_2, container, false);

        Button btnNextPattern = rootView.findViewById(R.id.btnNextPattern);
        btnNextPattern.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnNextPattern:
                Log.d(Constants.LOG_TAG, "onClick btnNextPattern");
                ((MainActivity) getActivity()).setSubFragment(Constants.SUBFRAGMENT_MAIN.get(3));
                break;
        }

    }

}
