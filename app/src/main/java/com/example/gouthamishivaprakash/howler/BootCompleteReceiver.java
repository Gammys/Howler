package com.example.gouthamishivaprakash.howler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.gouthamishivaprakash.howler.model.Alarms;

import io.realm.RealmResults;

import static android.content.Context.ALARM_SERVICE;

//Starts IntentService - RestartAlarmsService on boot completion
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent i = new Intent(context, RestartAlarmsService.class);
            ComponentName service = context.startService(i);
        }
    }
}
