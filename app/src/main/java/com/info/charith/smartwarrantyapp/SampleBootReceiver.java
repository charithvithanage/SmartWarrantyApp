package com.info.charith.smartwarrantyapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                new SetAlarm(context);
            }
        }
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//
//            //This will set the alarm time to be at the 14:30
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.HOUR_OF_DAY, 5);
//            calendar.set(Calendar.MINUTE, 7);
//
//            //This intent, will execute the AlarmBroadcaster when the alarm is triggered
//            Intent alertIntent = new Intent(context, AlarmBroadcaster.class);
//
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*5, pendingIntent);
//
//        }

    }
}
