package com.rarawa.tfm.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.sqlite.models.AngerLevel;
import com.rarawa.tfm.utils.Constants;

import java.util.Map;
import java.util.Random;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;

public class WidgetProvider extends AppWidgetProvider {

    private PendingIntent pendingIntent;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SqliteHandler db = new SqliteHandler(context);

        //Update all copies of this widget
        final int count = appWidgetIds.length;

        Log.d(LOG_TAG, ": " + count);

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            AngerLevel lastAngerLevel = db.getLastAngerLevel();
            int currentAngerLevel = lastAngerLevel.getAngerLevel();

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget);

            final String thermoLiteralPrefix = "Nivel de ira: ";

            Map.Entry<Integer, String> currentEntry =
                    Constants.ANGER_LEVEL_UI_MAP.get(currentAngerLevel);

            remoteViews.setImageViewResource(R.id.widgetIcon, currentEntry.getKey());

            remoteViews.setTextViewText(R.id.textView,
                    thermoLiteralPrefix.concat(currentEntry.getValue()));

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // Get the layout for the App Widget and attach an on-click listener to the button
            remoteViews.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, WidgetProvider.class));

        onUpdate(context, appWidgetManager, ids);
    }
}
