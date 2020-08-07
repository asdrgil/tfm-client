package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.sqlite.models.DisplayedPattern;
import com.rarawa.tfm.sqlite.models.ReasonAnger;
import com.rarawa.tfm.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;

public class ReasonAngerHandler extends SQLiteOpenHelper {
    public ReasonAngerHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ReasonAnger.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + ReasonAnger.TABLE_NAME);
        //onCreate(db);
    }

    public long insertReasonAnger(int idFirstAngerLevel, int reasonAnger, SQLiteDatabase db){

        ContentValues values = new ContentValues();

        values.put(ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL, idFirstAngerLevel);
        values.put(ReasonAnger.COLUMN_REASON_ANGER, reasonAnger);
        //Insert new register
        long result = db.insert(ReasonAnger.TABLE_NAME, null, values);

        db.close();

        return result;
    }

    public long insertReasonAngerFull(int idFirstAngerLevel, int idLastAngerLevel, int reasonAnger, SQLiteDatabase db){

        ContentValues values = new ContentValues();

        values.put(ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL, idFirstAngerLevel);
        values.put(ReasonAnger.COLUMN_ID_LAST_ANGER_LEVEL, idLastAngerLevel);
        values.put(ReasonAnger.COLUMN_REASON_ANGER, reasonAnger);
        //Insert new register
        long result = db.insert(ReasonAnger.TABLE_NAME, null, values);

        db.close();

        return result;
    }

    public void updateReasonAnger(int idFirstAngerLevel, int idLastAngerLevel, SQLiteDatabase db){
        ContentValues values = new ContentValues();

        values.put(ReasonAnger.COLUMN_ID_LAST_ANGER_LEVEL, idLastAngerLevel);

        db.update(ReasonAnger.TABLE_NAME, values, ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL + " = ?",
                new String[]{String.valueOf(idFirstAngerLevel)});

        db.close();


    }

    public ReasonAnger getReasonAnger(int idFirstAngerLevel, SQLiteDatabase db){
        Cursor cursor = db.query(ReasonAnger.TABLE_NAME,
                new String[]{ReasonAnger.COLUMN_ID,
                        ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL},
                ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL + "=?",
                new String[]{String.valueOf(idFirstAngerLevel)},
                null, null, null, null);

        /*
        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;
        */

        ReasonAnger note = new ReasonAnger(
                cursor.getInt(cursor.getColumnIndex(ReasonAnger.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL)),
                cursor.getInt(cursor.getColumnIndex(ReasonAnger.COLUMN_REASON_ANGER))
        );

        cursor.close();

        return note;
    }

    public JSONObject getUnsyncedReasonAnger(SQLiteDatabase db){

        JSONObject result = new JSONObject();

        String query = String.format("SELECT T1.%s, T1.%s, " +
                        "T21.%s AS tmpIni, T22.%s AS tmpEnd " +
                        "FROM %s T1 " +
                        "INNER JOIN %s T21 ON T1.%s=T21.%s " +
                        "INNER JOIN %s T22 ON T1.%s=T22.%s " +
                        "WHERE T1.%s=0 " +
                        "ORDER BY T1.%s ASC;",
                ReasonAnger.COLUMN_ID, ReasonAnger.COLUMN_REASON_ANGER,
                AngerLevel.COLUMN_TIMESTAMP, AngerLevel.COLUMN_TIMESTAMP,
                ReasonAnger.TABLE_NAME,
                AngerLevel.TABLE_NAME, ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL, AngerLevel.COLUMN_ID,
                AngerLevel.TABLE_NAME, ReasonAnger.COLUMN_ID_LAST_ANGER_LEVEL, AngerLevel.COLUMN_ID,
                ReasonAnger.COLUMN_SYNCED,
                ReasonAnger.COLUMN_ID);

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        Log.d(LOG_TAG, "getUnsyncedReasonAnger.count: " + count);

        for(int i = 0; i < count; i++) {
            cursor.moveToNext();

            Log.d(LOG_TAG, "i:" + i);

            JSONObject element = new JSONObject();
            try {

                element.put("id", cursor.getInt(0));
                element.put("reasonAnger", cursor.getInt(1));
                element.put("tmpIni", cursor.getInt(2));
                element.put("tmpEnd", cursor.getInt(3));

                result.put(String.valueOf(i), element);

            } catch (JSONException e) {
                Log.d(LOG_TAG, "JSONException");
                e.printStackTrace();
            }

        }

        //Marked registers as synced with the server

        ContentValues values = new ContentValues();
        values.put(ReasonAnger.COLUMN_SYNCED, 1);

        db.update(ReasonAnger.TABLE_NAME, values, ReasonAnger.COLUMN_SYNCED + " = ?",
                new String[]{String.valueOf(0)});

        db.close();

        return result;
    }

}
