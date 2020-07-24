package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rarawa.tfm.sqlite.models.Pattern;
import com.rarawa.tfm.utils.Constants;

public class PatternsHandler extends SQLiteOpenHelper {
    public PatternsHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Pattern.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + Patterns.TABLE_NAME);
        //onCreate(db);
    }

    public long insertPattern(int id, String name, String intensities, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        values.put(Pattern.COLUMN_ID, id);
        values.put(Pattern.COLUMN_NAME, name);
        values.put(Pattern.COLUMN_INTENSITIES, intensities);

        //Insert new register
        db.insert(Pattern.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public Pattern getPattern(int id, SQLiteDatabase db) {

        Cursor cursor = db.query(Pattern.TABLE_NAME,
                new String[]{Pattern.COLUMN_ID, Pattern.COLUMN_NAME, Pattern.COLUMN_INTENSITIES},
                Pattern.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        Pattern note = new Pattern(
                cursor.getInt(cursor.getColumnIndex(Pattern.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Pattern.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Pattern.COLUMN_INTENSITIES)));

        cursor.close();

        return note;
    }

    public String getRandomOrderPatterns(SQLiteDatabase db) {

        Cursor cursor = db.query(Pattern.TABLE_NAME,
                new String[]{Pattern.COLUMN_ID, Pattern.COLUMN_NAME, Pattern.COLUMN_INTENSITIES},
                null,
                null,
                null, null, "RANDOM()", null);

        if (cursor == null)
            return null;

        if (cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        String result = "";

        int i = 0;

        while (cursor.moveToNext()) {
            result = result.concat(
                    String.valueOf(cursor.getInt(cursor.getColumnIndex(Pattern.COLUMN_ID))));

            result = result.concat(",");
        }


        cursor.close();

        if (result.contains(",")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public String getRandomOrderPatternsByAngerLevel(int angerLevel, SQLiteDatabase db) {

        Cursor cursor = db.query(Pattern.TABLE_NAME,
                new String[]{Pattern.COLUMN_ID, Pattern.COLUMN_NAME, Pattern.COLUMN_INTENSITIES},
                Pattern.COLUMN_INTENSITIES + " LIKE ?",
                new String[]{"%"+String.valueOf(1)+"%"},//DEBUG
                null, null, "RANDOM()", null);

        if (cursor == null)
            return null;

        if(cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        String result = "";

        int i=0;

        while(cursor.moveToNext()){
            result = result.concat(
                    String.valueOf(cursor.getInt(cursor.getColumnIndex(Pattern.COLUMN_ID))));

            result = result.concat(",");
        }


        cursor.close();

        if(result.contains(",")){
            result = result.substring(0, result.length() -1);
        }

        return result;
    }

    public boolean patternExists(int id, SQLiteDatabase db){
        Cursor cursor = db.query(Pattern.TABLE_NAME,
                new String[]{Pattern.COLUMN_ID, Pattern.COLUMN_NAME, Pattern.COLUMN_INTENSITIES},
                Pattern.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        boolean result = cursor != null;
        cursor.close();

        return result;
    }

    //Update pattern by id
    public void updatePattern(Pattern pattern, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        if(pattern.getName().length() > 0) {
            values.put(Pattern.COLUMN_NAME, pattern.getName());
        }

        if(pattern.getIntensities().length() > 0) {
            values.put(Pattern.COLUMN_INTENSITIES, pattern.getIntensities());
        }

        db.update(Pattern.TABLE_NAME, values, Pattern.COLUMN_ID + " = ?",
                new String[]{String.valueOf(pattern.getId())});
    }

    public void deletePattern(int id, SQLiteDatabase db){

        db.delete(Pattern.TABLE_NAME, Pattern.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
    }

}
