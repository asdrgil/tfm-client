package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.sqlite.models.CalibrateSleep;
import com.rarawa.tfm.sqlite.models.DisplayedPattern;
import com.rarawa.tfm.sqlite.models.UserInfo;
import com.rarawa.tfm.utils.Constants;

public class DisplayedPatternHandler extends SQLiteOpenHelper {
    public DisplayedPatternHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DisplayedPattern.TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + DisplayedPattern.TABLE_NAME);
        //onCreate(db);
    }

    public long insertDisplayedPattern(int angerLevelId, int patternId, SQLiteDatabase db){

        ContentValues values = new ContentValues();

        values.put(DisplayedPattern.COLUMN_ANGER_LEVEL_ID, angerLevelId);
        values.put(DisplayedPattern.COLUMN_PATTERN_ID, patternId);

        //Insert new register
        long result = db.insert(DisplayedPattern.TABLE_NAME, null, values);

        db.close();

        return result;

    }

    public long insertDisplayedPatternDebug(int angerLevelId, int patternId, int status, String comment, SQLiteDatabase db){

        ContentValues values = new ContentValues();

        values.put(DisplayedPattern.COLUMN_ANGER_LEVEL_ID, angerLevelId);
        values.put(DisplayedPattern.COLUMN_PATTERN_ID, patternId);
        values.put(DisplayedPattern.COLUMN_COMMENTS, comment);
        values.put(DisplayedPattern.COLUMN_STATUS, status);

        //Insert new register
        long result = db.insert(DisplayedPattern.TABLE_NAME, null, values);

        db.close();

        return result;

    }

    //TODO: implement it only if it is going to be neccesary
    /*public AngerLevel getDisplayedPatternByAngerLevelId(int angerLevel, SQLiteDatabase db){
    }*/

    public void updateDisplayedPattern(DisplayedPattern displayedPattern, SQLiteDatabase db){
        ContentValues values = new ContentValues();

        if(displayedPattern.getStatus() > -2){
            values.put(DisplayedPattern.COLUMN_STATUS, displayedPattern.getStatus());
        }

        if(displayedPattern.getComments().length() > 0){
            values.put(DisplayedPattern.COLUMN_COMMENTS, displayedPattern.getComments());
        }

        db.update(DisplayedPattern.TABLE_NAME, values, DisplayedPattern.COLUMN_ID + " = ?",
                new String[]{String.valueOf(displayedPattern.getId())});

        db.close();
    }

}
