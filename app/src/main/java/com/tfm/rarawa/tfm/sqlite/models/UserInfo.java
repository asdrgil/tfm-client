package com.tfm.rarawa.tfm.sqlite.models;

public class UserInfo {

    public static final String TABLE_NAME = "userInfo";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME1 = "surname1";
    public static final String COLUMN_SURNAME2 = "surname2";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_SEX = "sex";
    public static final String COLUMN_COMMUNICATION_TOKEN = "communicationToken";

    private int id = 0;
    private String name = "";
    private String surname1 = "";
    private String surname2 = "";
    private int age = 0;
    private String sex = "";
    private String communicationToken = "";


    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s, " +
                            "%s %s, %s %s, %s %s)", TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                    COLUMN_NAME, "TEXT NOT NULL",
                    COLUMN_SURNAME1, "TEXT NOT NULL",
                    COLUMN_SURNAME2, "TEXT",
                    COLUMN_AGE, "INTEGER NOT NULL",
                    COLUMN_SEX, "TEXT NOT NULL", //M or F
                    COLUMN_COMMUNICATION_TOKEN, "TEXT NOT NULL");

    public UserInfo(){}

    public UserInfo(int id, String name, String surname1, String surname2, int age, String sex, String communicationToken){
        this.id = id;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.age = age;
        this.sex = sex;
        this.communicationToken = communicationToken;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCommunicationToken() {
        return communicationToken;
    }

    public void setCommunicationToken(String communicationToken) {
        this.communicationToken = communicationToken;
    }

}