package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.Constants;

public class AngerLevelHandler extends SQLiteOpenHelper {
    public AngerLevelHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AngerLevel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + AngerLevel.TABLE_NAME);
        //onCreate(db);
    }

    public long insertAngerLevel(long timestamp, int angerLevel, SQLiteDatabase db){

        ContentValues values = new ContentValues();

        values.put(AngerLevel.COLUMN_TIMESTAMP, timestamp);
        values.put(AngerLevel.COLUMN_ANGER_VAL, angerLevel);

        //Insert new register
        long result = db.insert(AngerLevel.TABLE_NAME, null, values);

        db.close();

        return result;

    }

    public AngerLevel getAngerLevelById(int id, SQLiteDatabase db){
        Cursor cursor = db.query(AngerLevel.TABLE_NAME,
                new String[]{AngerLevel.COLUMN_ID, AngerLevel.COLUMN_TIMESTAMP,
                        AngerLevel.COLUMN_ANGER_VAL},
                AngerLevel.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        AngerLevel note = new AngerLevel(
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_VAL)));

        cursor.close();

        return note;
    }

    public AngerLevel getAngerLevelByTimestamp(long timestamp, SQLiteDatabase db){
        Cursor cursor = db.query(AngerLevel.TABLE_NAME,
                new String[]{AngerLevel.COLUMN_ID, AngerLevel.COLUMN_TIMESTAMP,
                        AngerLevel.COLUMN_ANGER_VAL},
                AngerLevel.COLUMN_TIMESTAMP + "=?",
                new String[]{String.valueOf(timestamp)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        AngerLevel note = new AngerLevel(
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_VAL)));

        cursor.close();

        return note;
    }

    public AngerLevel getLastAngerLevel(SQLiteDatabase db){
        Cursor cursor = db.query(AngerLevel.TABLE_NAME,
                new String[]{AngerLevel.COLUMN_ID, AngerLevel.COLUMN_TIMESTAMP,
                        AngerLevel.COLUMN_ANGER_VAL},
                null, null, null, null,
                AngerLevel.COLUMN_TIMESTAMP + " DESC");

        if (cursor != null) {
            Log.d(Constants.LOG_TAG, "Cursor is NOT null");
            cursor.moveToFirst();
        }else {
            Log.d(Constants.LOG_TAG, "Cursor is null");
            return null;
        }

        AngerLevel note = new AngerLevel(
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_VAL)));

        cursor.close();

        return note;
    }

    public AngerLevel getPenultimateAngerLevel(SQLiteDatabase db){
        Cursor cursor = db.query(AngerLevel.TABLE_NAME,
                new String[]{AngerLevel.COLUMN_ID, AngerLevel.COLUMN_TIMESTAMP,
                        AngerLevel.COLUMN_ANGER_VAL},
                null, null, null, null,
                AngerLevel.COLUMN_TIMESTAMP + " DESC LIMIT 1,1");

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        AngerLevel note = new AngerLevel(
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_VAL)));

        cursor.close();

        return note;
    }

}
