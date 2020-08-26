package com.rarawa.tfm.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import static com.rarawa.tfm.utils.Constants.DIRECTION_LEFT;
import static com.rarawa.tfm.utils.Constants.DIRECTION_RIGHT;
import static com.rarawa.tfm.utils.Constants.LOG_TAG;


public class MainFragment_2 extends Fragment implements View.OnClickListener {

    public View rootView;
    public TextView patternTextView;
    TextView patternCommentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "mainFragment_2");

        rootView = inflater.inflate(R.layout.fragment_main_2, container, false);
        patternTextView = rootView.findViewById(R.id.textPatternValue);
        patternCommentView = rootView.findViewById(R.id.patternComment);

        displayNextPattern(DIRECTION_RIGHT);

        Button btnPrevPattern = rootView.findViewById(R.id.btnPrevPattern);
        Button btnNextPattern = rootView.findViewById(R.id.btnNextPattern);
        Button btnSaveComment = rootView.findViewById(R.id.btnSaveComment);
        Button btnOkPattern = rootView.findViewById(R.id.btnOkPattern);


        patternCommentView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (s.toString().equals("")) {
                    btnSaveComment.setEnabled(false);
                } else {
                    btnSaveComment.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnPrevPattern.setOnClickListener(this);
        btnNextPattern.setOnClickListener(this);
        btnSaveComment.setOnClickListener(this);
        btnOkPattern.setOnClickListener(this);

        return rootView;
    }

    public void displayNextPattern(int rotateDirection){

        Log.d(LOG_TAG, "displayNextPattern");

        Pattern newPattern = DisplayPatternUtils.generateNewPattern(getContext(), rotateDirection);
        String newPatternText = "No existe ninguna pauta definida. Consulta con tu terapeuta.";

        if(newPattern != null){
            newPatternText = newPattern.getName();
        } else {
            Button btnOkPattern = rootView.findViewById(R.id.btnOkPattern);
            Button btnPrevPattern = rootView.findViewById(R.id.btnPrevPattern);
            Button btnNextPattern = rootView.findViewById(R.id.btnNextPattern);

            btnOkPattern.setVisibility(View.GONE);
            btnPrevPattern.setVisibility(View.GONE);
            btnNextPattern.setVisibility(View.GONE);
            patternCommentView.setVisibility(View.GONE);
        }



        patternTextView.setText(newPatternText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnPrevPattern:
                displayNextPattern(DIRECTION_LEFT);
                break;

            case R.id.btnNextPattern:
                displayNextPattern(DIRECTION_RIGHT);
                break;

            case R.id.btnSaveComment:
                saveComment();
                break;

            case R.id.btnOkPattern:
                saveComment();

                ((MainActivity) getActivity()).setSubFragment(
                        Constants.SUBFRAGMENT_MAIN.get(3));

                break;
        }

    }

    public void saveComment(){
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
            }
        }
    }

}
