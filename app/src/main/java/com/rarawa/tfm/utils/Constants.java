package com.rarawa.tfm.utils;

import com.rarawa.tfm.R;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Constants {
    public static final String LOG_TAG = "[DEBUG1]";

    /* BLE */
    public static final int REQUEST_ENABLE_BT = 1;
    public static final UUID mmServiceUUID =
            UUID.fromString("dd499b70-e4cd-4988-a923-a7aab7283f8e");
    public static final UUID streamingCharacteristicUUID =
            UUID.fromString("a0956420-9bd2-11e4-bd06-0800200c9a66");
    // Defined by the BLE standard
    public static final UUID clientCharacteristicConfigurationUUID =
            UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    //Measurement every X milliseconds
    public static final int MEASUREMENT_FREQUENCY = 10*1000; //DEBUG
    public static final int MEASUREMENT_FREQUENCY_2 = 10*1000; //DEBUG
    //Maximum time a anger level is valid (it should be MEASUREMENT_FREQUENCY, but the app can fail
    //and not measure some state. At this point, is when this value is useful
    public static final int MEASUREMENT_VALID_TIME = 20*1000;
    public static final int MAX_ANGER_LEVEL = 4;
    public static final int NUMBER_WEAVES = 1; //DEBUG
    public static final int UPDATE_PATTERNS_FREQUENCY = 20*60*1000; //DEBUG
    //public static final int UPDATE_PATTERNS_FREQUENCY = 30*60*1000; //PROD

    /*Broadcast constants*/
    public static final String BROADCAST_ACTION = "ACTION";
    public static final String BROADCAST_MESSAGE = "MESSAGE";

    /* General */
    public static final int FRAGMENT_TRANSITION_TIME = 3*1000;


    /* Sqlite */
    public static final int SQLITE_VERSION = 1;
    public static final String SQLITE_DB_NAME = "local";

    /* API Rest */
    public static final String API_BASE_URL = "https://holstein.fdi.ucm.es/termoira/api/v1/";
    public static final String SYNC_DEVICE_URL = "sincronizarDispositivo/";
    public static final String UPDATE_PATTERNS_PATIENT_URL = "actualizarPautasPaciente/";
    public static final int API_TOKEN_LENGTH = 6;

    /* Fragments */
    public static final String FRAGMENT_MAIN = "main";
    public static final String FRAGMENT_INDEX_NOT_CALIBRATED = "index_not_calibrated";
    public static final String FRAGMENT_HISTORY = "history";
    public static final String FRAGMENT_REGISTER = "register";
    public static final String FRAGMENT_CALIBRATE = "calibrate";
    public static final String FRAGMENT_CALIBRATE_SLEEP = "calibrateSleep";
    public static final String FRAGMENT_CALIBRATE_EXERCISE = "calibrateExercise";

    /* Subfragments */
    public static final TreeMap<Integer, String> SUBFRAGMENT_MAIN  = new TreeMap<Integer, String>() {{
        put(0, "submain_0");
        put(1, "submain_1");
        put(2, "submain_2");
        put(3, "submain_3");
    }};


    /* Calibrate sleep */
    //public static final long MINIMUM_SLEEP_CALIBRATE_TIME = 60*60*2*1000; //PROD
    public static final long MINIMUM_SLEEP_CALIBRATE_TIME = 60*1000; //DEBUG
    //public static final long MINIMUM_EXERCISE_CALIBRATE_TIME = 60*15*1000;
    public static final long MINIMUM_EXERCISE_CALIBRATE_TIME = 25*1000; //DEBUG

    /* Main */
    public static final long CALIBRATE_FREQUENCY = 10*60*1000;

    public static final TreeMap<Integer, String> REASON_ANGER  = new TreeMap<Integer, String>() {{
        put(0, "");
        put(1, "1. Rechazo, exclusión, ser ignorado");
        put(2, "2. Sentirse incomprendido");
        put(3, "3. Inseguridad");
        put(4, "4. Engaño");
        put(5, "5. Vejaciones verbales");
        put(6, "6. Sentirse avergonzado");
        put(7, "7. Frustración o impotencia");
        put(8, "8. Decepción");
        put(9, "9. Tristeza");
        put(10, "10. Miedo a perder algo");
        put(11, "11. Impaciencia");
        put(12, "12. Ataque físico");
        put(13, "13. Otro motivo");
    }};

    public static final Map<Integer, Map.Entry<Integer, String>> ANGER_LEVEL_UI_MAP = new HashMap<Integer, Map.Entry<Integer, String>>() {{
        put(0, new AbstractMap.SimpleEntry(R.drawable.ic_thermometer_0, "Nivel de ira: verde (0/4)"));
        put(1, new AbstractMap.SimpleEntry(R.drawable.ic_thermometer_1, "Nivel de ira: amarillo (1/4)"));
        put(2, new AbstractMap.SimpleEntry(R.drawable.ic_thermometer_2, "Nivel de ira: amarillo-naranja (2/4)"));
        put(3, new AbstractMap.SimpleEntry(R.drawable.ic_thermometer_3, "Nivel de ira: naranja (3/4)"));
        put(4, new AbstractMap.SimpleEntry(R.drawable.ic_thermometer_4, "Nivel de ira: rojo (4/4)"));
    }};



    //Sharedpreferences info
    public static final String SHAREDPREFERENCES_FILE = "spFile";
    public static final String SHAREDPREFERENCES_FRAGMENT_MAIN = "fragmentMain";
    //ID of the anger level measurement that is being displayed on the screen
    public static final String SHAREDPREFERENCES_DISPLAY_ANGERLEVEL_ID = "displayAngerLevelId";
    public static final String SHAREDPREFERENCES_MESSAGE_CALIBRATED = "messageCalibrated";
    public static final String SHAREDPREFERENCES_FIRST_ZERO_LEVEL_PLATEAU = "firstZeroLevelPlateau";
    public static final String SHAREDPREFERENCES_CURRENT_PATTERNS_ORDER = "currentPatternsOrder";


    //Inactive time before making the old episode obsolete
    public static final int TIME_EPISODE_IS_OBSOLETE = 60*1000;

    public static final long ONE_DAY_TIMESTAMP = 60*60*24;
}
