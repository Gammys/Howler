package com.example.gouthamishivaprakash.howler;


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

import java.util.ArrayList;
import java.util.Calendar;

public class DisplayAlarmMainActivity extends AppCompatActivity {

    private AlarmAdapter adapter;
    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private ArrayList<String> alarms;
    static PendingIntent pendingIntent;
    static AlarmManager alarmManager;
    private int hour;
    private int min;
    Calendar calSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alarm_main);

        // data to populate the RecyclerView with
        alarms = new ArrayList<>();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.list_alarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlarmAdapter(this, alarms);
        recyclerView.setAdapter(adapter);

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
                        String timeset = String.format("%02d", hour) + ":" + String.format("%02d", min);
                        alarms.add(timeset);
                        adapter.notifyItemInserted(alarms.toArray().length - 1);
                        setAlarm(hour, min);
                    }
                };
                TimePickerDialog alarm_timepicker = new TimePickerDialog(DisplayAlarmMainActivity.this, listener, hour, min, true);
                alarm_timepicker.show();

            }
        });

    }

    //creates pending Intent and fires the alarm at specified time
    public void setAlarm(int hour, int min) {

        //create pending intent
        Intent alarmIntent = new Intent(getBaseContext(), SnoozeAlarmActivity.class);
        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, alarmIntent, 0);

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

    public static void stopAlarm(){
        Log.i("InStopAlarm","Cancelling alarm");
        alarmManager.cancel(pendingIntent);
    }



}

