package com.rarawa.tfm.sqlite.controllers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.sqlite.models.ReasonAnger;
import com.rarawa.tfm.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryHandler extends SQLiteOpenHelper {
    public HistoryHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //Nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing
    }

    public HashMap<Long, Integer> getNumberEpisodesPerDay(long from, long to, SQLiteDatabase db){

        String query = String.format("SELECT %s FROM %s WHERE %s IN (SELECT DISTINCT %s FROM %s) " +
                        "AND %s BETWEEN %d AND %d ORDER BY %s ASC;",
                AngerLevel.COLUMN_TIMESTAMP, AngerLevel.TABLE_NAME, AngerLevel.COLUMN_ID,
                ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL, ReasonAnger.TABLE_NAME,
                AngerLevel.COLUMN_TIMESTAMP, from, to,
                AngerLevel.COLUMN_TIMESTAMP);

        Log.d(Constants.LOG_TAG, "Query:\n" + query);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        HashMap<Long, Integer> result = new HashMap<Long, Integer>();

        while(cursor.moveToNext()) {
            Calendar currentCalendar = Calendar.getInstance();
            long currentTimestamp = cursor.getInt(0);

            currentCalendar.setTimeInMillis(currentTimestamp * 1000);

            //Get rid of the part of the timestamp belonging to the time
            long currentValueDayTimestamp = currentTimestamp -
                    (currentCalendar.get(Calendar.HOUR_OF_DAY)*3600 +
                            currentCalendar.get(Calendar.MINUTE)*60 +
                            currentCalendar.get(Calendar.SECOND));

            if (result.containsKey(currentValueDayTimestamp)){
                int numberEpisodes = result.get(currentValueDayTimestamp);
                numberEpisodes++;

                result.remove(currentValueDayTimestamp);
                result.put(currentValueDayTimestamp, numberEpisodes);
            } else{
                result.put(currentValueDayTimestamp, 1);
            }
        }

        Log.d(Constants.LOG_TAG, "getNumberEpisodesPerDay size: " + result.size());

        return result;
    }


}
