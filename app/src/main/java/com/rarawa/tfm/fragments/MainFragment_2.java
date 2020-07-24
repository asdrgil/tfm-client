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
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.DisplayedPattern;
import com.rarawa.tfm.sqlite.models.Pattern;
import com.rarawa.tfm.utils.Constants;
import com.rarawa.tfm.utils.DisplayPatternUtils;


public class MainFragment_2 extends Fragment implements View.OnClickListener {

    public View rootView;
    public TextView patternTextView;
    TextView patternCommentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_2, container, false);
        patternTextView = rootView.findViewById(R.id.textPatternValue);
        patternCommentView = rootView.findViewById(R.id.patternCommentInfo);

        displayNextPattern();

        Button btnNextPattern = rootView.findViewById(R.id.btnNextPattern);
        Button btnOkPattern = rootView.findViewById(R.id.btnOkPattern);
        btnNextPattern.setOnClickListener(this);
        btnOkPattern.setOnClickListener(this);

        return rootView;
    }

    public void displayNextPattern(){

        Log.d(Constants.LOG_TAG, "displayNextPattern");

        SqliteHandler db = new SqliteHandler(getContext());

        Pattern newPattern = DisplayPatternUtils.generateNewPattern(getContext());
        String newPatternText = "No existe ninguna pauta definida. Consulta con tu terapeuta.";

        if(newPattern != null){
            newPatternText = newPattern.getName();
        } else {
            Button btnOkPattern = rootView.findViewById(R.id.btnOkPattern);
            Button btnNextPattern = rootView.findViewById(R.id.btnNextPattern);

            btnOkPattern.setVisibility(View.GONE);
            btnNextPattern.setVisibility(View.GONE);
            patternCommentView.setVisibility(View.GONE);
        }

        patternTextView.setText(newPatternText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnNextPattern:
                displayNextPattern();
                break;

            case R.id.btnOkPattern:
                SqliteHandler db = new SqliteHandler(getContext());

                String patternCommentValue =  patternCommentView.getText().toString().trim();

                if(patternCommentValue.length() > 0){
                    SharedPreferences sharedPref = getContext().getSharedPreferences(
                            Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

                    Pattern currentPattern = DisplayPatternUtils.getCurrentPattern(getContext());

                    int displayAngerLevelId =
                            sharedPref.getInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID,
                                    0);

                    if(currentPattern != null){
                        DisplayedPattern displayedPattern = new DisplayedPattern();
                        displayedPattern.setId(currentPattern.getId());
                        displayedPattern.setAngerLevelId(displayAngerLevelId);
                        displayedPattern.setComments(patternCommentValue);

                        db.updateDisplayedPattern(displayedPattern);

                        Log.d(Constants.LOG_TAG, "onClick btnNextPattern");
                        ((MainActivity) getActivity()).setSubFragment(
                                Constants.SUBFRAGMENT_MAIN.get(3));
                        break;
                    }
                }
        }

    }

}
