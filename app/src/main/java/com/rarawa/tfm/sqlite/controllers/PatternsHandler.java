package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.Patterns;
import com.rarawa.tfm.utils.Constants;

public class PatternsHandler extends SQLiteOpenHelper {
    public PatternsHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Patterns.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + Patterns.TABLE_NAME);
        //onCreate(db);
    }

    public long insertPattern(int id, String name, String intensities, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        values.put(Patterns.COLUMN_ID, id);
        values.put(Patterns.COLUMN_NAME, name);
        values.put(Patterns.COLUMN_INTENSITIES, intensities);

        //Insert new register
        db.insert(Patterns.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public Patterns getPattern(int id, SQLiteDatabase db) {

        Cursor cursor = db.query(Patterns.TABLE_NAME,
                new String[]{Patterns.COLUMN_ID, Patterns.COLUMN_NAME, Patterns.COLUMN_INTENSITIES},
                Patterns.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        Patterns note = new Patterns(
                cursor.getInt(cursor.getColumnIndex(Patterns.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Patterns.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Patterns.COLUMN_INTENSITIES)));

        cursor.close();

        return note;
    }

    public Patterns getRandomPatternByAngerLevel(int angerLevel, SQLiteDatabase db) {

        Cursor cursor = db.query(Patterns.TABLE_NAME,
                new String[]{Patterns.COLUMN_ID, Patterns.COLUMN_NAME, Patterns.COLUMN_INTENSITIES},
                Patterns.COLUMN_INTENSITIES + " LIKE ?",
                new String[]{"%"+String.valueOf(1)+"%"},//DEBUG
                null, null, "RANDOM()", "1");//RANDOM()

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        Patterns note;

        try{
            note = new Patterns(
                    cursor.getInt(cursor.getColumnIndex(Patterns.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Patterns.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(Patterns.COLUMN_INTENSITIES)));
        } catch (CursorIndexOutOfBoundsException e){
            return null;
        }



        cursor.close();

        return note;
    }

    public boolean patternExists(int id, SQLiteDatabase db){
        Cursor cursor = db.query(Patterns.TABLE_NAME,
                new String[]{Patterns.COLUMN_ID, Patterns.COLUMN_NAME, Patterns.COLUMN_INTENSITIES},
                Patterns.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        boolean result = cursor != null;
        cursor.close();

        return result;
    }

    //Update pattern by id
    public void updatePattern(Patterns pattern, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        if(pattern.getName().length() > 0) {
            values.put(Patterns.COLUMN_NAME, pattern.getName());
        }

        if(pattern.getIntensities().length() > 0) {
            values.put(Patterns.COLUMN_INTENSITIES, pattern.getIntensities());
        }

        db.update(Patterns.TABLE_NAME, values, Patterns.COLUMN_ID + " = ?",
                new String[]{String.valueOf(pattern.getId())});
    }

    public void deletePattern(int id, SQLiteDatabase db){

        db.delete(Patterns.TABLE_NAME, Patterns.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
    }

}
