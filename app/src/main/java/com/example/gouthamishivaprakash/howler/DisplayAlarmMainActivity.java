package com.example.gouthamishivaprakash.howler;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gouthamishivaprakash.howler.model.Alarms;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class DisplayAlarmMainActivity extends AppCompatActivity {

    private AlarmAdapter adapter;
    private FloatingActionButton add;
    private RecyclerView recyclerView;
    static PendingIntent pendingIntent;
    static AlarmManager alarmManager;
    private Realm realm;
    private RealmResults<Alarms> results;
    private Alarms alarm;
    private int hour;
    private int min;
    private String timeset;
    private int PRIMARY_KEY;
    private int REQUEST_CODE;
    Calendar calSet;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alarm_main);

        //getting realm object to querry
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm -> results = realm.where(Alarms.class).findAll());

        // data to populate the RecyclerView with
        //alarms = new ArrayList<>();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.list_alarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlarmAdapter(this, results, realm);
        adapter.setOnClickTrashIconListener(setTime -> {
            realm.executeTransaction(realm -> {
                RealmResults<Alarms> results = realm.where(Alarms.class).equalTo("alarmTime", setTime).findAll();
                requestCode = results.first().getRequestCode();
                results.deleteAllFromRealm();
            });
            Log.i("Alarm deleted", setTime);
            Log.i("P.I. deleted", String.valueOf(requestCode));
            Intent intent = new Intent(getApplicationContext(), SnoozeAlarmActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, 0);
            alarmManager.cancel(mPendingIntent);
        });
        recyclerView.setAdapter(adapter);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //setting click listener for add alarm fab to set a new alarm
        add = findViewById(R.id.fabAddAlarm);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the calender instance
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                min = c.get(Calendar.MINUTE);

                //TimePickerDialog pops up to pick the time for the new alarm
                final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        timeset = String.format("%02d", hour) + ":" + String.format("%02d", min);
                        setAlarm(hour, min, timeset);
                    }
                };
                TimePickerDialog alarm_timepicker = new TimePickerDialog(DisplayAlarmMainActivity.this, listener, hour, min, true);
                alarm_timepicker.show();
            }
        });
    }


    //creates pending Intent and fires the alarm at specified time
    public void setAlarm(int hour, int min, String setTime) {
        //Computing Primary key and Request code
        PRIMARY_KEY = (Integer.parseInt(String.format("%02d", hour)+String.format("%02d", min)));
        REQUEST_CODE = PRIMARY_KEY;

        //create pending intent
        Intent alarmIntent = new Intent(getApplicationContext(), SnoozeAlarmActivity.class);
        alarmIntent.putExtra("Alarm time", setTime);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE, alarmIntent, 0);

        //adding alarm to the database
        realm.executeTransaction((Realm realm) -> {
            alarm = realm.createObject(Alarms.class, PRIMARY_KEY);
            alarm.setAlarmTime(setTime);
            alarm.setRequestCode(REQUEST_CODE);
        });
        adapter.updateAdapter(alarm);

        //set up alarmManager to fire the alarm
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calNow = Calendar.getInstance();
        //Calendar calSet = (Calendar) calNow.clone();
        calSet = (Calendar) calNow.clone();
        calSet.set(Calendar.HOUR_OF_DAY, hour);
        calSet.set(Calendar.MINUTE, min);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);
        if (calSet.compareTo(calNow) <= 0)
            calSet.add(Calendar.DATE, 1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
        Log.i("PendingIntent","created");


        Toast.makeText(this, "Alarm set to the time specified", Toast.LENGTH_SHORT).show();

    }

//    public static void stopAlarm(){
//        Log.i("InStopAlarm","Cancelling alarm");
//        alarmManager.cancel(pendingIntent);
//    }

}

