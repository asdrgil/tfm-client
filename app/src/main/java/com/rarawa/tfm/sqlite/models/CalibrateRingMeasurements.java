package com.rarawa.tfm.sqlite.models;

public class CalibrateRingMeasurements {

    public static final String TABLE_NAME = "calibrateRingMeasurements";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SENSOR = "sensor";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id = 0;
    private int sensor = 0;
    private long value = 0;
    private long timestamp = 0;

    /*Sensor values:
    * 0: Acceleration
    * 1: HR
    * 2: EDA
    * */

    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                    COLUMN_SENSOR, "INTEGER NOT NULL",
                    COLUMN_VALUE, "INTEGER NOT NULL",
                    COLUMN_TIMESTAMP, "INTEGER");

    public CalibrateRingMeasurements(){}

    public CalibrateRingMeasurements(int id, int sensor, long value){
        this.id = id;
        this.sensor = sensor;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSensor() {
        return sensor;
    }

    public void setSensor(int sensor) {
        this.sensor = sensor;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
