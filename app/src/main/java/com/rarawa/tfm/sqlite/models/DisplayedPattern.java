package com.rarawa.tfm.sqlite.models;

public class DisplayedPattern {

    public static final String TABLE_NAME = "displayedPattern";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ANGER_LEVEL_ID = "angerLevelId";
    public static final String COLUMN_PATTERN_ID = "patternId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_COMMENTS = "comments";


    private int id = 0;
    private int patternId = 0;
    private int angerLevelId = 0;
    private int status = 0;
    private String comments = "";

    //Status:
    // 0 -> Unknown
    //-1 -> Discarded
    //-2 -> Useless
    // 1 -> Useful

    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                    COLUMN_PATTERN_ID, "INTEGER NOT NULL",
                    COLUMN_ANGER_LEVEL_ID, "INTEGER NOT NULL",
                    COLUMN_STATUS, "INTEGER NOT NULL DEFAULT 0",
                    COLUMN_COMMENTS, "TEXT");

    public DisplayedPattern(){}

    public DisplayedPattern(int id, int angerLevelId, int patternId, int status, String comments){
        this.id = id;
        this.angerLevelId = angerLevelId;
        this.patternId = patternId;
        this.status = status;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAngerLevelId() {
        return angerLevelId;
    }

    public void setAngerLevelId(int angerLevelId) {
        this.angerLevelId = angerLevelId;
    }

    public int getPatternId() {
        return patternId;
    }

    public void setPatternId(int patternId) {
        this.patternId = patternId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


}
