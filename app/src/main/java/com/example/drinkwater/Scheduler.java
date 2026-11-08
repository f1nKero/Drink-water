package com.example.drinkwater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public final class Scheduler {
    private static final String PREFS = "settings";
    private Scheduler() {}
    public static boolean enabled(Context c) { return c.getSharedPreferences(PREFS, 0).getBoolean("enabled", true); }
    public static void setEnabled(Context c, boolean enabled) {
        c.getSharedPreferences(PREFS, 0).edit().putBoolean("enabled", enabled).apply();
        schedule(c);
    }
    private static PendingIntent pending(Context c) {
        Intent intent = new Intent(c, ReminderReceiver.class);
        return PendingIntent.getBroadcast(c, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
    public static void schedule(Context c) {
        AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = pending(c);
        manager.cancel(pi);
        if (!enabled(c)) return;
        Calendar now = Calendar.getInstance();
        Calendar next = (Calendar) now.clone();
        next.set(Calendar.SECOND, 0); next.set(Calendar.MILLISECOND, 0);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        if (hour < 8) {
            next.set(Calendar.HOUR_OF_DAY, 8); next.set(Calendar.MINUTE, 0);
        } else if (hour >= 22) {
            next.add(Calendar.DAY_OF_YEAR, 1);
            next.set(Calendar.HOUR_OF_DAY, 8); next.set(Calendar.MINUTE, 0);
        } else {
            // Align reminders to :00 and :30, never fire a past slot.
            if (minute < 30) { next.set(Calendar.MINUTE, 30); }
            else { next.add(Calendar.HOUR_OF_DAY, 1); next.set(Calendar.MINUTE, 0); }
            if (next.get(Calendar.HOUR_OF_DAY) >= 22) {
                next.add(Calendar.DAY_OF_YEAR, 1);
                next.set(Calendar.HOUR_OF_DAY, 8); next.set(Calendar.MINUTE, 0);
            }
        }
        long trigger = next.getTimeInMillis();
        if (Build.VERSION.SDK_INT >= 31 && !manager.canScheduleExactAlarms()) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pi);
        } else {
            try {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pi);
            } catch (SecurityException permissionRevoked) {
                // Exact-alarm permission can be revoked between checking and scheduling.
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pi);
            }
        }
    }
}
