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
import com.rarawa.tfm.sqlite.models.DisplayedPattern;
import com.rarawa.tfm.sqlite.models.Pattern;
import com.rarawa.tfm.utils.Constants;
import com.rarawa.tfm.utils.DisplayPatternUtils;


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

        displayOldPattern(rootView);

        return rootView;
    }

    public void displayOldPattern(View rootView){

        TextView patternTextView = rootView.findViewById(R.id.textPatternValueOld);
        Pattern currentPattern = DisplayPatternUtils.getCurrentPattern(getContext());
        patternTextView.setText(currentPattern.getName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Update radio button info on DB
            case R.id.btnUsefulnessPattern:
                Log.d(Constants.LOG_TAG, "onClick btnUsefulnessPattern");
                SqliteHandler db = new SqliteHandler(getContext());
                AngerLevel lastAngerLevel = db.getLastAngerLevel();

                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

                Pattern currentPattern = DisplayPatternUtils.getCurrentPattern(getContext());

                int displayAngerLevelId =
                        sharedPref.getInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID, 0);

                if(!(displayAngerLevelId == 0 ||
                        (!usefulnessPatternYes.isChecked() && !usefulnessPatternNo.isChecked()))) {

                    DisplayedPattern displayedPattern = new DisplayedPattern();
                    displayedPattern.setId(currentPattern.getId());
                    displayedPattern.setAngerLevelId(displayAngerLevelId);

                    if (usefulnessPatternYes.isChecked()) {
                        displayedPattern.setStatus(1);
                    } else if(usefulnessPatternNo.isChecked()) {
                        displayedPattern.setStatus(-2);
                    }

                    db.updateDisplayedPattern(displayedPattern);

                    //Load new pattern
                    if (lastAngerLevel.getAngerLevel() > 0) {

                        //If the next anger level is the same as the current displayed one,
                        // just rotate the patterns to be shown
                        if(lastAngerLevel.getAngerLevel() ==
                                DisplayPatternUtils.getDisplayAngerLevel(getContext())){
                            DisplayPatternUtils.rotatePatterns(getContext());
                            DisplayPatternUtils.setDisplayAngerLevel(getContext());

                        //Else, generate new patterns
                        } else {
                            DisplayPatternUtils.setDisplayAngerLevel(getContext());
                            DisplayPatternUtils.generateNewPattern(getContext());
                        }

                        ((MainActivity) getActivity())
                                .setSubFragment(Constants.SUBFRAGMENT_MAIN.get(2));

                    } else {

                        DisplayPatternUtils.setDisplayAngerLevel(getContext());

                        ((MainActivity) getActivity())
                                .setSubFragment(Constants.SUBFRAGMENT_MAIN.get(0));
                    }
                }

                break;
        }

    }
}
