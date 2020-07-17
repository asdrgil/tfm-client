package com.rarawa.tfm.sqlite.models;

public class AngerLevel {

    public static final String TABLE_NAME = "angerLevel";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ANGER_LEVEL = "angerLevel";
    public static final String COLUMN_SUGGESTED_PATTERN = "suggestedPattern";
    public static final String COLUMN_USEFULNESS_PATTERN = "usefulnessPattern";
    public static final String COLUMN_COMMENT_PATTERN = "commentPattern";

    private int id = 0;
    private long timestamp = 0;
    private int angerLevel = 0;
    private int suggestedPattern = 0;
    private int usefulnessPattern = 0;
    private String commentPattern = "";



    public static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s, %s %s, %s %s)",
                    TABLE_NAME,
                    COLUMN_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                    COLUMN_TIMESTAMP, "INTEGER NOT NULL",
                    COLUMN_ANGER_LEVEL, "INTEGER NOT NULL",
                    COLUMN_SUGGESTED_PATTERN, "INTEGER",
                    COLUMN_USEFULNESS_PATTERN, "INTEGER",
                    COLUMN_COMMENT_PATTERN, "TEXT");

    public AngerLevel(){}

    public AngerLevel(int id, long timestamp, int angerLevel, int suggestedPattern,
                      int usefulnessPattern, String commentPattern){
        this.id = id;
        this.timestamp = timestamp;
        this.angerLevel = angerLevel;
        this.suggestedPattern = suggestedPattern;
        this.usefulnessPattern = usefulnessPattern;
        this.commentPattern = commentPattern;
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

    public int getSuggestedPattern() {
        return suggestedPattern;
    }

    public void setSuggestedPattern(int suggestedPattern) {
        this.suggestedPattern = suggestedPattern;
    }

    public int getUsefulnessPattern() {
        return usefulnessPattern;
    }

    public void setUsefulnessPattern(int usefulnessPattern) {
        this.usefulnessPattern = usefulnessPattern;
    }

    public String getCommentPattern() {
        return commentPattern;
    }

    public void setCommentPattern(String commentPattern) {
        this.commentPattern = commentPattern;
    }
}
