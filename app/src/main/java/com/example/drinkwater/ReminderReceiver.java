package com.example.drinkwater;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    static final String CHANNEL = "drink_water_v1";
    static void createChannel(Context c) {
        NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL, "喝水提醒", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("每天 08:00–22:00 每半小时提醒喝水");
        channel.enableVibration(true);
        nm.createNotificationChannel(channel);
    }
    @Override public void onReceive(Context c, Intent intent) {
        if (!Scheduler.enabled(c)) return;
        createChannel(c);
        boolean allowed = Build.VERSION.SDK_INT < 33 || c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        if (allowed) {
            Notification notification = new Notification.Builder(c, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("💧 该喝水啦")
                .setContentText("休息一下，喝几口水吧～")
                .setAutoCancel(true).build();
            ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notification);
        }
        Scheduler.schedule(c);
    }
}
