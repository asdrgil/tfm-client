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
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.sqlite.models.Patterns;
import com.rarawa.tfm.utils.Constants;


public class MainFragment_2 extends Fragment implements View.OnClickListener {

    public View rootView;
    public TextView patternTextView;
    TextView patternCommentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_2, container, false);
        patternTextView = rootView.findViewById(R.id.textPatternValue);
        patternCommentView = rootView.findViewById(R.id.patternCommentInfo);

        displayNextPattern(rootView);

        Button btnNextPattern = rootView.findViewById(R.id.btnNextPattern);
        Button btnOkPattern = rootView.findViewById(R.id.btnOkPattern);
        btnNextPattern.setOnClickListener(this);
        btnOkPattern.setOnClickListener(this);

        return rootView;
    }

    public void displayNextPattern(View rootView){

        Log.d(Constants.LOG_TAG, "displayNextPattern");

        SqliteHandler db = new SqliteHandler(getContext());
        AngerLevel lastAngerLevel = db.getLastAngerLevel();

        String newPatternText = "";

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        String nextPatternUnparsed =
                sharedPref.getString(Constants.SHAREDPREFERENCES_NEXT_PATTERN, "");

        //No patterns on the queue => Get new pattern
        if(nextPatternUnparsed.length() == 0){
            Patterns newPattern = db.getRandomPatternByAngerLevel(lastAngerLevel.getAngerLevel());

            //No pattern has been defined for this level
            if(newPattern == null || newPattern.getId() == 0){
                newPatternText =
                        "No hay pautas definidas para este nivel de ira. Consulta con tu terapeuta.";
                sharedPrefEditor
                        .putString(Constants.SHAREDPREFERENCES_CURRENT_PATTERN, "");
            } else {
                newPatternText = newPattern.getName();
                sharedPrefEditor
                        .putString(Constants.SHAREDPREFERENCES_CURRENT_PATTERN,
                                String.format("%d,%s",newPattern.getId(), newPattern.getName()));
            }

        //One pattern on queue -> Move it to the front
        } else{
            String [] nextPatternElements =  nextPatternUnparsed.split(",");

            newPatternText = nextPatternElements[1];

            sharedPrefEditor
                    .putString(Constants.SHAREDPREFERENCES_CURRENT_PATTERN, nextPatternUnparsed);

            sharedPrefEditor
                    .putString(Constants.SHAREDPREFERENCES_NEXT_PATTERN, "");
        }

        sharedPrefEditor
                .putInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID, db.getLastAngerLevel().getId());

        sharedPrefEditor.apply();

        patternTextView.setText(newPatternText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnNextPattern:
                displayNextPattern(rootView);
                break;

            case R.id.btnOkPattern:
                SqliteHandler db = new SqliteHandler(getContext());

                String patternCommentValue =  patternCommentView.getText().toString().trim();

                if(patternCommentValue.length() > 0){
                    SharedPreferences sharedPref = getContext().getSharedPreferences(
                            Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

                    int displayAngerLevelId =
                            sharedPref.getInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID,
                                    0);

                    AngerLevel displayAngerLevel = db.getAngerLevelById(displayAngerLevelId);

                    Log.d(Constants.LOG_TAG, "displayAngerLevelId: "  + displayAngerLevelId);

                    if(displayAngerLevel.getAngerLevel() > 0 && patternCommentValue.length() > 0){
                        displayAngerLevel.setCommentPattern(patternCommentValue);
                        db.updateAngerLevel(displayAngerLevel);
                    }
                }


                Log.d(Constants.LOG_TAG, "onClick btnNextPattern");
                ((MainActivity) getActivity()).setSubFragment(Constants.SUBFRAGMENT_MAIN.get(3));
                break;
        }

    }

}
