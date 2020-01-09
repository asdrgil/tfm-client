package com.tfm.rarawa.tfm.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tfm.rarawa.tfm.credentials.Sqlite;
import com.tfm.rarawa.tfm.sqlite.models.UserInfo;

import java.util.List;

public class SqliteHandler extends SQLiteOpenHelper {

    public SqliteHandler(Context context) {
        super(context, Sqlite.NAME, null, Sqlite.VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserInfo.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME);
        onCreate(db);
    }

    public long insertUserInfo(String name, String surname1, String surname2, int age, String sex, String communicationToken) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(UserInfo.COLUMN_NAME, name);
        values.put(UserInfo.COLUMN_SURNAME1, surname1);
        values.put(UserInfo.COLUMN_SURNAME2, surname2);
        values.put(UserInfo.COLUMN_AGE, age);
        values.put(UserInfo.COLUMN_SEX, sex);
        values.put(UserInfo.COLUMN_COMMUNICATION_TOKEN, communicationToken);

        //Insert new register
        long id = db.insert(UserInfo.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public UserInfo getUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();

        //There is only going to be one register on this table
        int id = 1;

        Cursor cursor = db.query(UserInfo.TABLE_NAME,
                new String[]{UserInfo.COLUMN_ID, UserInfo.COLUMN_NAME, UserInfo.COLUMN_SURNAME1,
                        UserInfo.COLUMN_SURNAME2, UserInfo.COLUMN_AGE, UserInfo.COLUMN_SEX,
                        UserInfo.COLUMN_COMMUNICATION_TOKEN},
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
                cursor.getString(cursor.getColumnIndex(UserInfo.COLUMN_SEX)),
                cursor.getString(cursor.getColumnIndex(UserInfo.COLUMN_COMMUNICATION_TOKEN)));

        cursor.close();

        return note;
    }

    public boolean userInfoExists(){
        String countQuery = String.format("SELECT * FROM %s", UserInfo.TABLE_NAME);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();

        return result;
    }

    public void updateUserInfo(UserInfo userInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

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

        if(userInfo.getSex().length() > 0) {
            values.put(UserInfo.COLUMN_SEX, userInfo.getSex());
        }

        if(userInfo.getCommunicationToken().length() > 0) {
            values.put(UserInfo.COLUMN_COMMUNICATION_TOKEN, userInfo.getCommunicationToken());
        }

        db.update(UserInfo.TABLE_NAME, values, UserInfo.COLUMN_ID + " = ?",
                new String[]{String.valueOf(1)});
    }

    public void deleteUserInfo(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(UserInfo.TABLE_NAME, String.format("%s = ?", UserInfo.COLUMN_ID),
                new String[]{String.valueOf(1)});

        db.close();
    }

}
