package com.rarawa.tfm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.Constants;

import java.util.Map;

import static com.rarawa.tfm.utils.Constants.REASON_ANGER;


public class MainFragment_1 extends Fragment implements View.OnClickListener {

    int spinnerPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_1, container, false);

        angerReasonSpinner(rootView);
        Button btnReasonAnger = rootView.findViewById(R.id.btnReasonAnger);
        btnReasonAnger.setOnClickListener( this);

        return rootView;
    }

    private void angerReasonSpinner(final View rootView) {

        Spinner spinner = rootView.findViewById(R.id.spinnerReasonAnger);

        String [] reasonsSpinner = REASON_ANGER.values().toArray(new String[REASON_ANGER.size()]);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, reasonsSpinner);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition = position;

                Button btnAccept = rootView.findViewById(R.id.btnReasonAnger);
                if(position > 0){
                    btnAccept.setEnabled(true);
                } else {
                    btnAccept.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Button btnAccept = rootView.findViewById(R.id.btnReasonAnger);
                btnAccept.setEnabled(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnReasonAnger:
                Log.d(Constants.LOG_TAG, "onClick btnReasonAnger");
                ((MainActivity) getActivity()).setSubFragment(Constants.SUBFRAGMENT_MAIN.get(2));

                SqliteHandler db = new SqliteHandler(getContext());
                AngerLevel lastAngerLevel = db.getLastAngerLevel();

                db.insertReasonAnger(lastAngerLevel.getId(), spinnerPosition);

                break;
        }

    }

}
