package com.rarawa.tfm.services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.utils.Constants;
import com.rarawa.tfm.widget.WidgetProvider;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;

public class UpdateWidgetService extends Service  {
    private IBinder mBinder = new MyBinder();
    private static SqliteHandler db;

    @Override
    public void onCreate() {
        super.onCreate();

        db = new SqliteHandler(this);

        //Create one episode
        new Thread(new Runnable() {
            public void run() {
                while(true){

                    try {
                        if(db.getLastAngerLevel().getAngerLevel() !=
                                db.getPenultimateAngerLevel().getAngerLevel()){

                            Intent intent = new Intent(getBaseContext(), WidgetProvider.class);
                            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            int ids[] = AppWidgetManager.getInstance(getApplication())
                                    .getAppWidgetIds(
                                            new ComponentName(getApplication(),
                                                    WidgetProvider.class));
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                            sendBroadcast(intent);

                        }

                        Thread.sleep(Constants.MEASUREMENT_FREQUENCY/2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    private void updateWidget(){

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
        public UpdateWidgetService getService() {
            return UpdateWidgetService.this;
        }
    }

}