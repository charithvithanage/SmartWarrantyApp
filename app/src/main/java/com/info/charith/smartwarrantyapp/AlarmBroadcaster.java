package com.info.charith.smartwarrantyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmBroadcaster extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);
    }

    public void createNotification(Context context) {
        NotificationTrigger.starNotification(context);
    }
}
