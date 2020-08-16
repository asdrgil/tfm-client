package com.rarawa.tfm.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.ApiRest;
import com.rarawa.tfm.utils.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.rarawa.tfm.MainActivity.snackbarProgressBar;
import static com.rarawa.tfm.utils.Constants.LOG_TAG;
import static com.rarawa.tfm.utils.Constants.MEASUREMENT_VALID_TIME;
import static com.rarawa.tfm.utils.Constants.REASON_ANGER;


public class MainFragment_0 extends Fragment implements View.OnClickListener {

    public View rootView;
    SqliteHandler db;
    SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_0, container, false);
        db = new SqliteHandler(getContext());
        sharedPref = getContext().getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        setString1();
        setString2();
        setString3();

        Button btnViewHistory = rootView.findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener((View.OnClickListener) this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnViewHistory:
                ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_HISTORY);
                break;
        }
    }

    public void setString1(){
        TextView textMain0_0 = rootView.findViewById(R.id.textMain0_0);

        long firstTmpPlateau =
                sharedPref.getLong(Constants.SHAREDPREFERENCES_FIRST_ZERO_LEVEL_PLATEAU, 0);

        if(firstTmpPlateau == 0){
            textMain0_0.setVisibility(View.GONE);
        } else {

            long plateauTime = System.currentTimeMillis()/1000 - firstTmpPlateau;
            long days = plateauTime / (24*60*60);
            long hours = (plateauTime-(days*24*60*60)) / 3600;
            long minutes = (plateauTime-(days*24*60*60+hours*60*60)) / 60;
            long seconds = plateauTime-(days*24*60*60+hours*60*60+minutes*60);

            /*Log.d(LOG_TAG, "firstTmpPlateau: " + firstTmpPlateau);
            Log.d(LOG_TAG, "plateauTime: " + plateauTime);
            Log.d(LOG_TAG, "plateauDays: " + days);
            Log.d(LOG_TAG, "plateauHours: " + hours);
            Log.d(LOG_TAG, "plateauMinutes: " + minutes);
            Log.d(LOG_TAG, "plateauSeconds: " + seconds);*/

            String result = "";

            if (days > 0) {
                result = String.format("%d día", days);
            }

            if(days > 1){
                result += "s";
            }

            if (hours > 0) {
                if (result.length() > 0) {
                    result +="y ";
                }

                result += String.format("%d hora", hours);

                if(hours > 1){
                    result += "s";
                }
            }

            /*if (minutes > 0) {
                if (result.length() > 0) {
                    result += ", ";
                }

                result += String.format(" %d minuto", minutes);

                if(minutes > 1){
                    result += "s";
                }
            }*/

            /*if (seconds > 0) {
                if (result.length() > 0) {
                    result += ", ";
                }

                result += String.format("%d segundo", seconds);

                if(seconds > 1){
                    result += "s";
                }
            }*/

            if(result.length() > 0) {
                textMain0_0.setVisibility(View.VISIBLE);
                String originalText = textMain0_0.getText().toString();
                textMain0_0.setText(originalText.replace("{0}", result));
            } else {
                textMain0_0.setVisibility(View.GONE);
            }
        }
    }

    public void setString2(){
        HashMap<Integer, Integer> result = db.getTotalEpisodesTodayYesterday();

        TextView textMain0_1 = rootView.findViewById(R.id.textMain0_1);

        int difference = result.get(0) - result.get(1);

        String secondLiteral = "";

        if(difference == 0){
            secondLiteral = "(igual que el día anterior)";
        } else {

            String diffLit = difference > 0 ? "más" : "menos";


            secondLiteral = String.format("(%d %s que el día anterior)",
                    Math.abs(difference), diffLit);
        }

        String originalText = textMain0_1.getText().toString();
        String newText = originalText.replace("{0}", String.valueOf(result.get(0)))
                .replace("{1}", secondLiteral);

        textMain0_1.setText(newText);

    }

    public void setString3(){
        TextView textMain0_2 = rootView.findViewById(R.id.textMain0_2);
        TextView textMain0_3 = rootView.findViewById(R.id.textMain0_3);
        TextView textMain0_4 = rootView.findViewById(R.id.textMain0_4);
        TextView textMain0_5 = rootView.findViewById(R.id.textMain0_5);

        long currentTimestamp = System.currentTimeMillis()/1000;
        long prevWeekTimestamp = currentTimestamp - 7*24*60*60;

        HashMap<Integer, String> result = db.getTopUsefulPatterns(
                prevWeekTimestamp, currentTimestamp, 3);

        if(result.size() == 0){
            textMain0_2.setVisibility(View.GONE);
            textMain0_3.setVisibility(View.GONE);
            textMain0_4.setVisibility(View.GONE);
            textMain0_5.setVisibility(View.GONE);
            return;
        }

        if(result.size() >= 1){
            textMain0_2.setVisibility(View.VISIBLE);
            textMain0_3.setVisibility(View.VISIBLE);

            String [] res = result.get(0).split("|");

            textMain0_3.setText(res[0]);
        }

        if(result.size() >= 2){
            textMain0_4.setVisibility(View.VISIBLE);

            String [] res = result.get(1).split("|");

            textMain0_4.setText(res[0]);
        }

        if(result.size() >= 3){
            textMain0_5.setVisibility(View.VISIBLE);

            String [] res = result.get(2).split("|");

            textMain0_5.setText(res[0]);
        }

    }

}
