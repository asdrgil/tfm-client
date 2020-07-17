package com.rarawa.tfm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;


public class MainFragment_3 extends Fragment implements View.OnClickListener {

    public View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_3, container, false);

        Button btnUsefulnessPattern = rootView.findViewById(R.id.btnUsefulnessPattern);
        btnUsefulnessPattern.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnUsefulnessPattern:
                Log.d(Constants.LOG_TAG, "onClick btnUsefulnessPattern");
                ((MainActivity) getActivity()).setSubFragment(Constants.SUBFRAGMENT_MAIN.get(2));
                break;
        }

    }
}
