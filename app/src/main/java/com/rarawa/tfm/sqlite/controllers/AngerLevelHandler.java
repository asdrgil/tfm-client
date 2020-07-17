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
        return insertAngerLevel(timestamp, angerLevel, 0, 0, "", db);
    }

    public long insertAngerLevel(long timestamp, int angerLevel, int suggestedPattern,
            int usefulnessPattern, String commentPattern, SQLiteDatabase db){

        ContentValues values = new ContentValues();

        values.put(AngerLevel.COLUMN_TIMESTAMP, timestamp);
        values.put(AngerLevel.COLUMN_ANGER_LEVEL, angerLevel);
        values.put(AngerLevel.COLUMN_SUGGESTED_PATTERN, suggestedPattern);
        values.put(AngerLevel.COLUMN_USEFULNESS_PATTERN, usefulnessPattern);
        values.put(AngerLevel.COLUMN_COMMENT_PATTERN, commentPattern);

        //Insert new register
        long result = db.insert(AngerLevel.TABLE_NAME, null, values);

        db.close();

        return result;

    }

    public AngerLevel getAngerLevel(long timestamp, SQLiteDatabase db){
        Cursor cursor = db.query(AngerLevel.TABLE_NAME,
                new String[]{AngerLevel.COLUMN_ID, AngerLevel.COLUMN_TIMESTAMP,
                        AngerLevel.COLUMN_ANGER_LEVEL, AngerLevel.COLUMN_SUGGESTED_PATTERN,
                        AngerLevel.COLUMN_USEFULNESS_PATTERN, AngerLevel.COLUMN_COMMENT_PATTERN},
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
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_LEVEL)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_SUGGESTED_PATTERN)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_USEFULNESS_PATTERN)),
                cursor.getString(cursor.getColumnIndex(AngerLevel.COLUMN_SUGGESTED_PATTERN)));

        cursor.close();

        return note;
    }

    public AngerLevel getLastAngerLevel(SQLiteDatabase db){
        Cursor cursor = db.query(AngerLevel.TABLE_NAME,
                new String[]{AngerLevel.COLUMN_ID, AngerLevel.COLUMN_TIMESTAMP,
                        AngerLevel.COLUMN_ANGER_LEVEL, AngerLevel.COLUMN_SUGGESTED_PATTERN,
                        AngerLevel.COLUMN_USEFULNESS_PATTERN, AngerLevel.COLUMN_COMMENT_PATTERN},
                null, null, null, null,
                AngerLevel.COLUMN_TIMESTAMP + " DESC");

        if (cursor != null) {
            Log.d(Constants.LOG_TAG, "Cursor is NOT null");
            cursor.moveToFirst();
        }else {
            Log.d(Constants.LOG_TAG, "Cursor is null");
            return null;
        }

        //long tmp = cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP));

        AngerLevel note = new AngerLevel(
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_LEVEL)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_SUGGESTED_PATTERN)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_USEFULNESS_PATTERN)),
                cursor.getString(cursor.getColumnIndex(AngerLevel.COLUMN_COMMENT_PATTERN)));
                //"");

        cursor.close();

        return note;
    }

    //TODO: test
    public AngerLevel getPenultimateAngerLevel(SQLiteDatabase db){
        Cursor cursor = db.query(AngerLevel.TABLE_NAME,
                new String[]{AngerLevel.COLUMN_ID, AngerLevel.COLUMN_TIMESTAMP,
                        AngerLevel.COLUMN_ANGER_LEVEL, AngerLevel.COLUMN_SUGGESTED_PATTERN,
                        AngerLevel.COLUMN_USEFULNESS_PATTERN, AngerLevel.COLUMN_COMMENT_PATTERN},
                null, null, null, null,
                AngerLevel.COLUMN_TIMESTAMP + " DESC LIMIT 1,1");

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        AngerLevel note = new AngerLevel(
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_LEVEL)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_SUGGESTED_PATTERN)),
                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_USEFULNESS_PATTERN)),
                cursor.getString(cursor.getColumnIndex(AngerLevel.COLUMN_SUGGESTED_PATTERN)));

        cursor.close();

        return note;
    }

    //Update angerLevel information
    public void updateAngerLevel(AngerLevel angerLevel, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        if(angerLevel.getAngerLevel() >= 0 && angerLevel.getAngerLevel() <= 4){
            values.put(AngerLevel.COLUMN_ANGER_LEVEL, angerLevel.getAngerLevel());
        }

        if(angerLevel.getSuggestedPattern() > 0){
            values.put(AngerLevel.COLUMN_SUGGESTED_PATTERN, angerLevel.getSuggestedPattern());
        }

        if(angerLevel.getUsefulnessPattern() != 0){
            values.put(AngerLevel.COLUMN_USEFULNESS_PATTERN, angerLevel.getUsefulnessPattern());
        }

        if(angerLevel.getCommentPattern().length() > 0){
            values.put(AngerLevel.COLUMN_COMMENT_PATTERN, angerLevel.getCommentPattern());
        }

        db.update(AngerLevel.TABLE_NAME, values, AngerLevel.COLUMN_TIMESTAMP + " = ?",
                new String[]{String.valueOf(angerLevel.getTimestamp())});
    }

    //TODO
    /*public boolean isOneMinRestAngerLevel(SQLiteDatabase db){
        long currentTimestamp = System.currentTimeMillis();
    }*/
}
