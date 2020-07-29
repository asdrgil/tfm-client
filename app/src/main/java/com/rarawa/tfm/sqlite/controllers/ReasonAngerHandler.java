package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.DisplayedPattern;
import com.rarawa.tfm.sqlite.models.ReasonAnger;
import com.rarawa.tfm.utils.Constants;

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

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        ReasonAnger note = new ReasonAnger(
                cursor.getInt(cursor.getColumnIndex(ReasonAnger.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL)),
                cursor.getInt(cursor.getColumnIndex(ReasonAnger.COLUMN_REASON_ANGER))
        );

        cursor.close();

        return note;
    }

}
