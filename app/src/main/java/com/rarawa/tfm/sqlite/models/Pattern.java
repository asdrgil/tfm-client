package com.rarawa.tfm.sqlite.models;

public class Pattern {

    public static final String TABLE_NAME = "patterns";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_INTENSITIES = "intensities";

    private int id = 0;
    private String name = "";
    private String intensities = "";

    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY",
                    COLUMN_NAME, "TEXT NOT NULL",
                    COLUMN_INTENSITIES, "TEXT NOT NULL");

    public Pattern(){}

    public Pattern(int id, String name, String intensity){
        this.id = id;
        this.name = name;
        this.intensities = intensities;
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

    public String getIntensities() {
        return intensities;
    }

    public void setIntensities(String intensities) {
        this.intensities = intensities;
    }

}
