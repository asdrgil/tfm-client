package com.rarawa.tfm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.rarawa.tfm.sqlite.SqliteHandler;

import java.util.HashMap;

import static com.rarawa.tfm.utils.Constants.SENSOR_ACC;
import static com.rarawa.tfm.utils.Constants.SENSOR_EDA;
import static com.rarawa.tfm.utils.Constants.SENSOR_HR;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_CALIBRATE_STATE_SLEEP;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_THRESHOLD_ACC;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_THRESHOLD_EDA_0;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_THRESHOLD_EDA_1;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_THRESHOLD_EDA_2;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_THRESHOLD_EDA_3;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_THRESHOLD_EDA_4;
import static com.rarawa.tfm.utils.Constants.SHAREDPREFERENCES_THRESHOLD_HR;
import static com.rarawa.tfm.utils.Constants.STATUS_CALIBRATED_EXERCISE;

public class CalibrateMeasurements {

    public static void calibrate(Context context, SqliteHandler db){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        HashMap<Integer, Long> minimumEDA = db.getMinimumMeasurementsSensor(SENSOR_EDA);

        HashMap<Integer, Long> maximumAcc = db.getMaximumMeasurementsSensor(SENSOR_ACC);
        HashMap<Integer, Long> maximumHR = db.getMaximumMeasurementsSensor(SENSOR_HR);
        HashMap<Integer, Long> maximumEDA = db.getMaximumMeasurementsSensor(SENSOR_EDA);

        //Remove minimum and maximum values of all of them and get the average value with the rest
        minimumEDA.remove(0);

        maximumAcc.remove(0);
        maximumHR.remove(0);
        maximumEDA.remove(0);

        //Set reference values for the patient regarding EDA values

        long basalEDA = minimumEDA.get(1);
        long stepEDA = (maximumEDA.get(1) - basalEDA)/4;

        sharedPrefEditor.putLong(Constants.SHAREDPREFERENCES_THRESHOLD_EDA_0,
                basalEDA);
        sharedPrefEditor.putLong(SHAREDPREFERENCES_THRESHOLD_EDA_1,
                basalEDA+stepEDA);
        sharedPrefEditor.putLong(SHAREDPREFERENCES_THRESHOLD_EDA_2,
                basalEDA+stepEDA*2);
        sharedPrefEditor.putLong(SHAREDPREFERENCES_THRESHOLD_EDA_3,
                basalEDA+stepEDA*3);
        sharedPrefEditor.putLong(Constants.SHAREDPREFERENCES_THRESHOLD_EDA_4,
                basalEDA+stepEDA*4);

        //Set reference values for the patient regarding ACC values

        long totalAccVal = 0;

        for(long accValue : maximumAcc.values()){
            totalAccVal += accValue;
        }

        long thresholdAcc = (long) ((totalAccVal/maximumAcc.size())*0.9);

        sharedPrefEditor.putLong(Constants.SHAREDPREFERENCES_THRESHOLD_ACC,
                thresholdAcc);

        //Set reference values for the patient regarding HR values

        long totalHRVal = 0;

        for(long HRValue : maximumAcc.values()){
            totalHRVal += HRValue;
        }

        long thresholdHR = (long) ((totalAccVal/maximumAcc.size())*0.75);

        sharedPrefEditor.putLong(Constants.SHAREDPREFERENCES_THRESHOLD_HR,
                thresholdHR);


        sharedPrefEditor.commit();
    }

    public static int measurementsToRangeLevel(long acc, long hr, long eda, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);

        long accThreshold = sharedPref.getLong(SHAREDPREFERENCES_THRESHOLD_ACC, 0);
        long hrThreshold = sharedPref.getLong(SHAREDPREFERENCES_THRESHOLD_HR, 0);

        boolean accCriteria =  acc < accThreshold;
        boolean hrCriteria =  hr > hrThreshold;

        if(!accCriteria || !hrCriteria){
            return 0;
        }

        long [] edaThreshold = new long []{
                sharedPref.getLong(SHAREDPREFERENCES_THRESHOLD_EDA_0, 0),
                sharedPref.getLong(SHAREDPREFERENCES_THRESHOLD_EDA_1, 0),
                sharedPref.getLong(SHAREDPREFERENCES_THRESHOLD_EDA_2, 0),
                sharedPref.getLong(SHAREDPREFERENCES_THRESHOLD_EDA_3, 0),
                sharedPref.getLong(SHAREDPREFERENCES_THRESHOLD_EDA_4, 0),
        };

        return getClosestValue(edaThreshold, eda);

    }

    //Code based on SO's answer from @JvR
    public static int getClosestValue(long[] a, long x) {
        int idx = java.util.Arrays.binarySearch(a, x);
        if ( idx < 0 ) {
            idx = -idx - 1;
        }

        if ( idx == 0 ) {
            return idx;
        } else if (idx == a.length) {
            return idx - 1;
        }

        return Math.abs(x - a[idx - 1]) < Math.abs(x - a[idx]) ? idx - 1 : idx;
    }

}
