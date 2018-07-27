package com.example.gouthamishivaprakash.howler;

import android.app.ActivityManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gouthamishivaprakash.howler.model.Alarms;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class SnoozeAlarmActivity extends AppCompatActivity {

    private TextView question;
    private EditText answer;
    private Button checkButton;
    private Realm realm;
    private MediaPlayer mediaPlayer;
    private int num1;
    private int num2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze_alarm);

        realm = Realm.getDefaultInstance();

        Log.i("SnoozeAlarm", "After PendingIntent");
        question = findViewById(R.id.textViewQuestion);
        answer = findViewById(R.id.editTextAnswer);
        checkButton = findViewById(R.id.checkButton);

        num1 = new Random().nextInt(100);
        num2 = new Random().nextInt(100);
        question.setText(Integer.toString(num1) + " + " + Integer.toString(num2));

        //MediaPlayer to play the alarm tone.
        //The alarm tone is hard-coded and the mp3 file is stored in the raw folder.
        mediaPlayer = MediaPlayer.create(this, R.raw.howl_tone);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to make sure the user types an answer
                checkAnswer();
            }

        });
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    //app stays on the same activity when back button is pressed
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "You can't Escape!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "You can't Escape!", Toast.LENGTH_SHORT).show();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
        Log.i("Inside", "onpause");

    }


    private void checkAnswer() {
        if (answer.getText().toString().length() == 0)
            Toast.makeText(getApplicationContext(), "Type an Answer!", Toast.LENGTH_SHORT).show();
            //to check if the entered answer is correct
        else if (Integer.parseInt(answer.getText().toString()) == (num1 + num2)) {
            //deleting time from the database
            String setTime = getIntent().getStringExtra("Alarm time");
            realm.executeTransaction(realm -> {
                RealmResults<Alarms> results = realm.where(Alarms.class).equalTo("alarmTime", setTime).findAll();
                //requestCode = results.first().getRequestCode();
                results.deleteAllFromRealm();
            });
            //DisplayAlarmMainActivity.stopAlarm();
            Intent intent = new Intent(getApplicationContext(),AwakeActivity.class);
            startActivity(intent);
            mediaPlayer.stop();
            //finish();

        } else
            Toast.makeText(getApplicationContext(), "One more chance to save yourself...", Toast.LENGTH_SHORT).show();
    }


}



