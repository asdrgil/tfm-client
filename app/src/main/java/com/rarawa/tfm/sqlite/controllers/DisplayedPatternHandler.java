package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.sqlite.models.DisplayedPattern;
import com.rarawa.tfm.sqlite.models.Pattern;
import com.rarawa.tfm.sqlite.models.ReasonAnger;
import com.rarawa.tfm.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;

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

    public HashMap<Integer, String> getTopUsefulPatterns(long from, long to, int limit, SQLiteDatabase db){

        String query = String.format("SELECT T2.%s, SUM(T1.%) " +
                        "FROM %s T1 " +
                        "INNER JOIN %s T2 ON  T1.patternId=T2.%s " +
                        "INNER JOIN %s T3 ON  T1.%s=T3.%s " +
                        "WHERE T3.%s BETWEEN %d AND %d " +
                        "GROUP BY T1.%s " +
                        "ORDER BY SUM(T1.%s) DESC, T2.%s ASC " +
                        "LIMIT %d; ",
                Pattern.COLUMN_NAME, DisplayedPattern.COLUMN_STATUS,
                DisplayedPattern.TABLE_NAME,
                Pattern.TABLE_NAME, DisplayedPattern.COLUMN_PATTERN_ID, Pattern.TABLE_NAME,
                AngerLevel.TABLE_NAME, DisplayedPattern.COLUMN_ANGER_LEVEL_ID, Pattern.COLUMN_ID,
                AngerLevel.COLUMN_TIMESTAMP, from, to,
                DisplayedPattern.COLUMN_PATTERN_ID,
                DisplayedPattern.COLUMN_STATUS, Pattern.COLUMN_ID,
                limit);

        //Log.d(LOG_TAG, "query:\n" + query);

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        HashMap<Integer, String> result = new HashMap<>();

        for(int i = 0; i < count; i++) {
            cursor.moveToNext();

            String patternStr = cursor.getString(0);
            int numberCoincidences = cursor.getInt(1);

            result.put(i, String.format("%s|%d",patternStr, numberCoincidences));
        }

        return result;

    }

    public JSONObject getUnsyncedPatterns(SQLiteDatabase db){

        JSONObject result = new JSONObject();

        String query = String.format("SELECT T1.%s, T1.%s, T1.%s," +
                        "T2.%s, T2.%s " +
                        "FROM %s T1 " +
                        "INNER JOIN %s T2 ON T1.%s=T2.%s " +
                        "WHERE T1.%s=0 AND T2.%s > 0 " +
                        "ORDER BY T2.%s, T1.%s, T1.%s ASC;",
                DisplayedPattern.COLUMN_PATTERN_ID, DisplayedPattern.COLUMN_STATUS, DisplayedPattern.COLUMN_COMMENTS,
                AngerLevel.COLUMN_ANGER_VAL, AngerLevel.COLUMN_TIMESTAMP,
                DisplayedPattern.TABLE_NAME,
                AngerLevel.TABLE_NAME, AngerLevel.COLUMN_ID, DisplayedPattern.COLUMN_ID,
                DisplayedPattern.COLUMN_SYNCED, AngerLevel.COLUMN_ANGER_VAL,
                AngerLevel.COLUMN_TIMESTAMP, DisplayedPattern.COLUMN_ID, DisplayedPattern.COLUMN_STATUS);

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();



        for(int i = 0; i < count; i++) {
            cursor.moveToNext();

            JSONObject element = new JSONObject();
            try {

                element.put("level",
                        String.valueOf(
                                cursor.getInt(cursor.getColumnIndex(AngerLevel.COLUMN_ANGER_VAL))));

                element.put("timestamp",
                        String.valueOf(
                                cursor.getLong(cursor.getColumnIndex(AngerLevel.COLUMN_TIMESTAMP))));



                element.put("pattern",
                        String.valueOf(
                                cursor.getInt(cursor.getColumnIndex(DisplayedPattern.COLUMN_PATTERN_ID))));

                element.put("status",
                        String.valueOf(
                                cursor.getInt(cursor.getColumnIndex(DisplayedPattern.COLUMN_STATUS))));

                String comment =
                        cursor.getString(cursor.getColumnIndex(DisplayedPattern.COLUMN_COMMENTS));

                if(comment.length() > 0) {
                    element.put(DisplayedPattern.COLUMN_COMMENTS, comment);
                }

                result.put(String.valueOf(i), element);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        ContentValues values = new ContentValues();
        values.put(DisplayedPattern.COLUMN_SYNCED, 1);

        db.update(DisplayedPattern.TABLE_NAME, values, DisplayedPattern.COLUMN_SYNCED + " = ?",
                new String[]{String.valueOf(0)});

        db.close();

        return result;

    }

}
