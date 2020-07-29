package com.rarawa.tfm.utils;

import android.content.Context;
import android.util.Log;

import com.rarawa.tfm.sqlite.SqliteHandler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InsertDebugRegisters {

    static int dayInSeconds = 86400;
    static int numberDays = 3;

    public static void insertRegisters(Context context){
        SqliteHandler db = new SqliteHandler(context);

        db.updgradeDB();

        insertUser(db);
        insertSleepCalibrate(db);
        insertExerciseCalibrate(db);
        insertPatterns(db);

        if(numberDays >= 1) {
            insertAngerLevelD1(db);
            insertDisplayedPatternD1(db);
            insertReasonAngerD1(db);
        }

        if(numberDays >= 2) {
            insertAngerLevelD2(db);
            insertDisplayedPatternD2(db);
            insertReasonAngerD2(db);
        }

        if(numberDays >= 3) {
            insertAngerLevelD3(db);
            insertDisplayedPatternD3(db);
            insertReasonAngerD3(db);
        }

        //Log.d(Constants.LOG_TAG, "Number of episodes per day: " + aux);
    }

    public static void insertUser(SqliteHandler db){

        db.insertUserInfo("Pedro",
                "Gómez",
                "Trillo",
                30,
                "M",
                "ABCDEFGHIJ");
    }

    public static void insertSleepCalibrate(SqliteHandler db){
        db.insertSleepCalibrate();
        db.insertWakeUpDebugCalibrate();
    }

    public static void insertExerciseCalibrate(SqliteHandler db){
        db.insertStartExercise();
        db.insertEndExerciseDebug();
    }

    public static void insertPatterns(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertPatterns");

        db.insertPattern(1, "Beber agua", "1,2,3");
        db.insertPattern(2, "Contar hasta diez", "2,3,4");
        db.insertPattern(3, "Dar un paseo", "1,2");
        db.insertPattern(4, "Escribir en un cuaderno", "1,2");
        db.insertPattern(5, "Hacer ejercicio", "2,3,4");
        db.insertPattern(6, "Dibujar", "1,2,3");
        db.insertPattern(7, "Escuchar música", "1,2,3,4");
        db.insertPattern(8, "Respirar profundamente", "1,2,3,4");
        db.insertPattern(9, "Hablar por teléfono con un amigo", "3,4");
        db.insertPattern(10, "Tomarse una manzanilla", "1,2");
        db.insertPattern(11, "Ver un vídeo de animales", "1");
        db.insertPattern(12, "Cerrar los ojos y pensar en una situación que te haga feliz", "3,4");
    }

    /** INSERT REGISTERS FOR DAY 1 */

    //AngerLevelIds = [1-30]
    public static void insertAngerLevelD1(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertAngerLevelD1");

        db.insertAngerLevel(1595683560, 0);
        db.insertAngerLevel(1595683570, 0);
        db.insertAngerLevel(1595683580, 0);

        db.insertAngerLevel(1595683590, 1);
        db.insertAngerLevel(1595683600, 2);
        db.insertAngerLevel(1595683610, 3);
        db.insertAngerLevel(1595683620, 3);
        db.insertAngerLevel(1595683630, 3);
        db.insertAngerLevel(1595683640, 4);
        db.insertAngerLevel(1595683650, 4);
        db.insertAngerLevel(1595683660, 3);
        db.insertAngerLevel(1595683670, 2);
        db.insertAngerLevel(1595683680, 1);

        db.insertAngerLevel(1595683690, 0);
        db.insertAngerLevel(1595683700, 0);

        db.insertAngerLevel(1595683710, 1);
        db.insertAngerLevel(1595683720, 2);
        db.insertAngerLevel(1595683730, 2);
        db.insertAngerLevel(1595683740, 2);
        db.insertAngerLevel(1595683750, 1);

        db.insertAngerLevel(1595683760, 0);

        db.insertAngerLevel(1595683770, 1);
        db.insertAngerLevel(1595683780, 1);

        db.insertAngerLevel(1595683790, 0);
        db.insertAngerLevel(1595683800, 0);

        db.insertAngerLevel(1595683810, 1);
        db.insertAngerLevel(1595683820, 2);
        db.insertAngerLevel(1595683830, 1);

        db.insertAngerLevel(1595683840, 0);
        db.insertAngerLevel(1595683850, 0);
    }


    public static void insertDisplayedPatternD1(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertDisplayedPatternD1");

        db.insertDisplayedPatternDebug(4, 1, -1, "Esta pauta no me sirvio");
        db.insertDisplayedPatternDebug(4, 3, -1, "");
        db.insertDisplayedPatternDebug(4, 4, 1, "");
        db.insertDisplayedPatternDebug(5, 6, 1, "");
        db.insertDisplayedPatternDebug(6, 5, -1, "No podía hacer ejercicio");
        db.insertDisplayedPatternDebug(6, 6, 1, "");
        db.insertDisplayedPatternDebug(7, 7, 1, "");
        db.insertDisplayedPatternDebug(8, 8, -1, "");
        db.insertDisplayedPatternDebug(9, 9, 1, "");
        db.insertDisplayedPatternDebug(10, 12, 1, "");
        db.insertDisplayedPatternDebug(11, 8, 1, "");
        db.insertDisplayedPatternDebug(12, 10, 1, "");
        db.insertDisplayedPatternDebug(13, 11, 1, "");

        db.insertDisplayedPatternDebug(16, 1, -1, "");
        db.insertDisplayedPatternDebug(16, 4, 1, "");
        db.insertDisplayedPatternDebug(17, 5, 1, "");
        db.insertDisplayedPatternDebug(18, 6, 1, "");
        db.insertDisplayedPatternDebug(19, 5, 1, "");
        db.insertDisplayedPatternDebug(20, 11, 1, "");

        db.insertDisplayedPatternDebug(22, 7, 1, "");
        db.insertDisplayedPatternDebug(23, 8, 1, "");

        db.insertDisplayedPatternDebug(26, 3, 1, "");
        db.insertDisplayedPatternDebug(27, 4, 1, "");
        db.insertDisplayedPatternDebug(28, 5, 1, "");

    }

    public static void insertReasonAngerD1(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertReasonAngerD1");

        db.insertReasonAngerFull(4, 13, 1);
        db.insertReasonAngerFull(16, 20, 2);
        db.insertReasonAngerFull(22, 23, 5);
        db.insertReasonAngerFull(26, 28, 3);
    }

    /** INSERT REGISTERS FOR DAY 2 */

    //AngerLevelIds = [31 - 52]
    public static void insertAngerLevelD2(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertAngerLevelD2");

        db.insertAngerLevel(1595683560 + dayInSeconds, 0);
        db.insertAngerLevel(1595683570 + dayInSeconds, 0);
        db.insertAngerLevel(1595683580 + dayInSeconds, 0);

        db.insertAngerLevel(1595683590 + dayInSeconds, 1);
        db.insertAngerLevel(1595683600 + dayInSeconds, 2);
        db.insertAngerLevel(1595683610 + dayInSeconds, 3);
        db.insertAngerLevel(1595683620 + dayInSeconds, 3);
        db.insertAngerLevel(1595683630 + dayInSeconds, 3);
        db.insertAngerLevel(1595683640 + dayInSeconds, 4);
        db.insertAngerLevel(1595683650 + dayInSeconds, 4);
        db.insertAngerLevel(1595683660 + dayInSeconds, 3);
        db.insertAngerLevel(1595683670 + dayInSeconds, 2);
        db.insertAngerLevel(1595683680 + dayInSeconds, 1);

        db.insertAngerLevel(1595683690 + dayInSeconds, 0);
        db.insertAngerLevel(1595683700 + dayInSeconds, 0);

        db.insertAngerLevel(1595683710 + dayInSeconds, 1);
        db.insertAngerLevel(1595683720 + dayInSeconds, 2);
        db.insertAngerLevel(1595683730 + dayInSeconds, 2);
        db.insertAngerLevel(1595683740 + dayInSeconds, 2);
        db.insertAngerLevel(1595683750 + dayInSeconds, 1);

        db.insertAngerLevel(1595683760 + dayInSeconds, 0);
        db.insertAngerLevel(1595683770 + dayInSeconds, 0);
    }

    public static void insertDisplayedPatternD2(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertDisplayedPatternD2");

        db.insertDisplayedPatternDebug(34, 1, -1, "Esta pauta no me sirvio");
        db.insertDisplayedPatternDebug(34, 3, -1, "");
        db.insertDisplayedPatternDebug(34, 4, 1, "");
        db.insertDisplayedPatternDebug(35, 6, 1, "");
        db.insertDisplayedPatternDebug(36, 5, -1, "No podía hacer ejercicio");
        db.insertDisplayedPatternDebug(36, 6, 1, "");
        db.insertDisplayedPatternDebug(37, 7, 1, "");
        db.insertDisplayedPatternDebug(38, 8, -1, "");
        db.insertDisplayedPatternDebug(39, 9, 1, "");
        db.insertDisplayedPatternDebug(40, 12, 1, "");
        db.insertDisplayedPatternDebug(41, 8, 1, "");
        db.insertDisplayedPatternDebug(42, 10, 1, "");
        db.insertDisplayedPatternDebug(43, 11, 1, "");

        db.insertDisplayedPatternDebug(46, 1, -1, "");
        db.insertDisplayedPatternDebug(47, 4, 1, "");
        db.insertDisplayedPatternDebug(48, 5, 1, "");
        db.insertDisplayedPatternDebug(49, 6, 1, "");
        db.insertDisplayedPatternDebug(50, 5, 1, "");

    }

    public static void insertReasonAngerD2(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertReasonAngerD2");

        db.insertReasonAngerFull(34, 43, 1);
        db.insertReasonAngerFull(46, 50, 3);
    }

    /** INSERT REGISTERS FOR DAY 3 */

    //AngerLevelIds = [53-64]
    public static void insertAngerLevelD3(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertAngerLevelD3");

        db.insertAngerLevel(1595683560 + dayInSeconds*2, 0);
        db.insertAngerLevel(1595683570 + dayInSeconds*2, 0);
        db.insertAngerLevel(1595683580 + dayInSeconds*2, 0);

        db.insertAngerLevel(1595683590 + dayInSeconds*2, 1);
        db.insertAngerLevel(1595683600 + dayInSeconds*2, 1);

        db.insertAngerLevel(1595683610 + dayInSeconds*2, 0);
        db.insertAngerLevel(1595683620 + dayInSeconds*2, 0);

        db.insertAngerLevel(1595683630 + dayInSeconds*2, 1);
        db.insertAngerLevel(1595683640 + dayInSeconds*2, 2);
        db.insertAngerLevel(1595683650 + dayInSeconds*2, 1);

        db.insertAngerLevel(1595683660 + dayInSeconds*2, 0);
        db.insertAngerLevel(1595683670 + dayInSeconds*2, 0);
    }

    public static void insertDisplayedPatternD3(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertDisplayedPatternD3");

        db.insertDisplayedPatternDebug(56, 1, -1, "Esta pauta no me sirvio");
        db.insertDisplayedPatternDebug(56, 3, -1, "");
        db.insertDisplayedPatternDebug(56, 4, 1, "");
        db.insertDisplayedPatternDebug(57, 6, 1, "");

        db.insertDisplayedPatternDebug(60, 11, 1, "");
        db.insertDisplayedPatternDebug(61, 10, 1, "");
        db.insertDisplayedPatternDebug(62, 7, 1, "");

    }

    public static void insertReasonAngerD3(SqliteHandler db){
        Log.d(Constants.LOG_TAG, "insertReasonAngerD3");

        db.insertReasonAngerFull(56, 57, 1);
        db.insertReasonAngerFull(60, 62, 6);
    }
}
