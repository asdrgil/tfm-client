package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.CalibrateExercise;
import com.rarawa.tfm.utils.Constants;

import static com.rarawa.tfm.utils.Constants.MINIMUM_EXERCISE_CALIBRATE_TIME;

public class CalibrateExerciseHandler extends SQLiteOpenHelper {
    public CalibrateExerciseHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(Exercise.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + Exercise.TABLE_NAME);
        //onCreate(db);
    }

    public static void insertStartExercise(SQLiteDatabase db) {
        //There can only be one register on this table
        /*if(exerciseExists(db) > 0){
            Log.e(Constants.LOG_TAG, "exerciseExists: 1");
            deleteExercise(db);
        }*/

        deleteExercise(db);

        //Get writable database
        ContentValues values = new ContentValues();
        values.put(CalibrateExercise.COLUMN_ID, 1);
        values.put(CalibrateExercise.COLUMN_START_TIMESTAMP, System.currentTimeMillis());

        Log.e(Constants.LOG_TAG, String.valueOf(System.currentTimeMillis()));

        //Insert new register
        db.insert(CalibrateExercise.TABLE_NAME, null, values);
        db.close();
    }

    public static int insertEndExercise(SQLiteDatabase db) {

        if(calibrateExerciseExists(db) == 0){
            Log.e(Constants.LOG_TAG, "Exercise exists: -1");
            return -1;
        } else {

            CalibrateExercise exercise = getExercise(db);

            if(exercise.getStartTimestamp() == 0){
                Log.e(Constants.LOG_TAG, "Exercise exists; startTmp: -1");
                return -1;
            }

            //This is not neccesary as it is only called when this condition is met.

            //It is required at least MINIMUM_EXERCISE_CALIBRATE_TIME between the start time and
            // the end time in order to calibrate the wristband.

            /*long currentTimeMilis = System.currentTimeMillis();

            if(currentTimeMilis - exercise.getStartTimestamp() < MINIMUM_EXERCISE_CALIBRATE_TIME){
                Log.e(Constants.LOG_TAG, "Exercise exists: -2");
                Log.e(Constants.LOG_TAG, String.valueOf(currentTimeMilis));
                Log.e(Constants.LOG_TAG, String.valueOf(currentTimeMilis - exercise.getStartTimestamp()));
                Log.e(Constants.LOG_TAG, String.valueOf(MINIMUM_EXERCISE_CALIBRATE_TIME));

                return -2;
            }*/

            //Everything went ok, write the end time

            //Get writable database
            ContentValues values = new ContentValues();

            values.put(CalibrateExercise.COLUMN_END_TIMESTAMP, System.currentTimeMillis());

            db.update(CalibrateExercise.TABLE_NAME, values, CalibrateExercise.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(exercise.getId())});

            db.close();

            Log.e(Constants.LOG_TAG, "Exercise exists: 1");

            return 1;

        }
    }

    public static void undoStartExercise(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(CalibrateExercise.COLUMN_START_TIMESTAMP, 0);

        db.update(CalibrateExercise.TABLE_NAME, values, CalibrateExercise.COLUMN_ID + " = ?",
                new String[]{String.valueOf(1)});
    }

    public static CalibrateExercise getExercise(SQLiteDatabase db) {

        //There is only going to be one register on this table

        Cursor cursor = db.query(CalibrateExercise.TABLE_NAME,
                new String[]{CalibrateExercise.COLUMN_ID, CalibrateExercise.COLUMN_START_TIMESTAMP,
                        CalibrateExercise.COLUMN_END_TIMESTAMP},
                CalibrateExercise.COLUMN_START_TIMESTAMP + ">= ?",
                new String[]{String.valueOf(1)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        CalibrateExercise note = new CalibrateExercise(
                cursor.getInt(cursor.getColumnIndex(CalibrateExercise.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(CalibrateExercise.COLUMN_START_TIMESTAMP)),
                cursor.getLong(cursor.getColumnIndex(CalibrateExercise.COLUMN_END_TIMESTAMP)));

        cursor.close();

        return note;
    }

    public static int calibrateExerciseExists(SQLiteDatabase db){
        int result = 0;

        String countQuery = String.format("SELECT * FROM %s", CalibrateExercise.TABLE_NAME);

        Cursor cursor = db.rawQuery(countQuery, null);

        //Calibrate does not exist
        if(cursor.getCount() == 0){
            return result;
        }

        CalibrateExercise exercise = getExercise(db);

        //Calibrate sleep
        if(exercise.getStartTimestamp() > 0){
            result=1;
        }

        //Calibrate wake up
        if(exercise.getEndTimestamp() > 0){
            result+=2;
        }

        return result;
    }

    public static void deleteExercise(SQLiteDatabase db){
        db.delete(CalibrateExercise.TABLE_NAME, null, null);
    }
}
