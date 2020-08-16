package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.mikephil.charting.data.BarEntry;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.CalibrateExercise;
import com.rarawa.tfm.sqlite.models.CalibrateRingMeasurements;
import com.rarawa.tfm.sqlite.models.CalibrateSleep;
import com.rarawa.tfm.utils.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//TODO

public class CalibrateRingMeasurementsHandler extends SQLiteOpenHelper {
    public CalibrateRingMeasurementsHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(CalibrateRingMeasurements.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + CalibrateRingMeasurements.TABLE_NAME);
        //onCreate(db);
    }

    public static void deleteMinimumMeasurementsSensor(SQLiteDatabase db){
        String query = String.format("DELETE FROM %s" +
                                "WHERE %s < 5",
                CalibrateRingMeasurements.TABLE_NAME,
                CalibrateRingMeasurements.COLUMN_ID);

        db.rawQuery(query, null);

        db.close();
    }

    public static void deleteMaximumMeasurementsSensor(SQLiteDatabase db){
        String query = String.format("DELETE FROM %s" +
                        "WHERE %s > 4",
                CalibrateRingMeasurements.TABLE_NAME,
                CalibrateRingMeasurements.COLUMN_ID);

        db.rawQuery(query, null);

        db.close();
    }

    public static HashMap<Integer, Long> getMinimumMeasurementsSensor(int sensor, SQLiteDatabase db) {
        HashMap<Integer, Long> result = new HashMap<>();

        String query = String.format("SELECT %s, %s " +
                "FROM %s " +
                "WHERE %s = %d " +
                "ORDER BY %s ASC LIMIT 4",
                CalibrateRingMeasurements.COLUMN_ID, CalibrateRingMeasurements.COLUMN_VALUE,
                CalibrateRingMeasurements.TABLE_NAME,
                CalibrateRingMeasurements.COLUMN_SENSOR, sensor,
                CalibrateRingMeasurements.COLUMN_VALUE);

        Cursor cursor = db.rawQuery(query, null);

        //result
        int count = cursor.getCount();

        for(int i=0; i<count; i++){
            cursor.moveToNext();
            result.put(i, cursor.getLong(0));
        }

        cursor.close();

        return result;
    }

    public static HashMap<Integer, Long> getMaximumMeasurementsSensor(int sensor, SQLiteDatabase db) {
        HashMap<Integer, Long> result = new HashMap<>();

        String query = String.format("SELECT %s, %s " +
                        "FROM %s " +
                        "WHERE %s = %d " +
                        "ORDER BY %s DESC LIMIT 4",
                CalibrateRingMeasurements.COLUMN_ID, CalibrateRingMeasurements.COLUMN_VALUE,
                CalibrateRingMeasurements.TABLE_NAME,
                CalibrateRingMeasurements.COLUMN_SENSOR, sensor,
                CalibrateRingMeasurements.COLUMN_VALUE);

        Cursor cursor = db.rawQuery(query, null);

        //result
        int count = cursor.getCount();

        for(int i=0; i<count; i++){
            cursor.moveToNext();
            result.put(i, cursor.getLong(0));
        }

        cursor.close();

        return result;
    }

    public static void setMinimumMeasurementsSensor(int sensor, long value, SQLiteDatabase db){
        HashMap<Integer, Long> minimumMeasurementsSensor = getMinimumMeasurementsSensor(sensor, db);

        Iterator it = minimumMeasurementsSensor.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            long elementValue = Long.parseLong(element.getValue().toString());

            if(elementValue > value){
                ContentValues values = new ContentValues();
                values.put(CalibrateRingMeasurements.COLUMN_VALUE, value);
                values.put(CalibrateRingMeasurements.COLUMN_TIMESTAMP, System.currentTimeMillis()/1000);

                db.update(CalibrateRingMeasurements.TABLE_NAME, values, CalibrateRingMeasurements.COLUMN_VALUE + " = ?",
                        new String[]{String.valueOf(elementValue)});

                break;
            }

        }
    }

    public static void setMaximumMeasurementsSensor(int sensor, long value, SQLiteDatabase db){
        HashMap<Integer, Long> maximumMeasurementsSensor = getMaximumMeasurementsSensor(sensor, db);

        Iterator it = maximumMeasurementsSensor.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            long elementValue = Long.parseLong(element.getValue().toString());

            if(elementValue < value){
                ContentValues values = new ContentValues();
                values.put(CalibrateRingMeasurements.COLUMN_VALUE, value);
                values.put(CalibrateRingMeasurements.COLUMN_TIMESTAMP, System.currentTimeMillis()/1000);

                db.update(CalibrateRingMeasurements.TABLE_NAME, values, CalibrateRingMeasurements.COLUMN_VALUE + " = ?",
                        new String[]{String.valueOf(elementValue)});

                break;
            }

        }
    }

    //TODO
    public static int measurementsToEpisode(double acceleration, int heartRate, int EDA,
                                            SqliteHandler db){
        return 0;
    }
}
