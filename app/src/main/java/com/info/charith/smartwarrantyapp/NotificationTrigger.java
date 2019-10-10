package com.info.charith.smartwarrantyapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.info.charith.smartwarrantyapp.Activities.MainActivity;

public class NotificationTrigger extends IntentService {
    private static final String ALARM = "com.info.charith.smartwarrantyapp.ALARM";
    private static Context mContext;

    public NotificationTrigger() {
        super("NotificationTrigger");
    }

    public static void starNotification(Context context) {
        mContext=context;
        Intent intent = new Intent(context, NotificationTrigger.class);
        intent.setAction(ALARM);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ALARM.equals(action)) {
                handleAlarm();
            }
        }
    }

    private void handleAlarm() {

        PendingIntent notificationIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_menu_slideshow)
                .setContentTitle("title")
                .setTicker(mContext.getString(R.string.app_name))
                .setContentText("wake up user!");

        notificationBuilder.setContentIntent(notificationIntent);

        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        notificationBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
