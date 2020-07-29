package com.rarawa.tfm.sqlite.models;

public class ReasonAnger {

    public static final String TABLE_NAME = "reasonAngerEpisode";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_FIRST_ANGER_LEVEL = "idFirstAngerLevel";
    public static final String COLUMN_ID_LAST_ANGER_LEVEL = "idLastAngerLevel";
    public static final String COLUMN_REASON_ANGER = "reasonAnger";

    private int id = 0;
    //First id of the measured episode
    private int idFirstAngerLevel = 0;
    private int idLastAngerLevel = 0;
    private int reasonAnger = 0;



    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                    COLUMN_ID_FIRST_ANGER_LEVEL, "INTEGER NOT NULL",
                    COLUMN_ID_LAST_ANGER_LEVEL, "INTEGER NOT NULL",
                    COLUMN_REASON_ANGER, "INTEGER NOT NULL");

    public ReasonAnger(){}

    public ReasonAnger(int id, int idFirstAngerLevel, int reasonAnger){
        this.id = id;
        this.idFirstAngerLevel = idFirstAngerLevel;
        this.reasonAnger = reasonAnger;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFirstAngerLevel() {
        return idFirstAngerLevel;
    }

    public void setIdLastAngerLevel(int idLastAngerLevel) {
        this.idLastAngerLevel = idLastAngerLevel;
    }

    public int getIdLastAngerLevel() {
        return idLastAngerLevel;
    }

    public void setIdFirstAngerLevel(int idFirstAngerLevel) {
        this.idFirstAngerLevel = idFirstAngerLevel;
    }

    public int getReasonAnger() {
        return reasonAnger;
    }

    public void setReasonAnger(int reasonAnger) {
        this.reasonAnger = reasonAnger;
    }
}
