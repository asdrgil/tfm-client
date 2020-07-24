package com.rarawa.tfm.sqlite.models;

public class AngerLevel {

    public static final String TABLE_NAME = "angerLevel";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ANGER_LEVEL = "angerLevel";


    private int id = 0;
    private long timestamp = 0;
    private int angerLevel = 0;



    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                    COLUMN_TIMESTAMP, "INTEGER NOT NULL",
                    COLUMN_ANGER_LEVEL, "INTEGER NOT NULL");

    public AngerLevel(){}

    public AngerLevel(int id, long timestamp, int angerLevel){
        this.id = id;
        this.timestamp = timestamp;
        this.angerLevel = angerLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAngerLevel() {
        return angerLevel;
    }

    public void setAngerLevel(int angerLevel) {
        this.angerLevel = angerLevel;
    }

}
