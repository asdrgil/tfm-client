package com.rarawa.tfm.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.Pattern;

public class DisplayPatternUtils {
    public static Pattern getCurrentPattern(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        String currentPatternsUnparsed =
                sharedPref.getString(
                        Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");

        if(currentPatternsUnparsed.length() > 0){
            SqliteHandler db = new SqliteHandler(context);

            String [] currentPatterns = currentPatternsUnparsed.split(",", 2);
            int currentPatternId = Integer.parseInt(currentPatterns[1]);
            Pattern currentPattern = db.getPattern(currentPatternId);

            return  currentPattern;
        } else {
            return null;
        }

    }

    //Generate new pattern for the given anger being displayed and return it
    public static Pattern generateNewPattern(Context context){
        SqliteHandler db = new SqliteHandler(context);

        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        String currentPatternsUnparsed =
                sharedPref.getString(Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");

        int displayAngerLevel = getDisplayAngerLevel(context);

        if(displayAngerLevel == db.getPenultimateAngerLevel().getAngerLevel() &&
                currentPatternsUnparsed.length() > 0){
            return rotatePatterns(context);
        } else if(currentPatternsUnparsed.length() == 0) {
            String randomOrderPatterns = db.getRandomOrderPatternsByAngerLevel(displayAngerLevel);

            if(randomOrderPatterns.length() == 0){
                randomOrderPatterns = db.getRandomOrderPatterns();
            }

            sharedPrefEditor.putString(Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER,
                    randomOrderPatterns);

            String [] currentPatterns = currentPatternsUnparsed.split(",", 2);
            return db.getPattern(Integer.parseInt(currentPatterns[0]));
        }

        return null;

    }

    public static Pattern rotatePatterns(Context context){
        SqliteHandler db = new SqliteHandler(context);

        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        String currentPatternsUnparsed =
                sharedPref.getString(Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");

        if(currentPatternsUnparsed.contains(",")){
            String [] currentPatterns = currentPatternsUnparsed.split(",", 2);
            currentPatternsUnparsed = currentPatterns[1].concat(",").concat(currentPatterns[1]);

            sharedPrefEditor.putString(Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER,
                    currentPatternsUnparsed);

            sharedPrefEditor.apply();

            currentPatternsUnparsed =
                    sharedPref.getString(Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");
            String [] currentPatterns2 = currentPatternsUnparsed.split(",", 2);

            return db.getPattern(Integer.parseInt(currentPatterns2[0]));
        } else if(currentPatternsUnparsed.length() > 0) {
            return db.getPattern(Integer.parseInt(currentPatternsUnparsed));
        } else {
            return null;
        }
    }

    public static void setDisplayAngerLevel(Context context){
        SqliteHandler db = new SqliteHandler(context);

        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID,
                db.getLastAngerLevel().getAngerLevel());

        sharedPrefEditor.commit();
    }

    public static int getDisplayAngerLevel(Context context){
        SqliteHandler db = new SqliteHandler(context);

        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        return sharedPref.getInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID, 0);
    }


}
