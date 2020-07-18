package com.rarawa.tfm.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.Constants;


public class MainFragment extends Fragment {

    public View rootView;
    SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Context context = getActivity();
        sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        SqliteHandler db = new SqliteHandler(getContext());
        AngerLevel lastAngerLevel = db.getLastAngerLevel();

        Log.d(Constants.LOG_TAG, "lastAngerLevel.getAngerLevel(): " + lastAngerLevel.getAngerLevel());

        /*if(lastAngerLevel.getAngerLevel() == 0){
            Log.d(Constants.LOG_TAG, "angerLevel == 0");
            ((MainActivity) getActivity()).setSubFragment(Constants.SUBFRAGMENT_MAIN.get(0));
        } else {*/

        Log.d(Constants.LOG_TAG, "mainFragment->subfragment");
        int mainFragment =
                sharedPref.getInt(Constants.SHAREDPREFERENCES_FRAGMENT_MAIN, 0);
        ((MainActivity) getActivity())
                .setSubFragment(Constants.SUBFRAGMENT_MAIN.get(mainFragment));

        //}


        return rootView;
    }
}
