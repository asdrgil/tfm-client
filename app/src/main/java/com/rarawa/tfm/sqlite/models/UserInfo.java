package com.rarawa.tfm.sqlite.models;

import android.util.Log;

public class UserInfo {

    public static final String TABLE_NAME = "userInfo";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME1 = "surname1";
    public static final String COLUMN_SURNAME2 = "surname2";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_COMMUNICATION_TOKEN = "communicationToken";
    public static final String COLUMN_CALIBRATED = "calibrated";
    public static final String COLUMN_REGISTER_TIMESTAMP = "registerTimestamp";

    private int id = 0;
    private String name = "";
    private String surname1 = "";
    private String surname2 = "";
    private int age = 0;
    private String gender = "";
    private String communicationToken = "";
    private int calibrated = 0;
    private long registerTimestamp = 0;

    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s, " +
                            "%s %s, %s %s, %s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                    COLUMN_NAME, "TEXT NOT NULL",
                    COLUMN_SURNAME1, "TEXT NOT NULL",
                    COLUMN_SURNAME2, "TEXT",
                    COLUMN_AGE, "INTEGER NOT NULL",
                    COLUMN_GENDER, "TEXT NOT NULL", //M or F
                    COLUMN_COMMUNICATION_TOKEN, "TEXT NOT NULL",
                    COLUMN_CALIBRATED, "INTEGER DEFAULT 0",
                    COLUMN_REGISTER_TIMESTAMP, "INTEGER DEFAULT 0");

    public UserInfo(){}

    public UserInfo(int id, String name, String surname1, String surname2, int age, String gender, String communicationToken, long registerTimestamp){
        this.id = id;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.age = age;
        this.gender = gender;
        this.communicationToken = communicationToken;
        this.calibrated = 0;
        this.registerTimestamp = registerTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCommunicationToken() {
        return communicationToken;
    }

    public void setCommunicationToken(String communicationToken) {
        this.communicationToken = communicationToken;
    }

    public int getCalibrated() {
        return calibrated;
    }

    public void setCalibrated(int calibrated) {
        this.calibrated = calibrated;
    }

    public long getRegisterTimestamp() {
        return this.registerTimestamp;
    }

}
