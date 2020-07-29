package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.CalibrateSleep;
import com.rarawa.tfm.utils.Constants;

import static com.rarawa.tfm.utils.Constants.MINIMUM_SLEEP_CALIBRATE_TIME;

public class CalibrateSleepHandler extends SQLiteOpenHelper {
    public CalibrateSleepHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CalibrateSleep.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CalibrateSleep.TABLE_NAME);
        //onCreate(db);
    }

    public static void insertSleepCalibrate(SQLiteDatabase db) {
        //There can only be one register on this table
        if(calibrateSleepExists(db) > 0){
            deleteCalibrateSleep(db);
        }

        //Get writable database
        ContentValues values = new ContentValues();
        values.put(CalibrateSleep.COLUMN_ID, 1);
        values.put(CalibrateSleep.COLUMN_START_TIMESTAMP, System.currentTimeMillis());

        //Insert new register
        db.insert(CalibrateSleep.TABLE_NAME, null, values);
        db.close();
    }

    public static int insertWakeUpCalibrate(SQLiteDatabase db) {

        if(calibrateSleepExists(db) == 0){
            return -1;
        } else {

            CalibrateSleep calibrate = getCalibrate(db);

            if(calibrate.getStartTimestamp() == 0){
                return -1;
            }

            long currentTimeMilis = System.currentTimeMillis();

            //It is required at least two hours between the start time and the end time in order
            // to calibrate the wristband.
            if(currentTimeMilis - calibrate.getStartTimestamp() < MINIMUM_SLEEP_CALIBRATE_TIME){
                Log.e(Constants.LOG_TAG, String.valueOf(currentTimeMilis));
                Log.e(Constants.LOG_TAG, String.valueOf(calibrate.getStartTimestamp()));
                Log.e(Constants.LOG_TAG, String.valueOf(MINIMUM_SLEEP_CALIBRATE_TIME));

                return -2;
            }

            //Everything went ok, write the wake up time

            //Get writable database
            ContentValues values = new ContentValues();

            values.put(CalibrateSleep.COLUMN_END_TIMESTAMP, System.currentTimeMillis());

            db.update(CalibrateSleep.TABLE_NAME, values, CalibrateSleep.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(calibrate.getId())});

            db.close();

            return 1;

        }
    }

    public static int insertWakeUpDebugCalibrate(SQLiteDatabase db) {

        if(calibrateSleepExists(db) == 0){
            return -1;
        } else {

            CalibrateSleep calibrate = getCalibrate(db);

            if(calibrate.getStartTimestamp() == 0){
                return -1;
            }

            //Everything went ok, write the wake up time

            //Get writable database
            ContentValues values = new ContentValues();

            long wakeupTime = calibrate.getStartTimestamp() + MINIMUM_SLEEP_CALIBRATE_TIME + 1;

            values.put(CalibrateSleep.COLUMN_END_TIMESTAMP, wakeupTime);

            db.update(CalibrateSleep.TABLE_NAME, values, CalibrateSleep.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(calibrate.getId())});

            db.close();

            return 1;

        }
    }

    public static void undoSleepCalibrate(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(CalibrateSleep.COLUMN_START_TIMESTAMP, 0);

        db.update(CalibrateSleep.TABLE_NAME, values, CalibrateSleep.COLUMN_ID + " = ?",
                new String[]{String.valueOf(1)});
    }

    public static CalibrateSleep getCalibrate(SQLiteDatabase db) {

        //There is only going to be one register on this table

        Cursor cursor = db.query(CalibrateSleep.TABLE_NAME,
                new String[]{CalibrateSleep.COLUMN_ID, CalibrateSleep.COLUMN_START_TIMESTAMP,
                        CalibrateSleep.COLUMN_END_TIMESTAMP},
                CalibrateSleep.COLUMN_START_TIMESTAMP + ">= ?",
                new String[]{String.valueOf(0)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        CalibrateSleep note = new CalibrateSleep(
                cursor.getInt(cursor.getColumnIndex(CalibrateSleep.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(CalibrateSleep.COLUMN_START_TIMESTAMP)),
                cursor.getLong(cursor.getColumnIndex(CalibrateSleep.COLUMN_END_TIMESTAMP)));

        cursor.close();

        return note;
    }

    public static int calibrateSleepExists(SQLiteDatabase db){
        int result = 0;

        String countQuery = String.format("SELECT * FROM %s", CalibrateSleep.TABLE_NAME);

        Cursor cursor = db.rawQuery(countQuery, null);

        //Calibrate does not exist
        if(cursor.getCount() == 0){
            return result;
        }

        CalibrateSleep calibrate = getCalibrate(db);

        //Calibrate sleep
        if(calibrate.getStartTimestamp() > 0){
            result=1;
        }

        //Calibrate wake up
        if(calibrate.getEndTimestamp() > 0){
            result+=2;
        }

        return result;
    }

    public static void deleteCalibrateSleep(SQLiteDatabase db){
        db.delete(CalibrateSleep.TABLE_NAME, null, null);
    }
}
