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
import com.rarawa.tfm.sqlite.models.ReasonAnger;
import com.rarawa.tfm.utils.Constants;
import com.rarawa.tfm.utils.DisplayPatternUtils;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;
import static com.rarawa.tfm.utils.Constants.SUBFRAGMENT_MAIN;


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
                Log.d(LOG_TAG, "onClick btnUsefulnessPattern");
                SqliteHandler db = new SqliteHandler(getContext());
                AngerLevel lastAngerLevel = db.getLastAngerLevel();
                AngerLevel penultimateAngerLevel = db.getPenultimateAngerLevel();

                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

                Pattern currentPattern = DisplayPatternUtils.getCurrentPattern(getContext());

                int displayAngerLevelId =
                        sharedPref.getInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID, 0);

                if(usefulnessPatternYes.isChecked() || usefulnessPatternNo.isChecked()) {
                    usefulnessPatternNo.setError(null);

                    DisplayedPattern displayedPattern = new DisplayedPattern();
                    displayedPattern.setPatternId(currentPattern.getId());
                    displayedPattern.setAngerLevelId(displayAngerLevelId);

                    if (usefulnessPatternYes.isChecked()) {
                        displayedPattern.setStatus(1);
                    } else if(usefulnessPatternNo.isChecked()) {
                        displayedPattern.setStatus(-2);
                    }

                    DisplayPatternUtils.setDisplayAngerLevel(getContext());
                    db.updateDisplayedPattern(displayedPattern);

                    //Load new pattern as it is still in an episode
                    if (lastAngerLevel.getAngerLevel() > 0) {

                        ((MainActivity) getActivity())
                                .setSubFragment(SUBFRAGMENT_MAIN.get(2));

                    //End of episode
                    } else {

                        ((MainActivity) getActivity())
                                .setSubFragment(SUBFRAGMENT_MAIN.get(0));
                    }
                } else {
                    usefulnessPatternNo.setError("Es necesario rellenar este campo");
                }

                break;
        }

    }
}
