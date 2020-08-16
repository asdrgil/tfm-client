package com.rarawa.tfm.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.Constants;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;

public class GenerateEpisodesService extends Service  {
    private IBinder mBinder = new MyBinder();
    private LocalBroadcastManager broadcaster;



    @Override
    public void onCreate() {

        super.onCreate();

        broadcaster = LocalBroadcastManager.getInstance(this);

        //Create one episode
        new Thread(new Runnable() {
            public void run() {

                SqliteHandler db = new SqliteHandler(getBaseContext());

                for(int i = 0; i < Constants.NUMBER_WEAVES; i++) {
                    generateValue(db, 0);

                    //First half of the wave
                    for(int j = 0; j < 4; j++) {
                        generateValue(db, j);
                    }

                    //Second half of the wave
                    for(int j = 4; j >= 0; j--) {
                        generateValue(db, j);
                    }
                }

                //DEBUG for trying if isOneMinuteRest works. Delete it after trial.
                for(int i = 0; i < 30; i++){
                    generateValue(db, 0);
                }
            }
        }).start();


    }

    private void generateValue(SqliteHandler db, int currentAngerLevel){
        long currentTimestamp = System.currentTimeMillis();

        long aux = db.insertAngerLevel(currentTimestamp, currentAngerLevel);

        Log.d(LOG_TAG, "angerLevelId: " + aux);

        String message = String.format("%d,%d", currentAngerLevel, currentTimestamp);

        Log.d(LOG_TAG, "Generated: " + currentAngerLevel);

        Intent intent = new Intent("RESULT");
        intent.putExtra("MESSAGE", message);
        broadcaster.sendBroadcast(intent);

        try {
            Thread.sleep(Constants.MEASUREMENT_FREQUENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public GenerateEpisodesService getService() {
            return GenerateEpisodesService.this;
        }
    }

}