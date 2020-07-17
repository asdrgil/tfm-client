package com.rarawa.tfm.sqlite.models;

public class CalibrateExercise {

    public static final String TABLE_NAME = "calibrateExercise";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_START_TIMESTAMP = "startTimestamp";
    public static final String COLUMN_END_TIMESTAMP = "endTimestamp";

    private int id = 0;
    private long startTimestamp = 0;
    private long endTimestamp = 0;

    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY",
                    COLUMN_START_TIMESTAMP, "INTEGER DEFAULT 0",
                    COLUMN_END_TIMESTAMP, "INTEGER DEFAULT 0");

    public CalibrateExercise(){}

    public CalibrateExercise(int id, long startTimestamp, long endTimestamp){
        this.id = id;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return this.endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

}
