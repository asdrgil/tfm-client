package com.rarawa.tfm.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rarawa.tfm.sqlite.controllers.AngerLevelHandler;
import com.rarawa.tfm.sqlite.controllers.CalibrateSleepHandler;
import com.rarawa.tfm.sqlite.controllers.CalibrateExerciseHandler;
import com.rarawa.tfm.sqlite.controllers.DisplayedPatternHandler;
import com.rarawa.tfm.sqlite.controllers.HistoryHandler;
import com.rarawa.tfm.sqlite.controllers.PatternsHandler;
import com.rarawa.tfm.sqlite.controllers.ReasonAngerHandler;
import com.rarawa.tfm.sqlite.controllers.UserInfoHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.sqlite.models.CalibrateSleep;
import com.rarawa.tfm.sqlite.models.CalibrateExercise;
import com.rarawa.tfm.sqlite.models.DisplayedPattern;
import com.rarawa.tfm.sqlite.models.Pattern;
import com.rarawa.tfm.sqlite.models.ReasonAnger;
import com.rarawa.tfm.sqlite.models.UserInfo;
import com.rarawa.tfm.utils.Constants;

import org.json.JSONObject;

import java.util.HashMap;

public class SqliteHandler extends SQLiteOpenHelper {

    CalibrateSleepHandler calibrateSleepHandler;
    CalibrateExerciseHandler calibrateExerciseHandler;
    UserInfoHandler userInfoHandler;
    PatternsHandler patternsHandler;
    AngerLevelHandler angerLevelHandler;
    ReasonAngerHandler reasonAngerHandler;
    DisplayedPatternHandler displayedPatternHandler;
    HistoryHandler historyHandler;

