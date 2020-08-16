package com.rarawa.tfm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.Pattern;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_FILE;

public class DisplayPatternUtils {
    public static Pattern getCurrentPattern(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        String currentPatternsUnparsed =
                sharedPref.getString(
                        SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");

        if(currentPatternsUnparsed.length() > 0){
            SqliteHandler db = new SqliteHandler(context);

            String [] currentPatterns = currentPatternsUnparsed.split(",", 2);
            int currentPatternId = Integer.parseInt(currentPatterns[0]);
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
                SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        String currentPatternsUnparsed =
                sharedPref.getString(SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");

        int displayAngerLevel = getDisplayAngerLevel(context);

        //If the displayed anger level is the same as the previous anger level and there
        // are loaded patterns => Rotate the patterns
        if(displayAngerLevel == db.getPenultimateAngerLevel().getAngerLevel() &&
                currentPatternsUnparsed.length() > 0){
            return rotatePatterns(context);

        //If the displayed anger level is different from the previous anger level
        //Or there are no loaded patterns
        // => generate new patterns
        } else {
            currentPatternsUnparsed = db.getRandomOrderPatternsByAngerLevel(displayAngerLevel);

            //If there are no defined patterns for the given anger level id
            // => get patterns from other levels in random order
            if(currentPatternsUnparsed.length() == 0){
                currentPatternsUnparsed = db.getRandomOrderPatterns();
            }

            //If there are no patterns defined on the database
            // => return null
            if(currentPatternsUnparsed.length() == 0){
                return null;
            }

            sharedPrefEditor.putString(SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER,
                    currentPatternsUnparsed);

            sharedPrefEditor.commit();

            String [] currentPatterns = currentPatternsUnparsed.split(",", 2);
            return db.getPattern(Integer.parseInt(currentPatterns[0]));
        }

    }

    public static Pattern rotatePatterns(Context context){
        SqliteHandler db = new SqliteHandler(context);

        SharedPreferences sharedPref = context.getSharedPreferences(
                SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        String currentPatternsUnparsed =
                sharedPref.getString(SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");

        if(currentPatternsUnparsed.contains(",")){
            String [] currentPatterns = currentPatternsUnparsed.split(",", 2);
            currentPatternsUnparsed = currentPatterns[1].concat(",").concat(currentPatterns[1]);

            sharedPrefEditor.putString(SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER,
                    currentPatternsUnparsed);

            sharedPrefEditor.apply();

            currentPatternsUnparsed =
                    sharedPref.getString(SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER, "");
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
                SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID,
                db.getLastAngerLevel().getAngerLevel());

        sharedPrefEditor.commit();
    }

    public static int getDisplayAngerLevel(Context context){
        SqliteHandler db = new SqliteHandler(context);

        SharedPreferences sharedPref = context.getSharedPreferences(
                SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        return sharedPref.getInt(Constants.SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID, 0);
    }


}
