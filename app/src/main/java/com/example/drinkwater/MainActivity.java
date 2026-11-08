package com.example.drinkwater;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {
    private LinearLayout root;
    private TextView status;
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView text(String value, int sp, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(sp); t.setTextColor(color); t.setGravity(Gravity.CENTER); return t;
    }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28), dp(28), dp(28), dp(28)); root.setBackgroundColor(Color.rgb(246,250,255));
        TextView title = text("💧 喝水啦", 34, Color.rgb(38,103,172)); root.addView(title);
        TextView detail = text("每天 08:00–22:00\n每 30 分钟提醒一次", 18, Color.DKGRAY);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(-1, dp(100)); detailParams.topMargin = dp(22); root.addView(detail, detailParams);
        Switch toggle = new Switch(this); toggle.setText("开启喝水提醒"); toggle.setTextSize(18); toggle.setChecked(Scheduler.enabled(this));
        root.addView(toggle); toggle.setOnCheckedChangeListener((b, checked) -> { Scheduler.setEnabled(this, checked); updateStatus(); });
        status = text("", 15, Color.GRAY);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, dp(80)); root.addView(status, statusParams);
        Button permission = new Button(this); permission.setText("检查提醒权限"); root.addView(permission);
        permission.setOnClickListener(v -> requestPermissionsIfNeeded());
        setContentView(root);
        ReminderReceiver.createChannel(this);
        Scheduler.schedule(this);
        requestPermissionsIfNeeded();
    }
    @Override protected void onResume() { super.onResume(); if (status != null) { Scheduler.schedule(this); updateStatus(); } }
    private void updateStatus() {
        boolean exact = Build.VERSION.SDK_INT < 31 || ((AlarmManager)getSystemService(ALARM_SERVICE)).canScheduleExactAlarms();
        status.setText((Scheduler.enabled(this) ? "提醒已开启" : "提醒已关闭") + (exact ? "\n准点提醒权限可用" : "\n准点提醒权限未开启，可能延迟"));
    }
    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        if (Build.VERSION.SDK_INT >= 31 && !((AlarmManager)getSystemService(ALARM_SERVICE)).canScheduleExactAlarms()) {
            try {
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName()));
                startActivity(i);
            } catch (Exception ignored) { }
        }
    }
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == 1) {
            updateStatus();
            if (results.length > 0 && results[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionsIfNeeded();
            }
        }
    }
}
