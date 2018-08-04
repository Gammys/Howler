package com.example.gouthamishivaprakash.howler;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.example.gouthamishivaprakash.howler.model.Alarms;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class RestartAlarmsService extends IntentService {
    private RealmResults<Alarms> results;
    private Realm realm;

    public RestartAlarmsService() {
        super("RestartAlarmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //get all the alarms in the database
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            results =  realm.where(Alarms.class).findAll();});

        //loop through all the alarms and reset each of them
        for (int i = 0; i < results.size(); i++) {
            //get the set time
            String time = results.get(i).getAlarmTime();
            //get the primary key
            int keyAndCode = results.get(i).getId();

            //Re-initialising the pending intents
            Intent alarmIntent = new Intent(getApplicationContext(), SnoozeAlarmActivity.class);
            alarmIntent.putExtra("Alarm time", time);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), keyAndCode, alarmIntent, 0);

            //create calendar instance to the set time
            String hour = time.substring(0,2);
            String min = time.substring(3,5);
            Calendar timeSet = Calendar.getInstance();
            timeSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            timeSet.set(Calendar.MINUTE, Integer.parseInt(min));
            timeSet.set(Calendar.SECOND, 0);
            timeSet.set(Calendar.MILLISECOND, 0);
            if (timeSet.before(Calendar.getInstance())) { // If time set is before the current time, set the alarm to the same time the next day
                timeSet.add(Calendar.DAY_OF_MONTH, 1);
            }

            //re-initialise the alarm
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeSet.getTimeInMillis(), pendingIntent);
            Log.i("PendingIntent", "created");
            Log.i("Alarm created", time);
        }
    }

}