    public SqliteHandler(Context context) {
        super(context, Constants.SQLITE_DB_NAME, null, Constants.SQLITE_VERSION);
        calibrateSleepHandler = new CalibrateSleepHandler(context);
        calibrateExerciseHandler = new CalibrateExerciseHandler(context);
        userInfoHandler = new UserInfoHandler(context);
        patternsHandler = new PatternsHandler(context);
        angerLevelHandler = new AngerLevelHandler(context);
        reasonAngerHandler = new ReasonAngerHandler(context);
        displayedPatternHandler = new DisplayedPatternHandler(context);
        historyHandler = new HistoryHandler(context);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserInfo.CREATE_TABLE);
        db.execSQL(CalibrateSleep.CREATE_TABLE);
        db.execSQL(CalibrateExercise.CREATE_TABLE);
        db.execSQL(Pattern.CREATE_TABLE);
        db.execSQL(AngerLevel.CREATE_TABLE);
        db.execSQL(ReasonAnger.CREATE_TABLE);
        db.execSQL(DisplayedPattern.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CalibrateSleep.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CalibrateExercise.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Pattern.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AngerLevel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReasonAnger.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DisplayedPattern.TABLE_NAME);
        onCreate(db);
    }

    /* CALIBRATE SLEEP METHODS */

    public void insertSleepCalibrate() {
        calibrateSleepHandler.insertSleepCalibrate(this.getWritableDatabase());

    }

    public int insertWakeUpCalibrate() {
        return calibrateSleepHandler.insertWakeUpCalibrate(this.getWritableDatabase());
    }

    public int insertWakeUpDebugCalibrate() {
        return calibrateSleepHandler.insertWakeUpDebugCalibrate(this.getWritableDatabase());
    }

    public void undoSleepCalibrate() {
        calibrateSleepHandler.undoSleepCalibrate(this.getWritableDatabase());
    }



    public CalibrateSleep getCalibrateExerciseHandler() {
        return calibrateSleepHandler.getCalibrate(this.getReadableDatabase());
    }

    public int calibrateSleepExists(){
        return calibrateSleepHandler.calibrateSleepExists(this.getReadableDatabase());
    }

    public void deleteCalibrateSleep(){
        calibrateSleepHandler.deleteCalibrateSleep(this.getWritableDatabase());
    }

    /* CALIBRATE EXERCISE METHODS */

    public void insertStartExercise() {
        calibrateExerciseHandler.insertStartExercise(this.getWritableDatabase());
    }

    public void insertEndExercise() {
        calibrateExerciseHandler.insertEndExercise(this.getWritableDatabase());
    }

    public void insertEndExerciseDebug() {
        calibrateExerciseHandler.insertEndExerciseDebug(this.getWritableDatabase());
    }

    public void undoStartExercise() {
        calibrateExerciseHandler.undoStartExercise(this.getWritableDatabase());
    }

    public CalibrateExercise getExercise(){
        return calibrateExerciseHandler.getExercise(this.getReadableDatabase());
    }

    public int calibrateExerciseExists(){
        return calibrateExerciseHandler.calibrateExerciseExists(this.getReadableDatabase());
    }


    public void deleteCalibrateExercise(){
        calibrateExerciseHandler.deleteExercise(this.getWritableDatabase());
    }


    /* USER INFO METHODS */

    public long insertUserInfo(String name, String surname1, String surname2, int age, String gender, String communicationToken) {
        return userInfoHandler.insertUserInfo(name, surname1, surname2, age, gender,
                communicationToken, this.getWritableDatabase());
    }

    public UserInfo getUserInfo() {
        return userInfoHandler.getUserInfo(this.getReadableDatabase());

    }

    public boolean userInfoExists(){
        return userInfoHandler.userInfoExists(this.getReadableDatabase());
    }

    public void updateUserInfo(UserInfo userInfo) {
        userInfoHandler.updateUserInfo(userInfo, this.getWritableDatabase());
    }

    public void deleteUserInfo(){
        userInfoHandler.deleteUserInfo(this.getWritableDatabase());
    }

    /* PATTERNS METHODS */

    public long insertPattern(int id, String name, String intensities) {
        return patternsHandler.insertPattern(id, name, intensities, this.getWritableDatabase());
    }

    public Pattern getPattern(int id) {
        return patternsHandler.getPattern(id, this.getReadableDatabase());
    }

    public boolean patternExists(int id) {
        return patternsHandler.patternExists(id, this.getReadableDatabase());
    }

    public void updatePattern(Pattern pattern) {
        patternsHandler.updatePattern(pattern, this.getWritableDatabase());
    }

    public void deletePattern(int id) {
        patternsHandler.deletePattern(id, this.getWritableDatabase());
    }

    /* ANGERLEVEL METHODS */

    public long insertAngerLevel(long timestamp, int angerLevel){
        return angerLevelHandler.insertAngerLevel(timestamp, angerLevel, this.getWritableDatabase());
    }

    public String getRandomOrderPatterns(){
        return patternsHandler.getRandomOrderPatterns(this.getReadableDatabase());
    }

    public String getRandomOrderPatternsByAngerLevel(int angerLevel){
        return patternsHandler.getRandomOrderPatternsByAngerLevel(angerLevel, this.getReadableDatabase());
    }

    public AngerLevel getAngerLevelById(int id){
        return angerLevelHandler.getAngerLevelById(id, this.getReadableDatabase());
    }

    public AngerLevel getAngerLevelByTimestamp(long timestamp){
        return angerLevelHandler.getAngerLevelByTimestamp(timestamp, this.getReadableDatabase());
    }

    public AngerLevel getLastAngerLevel(){
        return angerLevelHandler.getLastAngerLevel(this.getReadableDatabase());
    }

    public AngerLevel getPenultimateAngerLevel(){
        return angerLevelHandler.getPenultimateAngerLevel(this.getReadableDatabase());
    }

    /* REASONANGER METHODS */

    public void insertReasonAnger(int idFirstAngerLevel, int reasonAnger){
        reasonAngerHandler.insertReasonAnger(
                idFirstAngerLevel, reasonAnger, this.getWritableDatabase());
    }

    public void insertReasonAngerFull(int idFirstAngerLevel, int idLastAngerLevel, int reasonAnger){
        reasonAngerHandler.insertReasonAngerFull(
                idFirstAngerLevel, idLastAngerLevel, reasonAnger, this.getWritableDatabase());
    }

    public void updateReasonAnger(int idFirstAngerLevel, int idLastAngerLevel){
        reasonAngerHandler.updateReasonAnger(
                idFirstAngerLevel, idLastAngerLevel, this.getWritableDatabase());
    }

    public ReasonAnger getReasonAnger(int idFirstAngerLevel){
        return reasonAngerHandler.getReasonAnger(idFirstAngerLevel, this.getReadableDatabase());
    }

    public JSONObject getUnsyncedReasonAnger(){
        return reasonAngerHandler.getUnsyncedReasonAnger(this.getWritableDatabase());
    }

    /* DISPLAYPATTERN METHODS */

    public void insertDisplayedPattern(int angerLevelId, int patternId){
        displayedPatternHandler.insertDisplayedPattern(
                angerLevelId, patternId, this.getWritableDatabase());
    }

    public void insertDisplayedPatternDebug(int angerLevelId, int patternId,
                                            int status, String comment){
        displayedPatternHandler.insertDisplayedPatternDebug(
                angerLevelId, patternId, status, comment, this.getWritableDatabase());
    }

    public void updateDisplayedPattern(DisplayedPattern displayedPattern){
        displayedPatternHandler.updateDisplayedPattern(displayedPattern, this.getWritableDatabase());
    }

    public HashMap<Integer, String> getTopUsefulPatterns(long from, long to, int limit){
        return displayedPatternHandler
                .getTopUsefulPatterns(from, to, limit, this.getReadableDatabase());
    }

    public JSONObject getUnsyncedPatterns(){
        return displayedPatternHandler.getUnsyncedPatterns(this.getWritableDatabase());
    }

    /* HISTORY METHODS */

    public HashMap<Long, Integer> getNumberEpisodesPerDay(long from, long to){
        return historyHandler.getNumberEpisodesPerDay(from, to, this.getReadableDatabase());
    }

    public HashMap<Long, Integer> getEpisodesDurationPerDay(long from, long to){
        return historyHandler.getEpisodesDurationPerDay(from, to, this.getReadableDatabase());
    }

    public HashMap<Long, Integer> getEpisodesAverageDurationPerDay(long from, long to){
        return historyHandler.getEpisodesAverageDurationPerDay(from, to, this.getReadableDatabase());
    }

    public HashMap<Integer, Integer> getTotalEpisodesTodayYesterday(){
        return historyHandler.getTotalEpisodesTodayYesterday(this.getReadableDatabase());
    }

    public void updgradeDB(){
        onUpgrade(this.getWritableDatabase(), 1, 2);
    }

}
