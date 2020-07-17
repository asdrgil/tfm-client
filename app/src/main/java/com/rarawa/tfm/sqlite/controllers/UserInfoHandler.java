package com.rarawa.tfm.sqlite.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.UserInfo;
import com.rarawa.tfm.utils.Constants;

public class UserInfoHandler extends SQLiteOpenHelper {
    public UserInfoHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserInfo.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME);
        //onCreate(db);
    }

    public long insertUserInfo(String name, String surname1, String surname2, int age,
                               String gender, String communicationToken, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        values.put(UserInfo.COLUMN_NAME, name);
        values.put(UserInfo.COLUMN_SURNAME1, surname1);
        values.put(UserInfo.COLUMN_SURNAME2, surname2);
        values.put(UserInfo.COLUMN_AGE, age);
        values.put(UserInfo.COLUMN_GENDER, gender);
        values.put(UserInfo.COLUMN_COMMUNICATION_TOKEN, communicationToken);
        values.put(UserInfo.COLUMN_REGISTER_TIMESTAMP, System.currentTimeMillis());

        //Insert new register
        long id = db.insert(UserInfo.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public UserInfo getUserInfo(SQLiteDatabase db) {

        //There is only going to be one register on this table
        int id = 1;

        Cursor cursor = db.query(UserInfo.TABLE_NAME,
                new String[]{UserInfo.COLUMN_ID, UserInfo.COLUMN_NAME, UserInfo.COLUMN_SURNAME1,
                        UserInfo.COLUMN_SURNAME2, UserInfo.COLUMN_AGE, UserInfo.COLUMN_GENDER,
                        UserInfo.COLUMN_COMMUNICATION_TOKEN, UserInfo.COLUMN_REGISTER_TIMESTAMP},
                UserInfo.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        UserInfo note = new UserInfo(
                cursor.getInt(cursor.getColumnIndex(UserInfo.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(UserInfo.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(UserInfo.COLUMN_SURNAME1)),
                cursor.getString(cursor.getColumnIndex(UserInfo.COLUMN_SURNAME2)),
                cursor.getInt(cursor.getColumnIndex(UserInfo.COLUMN_AGE)),
                cursor.getString(cursor.getColumnIndex(UserInfo.COLUMN_GENDER)),
                cursor.getString(cursor.getColumnIndex(UserInfo.COLUMN_COMMUNICATION_TOKEN)),
                cursor.getLong(cursor.getColumnIndex(UserInfo.COLUMN_REGISTER_TIMESTAMP)));

        cursor.close();

        return note;
    }

    public boolean userInfoExists(SQLiteDatabase db){
        String countQuery = String.format("SELECT * FROM %s", UserInfo.TABLE_NAME);

        Cursor cursor = db.rawQuery(countQuery, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();

        return result;
    }

    public void updateUserInfo(UserInfo userInfo, SQLiteDatabase db) {

        ContentValues values = new ContentValues();

        if(userInfo.getName().length() > 0) {
            values.put(UserInfo.COLUMN_NAME, userInfo.getName());
        }

        if(userInfo.getSurname1().length() > 0) {
            values.put(UserInfo.COLUMN_SURNAME1, userInfo.getSurname1());
        }

        if(userInfo.getSurname2().length() > 0) {
            values.put(UserInfo.COLUMN_SURNAME2, userInfo.getSurname2());
        }

        if(userInfo.getAge() > 0) {
            values.put(UserInfo.COLUMN_AGE, userInfo.getAge());
        }

        if(userInfo.getGender().length() > 0) {
            values.put(UserInfo.COLUMN_GENDER, userInfo.getGender());
        }

        if(userInfo.getCommunicationToken().length() > 0) {
            values.put(UserInfo.COLUMN_COMMUNICATION_TOKEN, userInfo.getCommunicationToken());
        }

        db.update(UserInfo.TABLE_NAME, values, UserInfo.COLUMN_ID + " = ?",
                new String[]{String.valueOf(1)});
    }

    public void deleteUserInfo(SQLiteDatabase db){

        db.delete(UserInfo.TABLE_NAME, null, null);

        db.close();
    }

}
