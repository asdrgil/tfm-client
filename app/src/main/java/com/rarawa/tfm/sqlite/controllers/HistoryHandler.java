package com.rarawa.tfm.sqlite.controllers;

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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;
import static com.rarawa.tfm.utils.Constants.ONE_DAY_TIMESTAMP;

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

        //Log.d(LOG_TAG, "query:\n" + query);

        Cursor cursor = db.rawQuery(query, null);

        int count = cursor.getCount();

        HashMap<Long, Integer> result = new HashMap<>();

        for(int i = 0; i < count; i++){
            cursor.moveToNext();
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

        cursor.close();

        Log.d(LOG_TAG, "getNumberEpisodesPerDay size: " + result.size());

        return result;
    }

    //Returns episodes duration in seconds
    public HashMap<Long, Integer> getEpisodesDurationPerDay(long from, long to, SQLiteDatabase db){

        String query =  String.format("SELECT T11.%s, abs(T11.%s - T12.%s) " +
                "FROM %s T2 " +
                "INNER JOIN %s T11 ON T2.%s = T11.%s " +
                "INNER JOIN %s T12 ON T2.%s = T12.%s " +
                "WHERE T11.%s > %d " +
                "AND T12.%s < %d " +
                "ORDER BY T2.%s ASC;",
                AngerLevel.COLUMN_TIMESTAMP, AngerLevel.COLUMN_TIMESTAMP, AngerLevel.COLUMN_TIMESTAMP,
                ReasonAnger.TABLE_NAME,
                AngerLevel.TABLE_NAME, ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL, AngerLevel.COLUMN_ID,
                AngerLevel.TABLE_NAME, ReasonAnger.COLUMN_ID_LAST_ANGER_LEVEL, AngerLevel.COLUMN_ID,
                AngerLevel.COLUMN_TIMESTAMP, from,
                AngerLevel.COLUMN_TIMESTAMP, to,
                ReasonAnger.COLUMN_ID);

        //Log.d(LOG_TAG, "Query:\n" + query);

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        Log.d(LOG_TAG, "number of registers: " + count);

        HashMap<Long, Integer> result = new HashMap<>();

        for(int i=0; i<count; i++){
            cursor.moveToNext();

            Calendar currentCalendar = Calendar.getInstance();
            long currentTimestamp = cursor.getInt(0);
            int difference = cursor.getInt(1);

            currentCalendar.setTimeInMillis(currentTimestamp * 1000);

            //Get rid of the part of the timestamp belonging to the time
            long currentValueDayTimestamp = currentTimestamp -
                    (currentCalendar.get(Calendar.HOUR_OF_DAY)*3600 +
                            currentCalendar.get(Calendar.MINUTE)*60 +
                            currentCalendar.get(Calendar.SECOND));

            if (result.containsKey(currentValueDayTimestamp)){
                int totalDuration = result.get(currentValueDayTimestamp);
                totalDuration += difference;

                result.remove(currentValueDayTimestamp);
                result.put(currentValueDayTimestamp, totalDuration);
            } else{
                result.put(currentValueDayTimestamp, difference);
            }
        }

        cursor.close();

        Log.d(LOG_TAG, "getNumberEpisodesPerDay size: " + result.size());

        return result;
    }


    //Returns episodes duration in seconds
    public HashMap<Long, Integer> getEpisodesAverageDurationPerDay(long from, long to, SQLiteDatabase db){

        HashMap<Long, Integer> numEpisodesDay = getNumberEpisodesPerDay(from, to, db);
        HashMap<Long, Integer> durationEpisodesDay =getEpisodesDurationPerDay(from, to, db);
        HashMap<Long, Integer> result  = new HashMap<>();

        Iterator it = numEpisodesDay.entrySet().iterator();

        while (it.hasNext()) {
            Log.d(LOG_TAG, "INSIDE ITERATOR");

            Map.Entry element = (Map.Entry) it.next();
            long timestamp = Long.parseLong(element.getKey().toString());
            int numberEpisodesDay = Integer.parseInt(element.getValue().toString());
            int durationEpisodes = durationEpisodesDay.get(timestamp);
            result.put(timestamp, durationEpisodes / numberEpisodesDay);
        }

        Log.d(LOG_TAG, "result.size(): " + result.size());

        return result;
    }

    public HashMap<Integer, Integer> getTotalEpisodesTodayYesterday(SQLiteDatabase db){

        long currentTimestamp = System.currentTimeMillis()/1000;
        long yesterdayTimestamp = currentTimestamp - ONE_DAY_TIMESTAMP;
        long yesterday2Timestamp = currentTimestamp - ONE_DAY_TIMESTAMP*2;

        HashMap<Integer, Integer> result = new HashMap<>();

        int res0 = getNumberEpisodesPerDayNoCuts(currentTimestamp, currentTimestamp, db);
        int res1= getNumberEpisodesPerDayNoCuts(yesterday2Timestamp, yesterdayTimestamp, db);

        result.put(0, res0);
        result.put(1, res1);

        return result;
    }

    //Same as getNumberEpisodesPerDay but with customized time window
    public int getNumberEpisodesPerDayNoCuts(long from, long to, SQLiteDatabase db){

        String query = String.format("SELECT %s FROM %s WHERE %s IN (SELECT DISTINCT %s FROM %s) " +
                        "AND %s BETWEEN %d AND %d ORDER BY %s ASC;",
                AngerLevel.COLUMN_TIMESTAMP, AngerLevel.TABLE_NAME, AngerLevel.COLUMN_ID,
                ReasonAnger.COLUMN_ID_FIRST_ANGER_LEVEL, ReasonAnger.TABLE_NAME,
                AngerLevel.COLUMN_TIMESTAMP, from, to,
                AngerLevel.COLUMN_TIMESTAMP);

        //Log.d(LOG_TAG, "query:\n" + query);

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

}
