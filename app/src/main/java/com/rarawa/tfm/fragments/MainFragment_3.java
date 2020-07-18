package com.rarawa.tfm.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.Constants;


public class MainFragment_3 extends Fragment implements View.OnClickListener {

    public View rootView;
    SqliteHandler db = new SqliteHandler(getContext());
    RadioButton usefulnessPatternYes;
    RadioButton usefulnessPatternNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_3, container, false);
        usefulnessPatternYes = rootView.findViewById(R.id.usefulnessPatternYes);
        usefulnessPatternNo = rootView.findViewById(R.id.usefulnessPatternNo);

        Button btnUsefulnessPattern = rootView.findViewById(R.id.btnUsefulnessPattern);
        btnUsefulnessPattern.setOnClickListener(this);

        return rootView;
    }

    public void displayOldPattern(View rootView){
        SqliteHandler db = new SqliteHandler(getContext());

        TextView patternTextView = rootView.findViewById(R.id.textPatternValueOld);

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        String oldPatternUnparsed =
                sharedPref.getString(Constants.SHAREDPREFERENCES_NEXT_PATTERN, "");

        String [] oldPatternElements =  oldPatternUnparsed.split(",");
        String  oldPatternText = oldPatternElements[1];

        patternTextView.setText(oldPatternText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnUsefulnessPattern:
                Log.d(Constants.LOG_TAG, "onClick btnUsefulnessPattern");
                SqliteHandler db = new SqliteHandler(getContext());
                AngerLevel lastAngerLevel = db.getLastAngerLevel();

                if(lastAngerLevel.getAngerLevel() > 0) {
                    ((MainActivity) getActivity())
                            .setSubFragment(Constants.SUBFRAGMENT_MAIN.get(2));
                } else {
                    ((MainActivity) getActivity())
                            .setSubFragment(Constants.SUBFRAGMENT_MAIN.get(0));
                }

                //Update radio button info on DB

                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

                int displayAngerLevelId =
                        sharedPref.getInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID, 0);

                if(displayAngerLevelId == 0 ||
                        (!usefulnessPatternYes.isChecked() && usefulnessPatternNo.isChecked())){
                    break;
                }

                AngerLevel displayAngerLevel = db.getAngerLevelById(displayAngerLevelId);

                if(usefulnessPatternYes.isChecked()){
                    displayAngerLevel.setUsefulnessPattern(1);
                } else {
                    displayAngerLevel.setUsefulnessPattern(0);
                }

                db.updateAngerLevel(displayAngerLevel);

                break;
        }

    }
}
