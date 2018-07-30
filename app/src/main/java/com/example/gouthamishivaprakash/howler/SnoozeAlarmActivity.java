package com.example.gouthamishivaprakash.howler;

import android.app.ActivityManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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
    private AudioManager mAudioManager;
    private int num1;
    private int num2;
    private int userVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze_alarm);

        realm = Realm.getDefaultInstance();

        Log.i("SnoozeAlarm", "After PendingIntent");
        question = findViewById(R.id.textViewQuestion);
        answer = findViewById(R.id.editTextAnswer);
        checkButton = findViewById(R.id.checkButton);

        //Generate Question
        generateQuestion();

        //Override silent mode and play the alarm tone
        overrideSilentModeAndPlayAlarmTone();

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void generateQuestion(){
        num1 = new Random().nextInt(100);
        num2 = new Random().nextInt(100);
        question.setText(Integer.toString(num1) + " + " + Integer.toString(num2));
        answer.setText("");
    }

    private void checkAnswer() {
        //to make sure the user types an answer
        if (answer.getText().toString().length() == 0)
            Toast.makeText(getApplicationContext(), "Type an Answer!", Toast.LENGTH_SHORT).show();
        //to check if the entered answer is correct
        else if (Integer.parseInt(answer.getText().toString()) == (num1 + num2)) {
            //deleting time from the database
            String setTime = getIntent().getStringExtra("Alarm time");
            realm.executeTransaction(realm -> {
                RealmResults<Alarms> results = realm.where(Alarms.class).equalTo("alarmTime", setTime).findAll();
                results.deleteAllFromRealm();
            });
            stopAlarm();
            Intent startAwakeActivity = new Intent(this, AwakeActivity.class);
            //To navigate back to the main activity.
            TaskStackBuilder.create(this).addNextIntentWithParentStack(startAwakeActivity).startActivities();
        } else {
            //generate a new question if the user gives a wrong answer
            Toast.makeText(getApplicationContext(), "You get another chance to save yourself...", Toast.LENGTH_SHORT).show();
            generateQuestion();
        }
    }

    private void overrideSilentModeAndPlayAlarmTone(){
        Log.i("Inside","AudioManager");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //remeber what the user's volume was set to before we change it.
        userVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        //MediaPlayer to play the alarm tone.
        //The alarm tone is hard-coded and the mp3 file is stored in the raw folder.
        mediaPlayer = MediaPlayer.create(this, R.raw.howl_tone);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
    }

    private void stopAlarm(){
        //Reset the volume back to user's settings after stopping the alarm
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, userVolume, AudioManager.FLAG_PLAY_SOUND);
        mediaPlayer.stop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch( event.getKeyCode() ) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(getApplicationContext(), "You can't Escape!", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_POWER:
                return  true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    //app stays on the same activity when back button is pressed
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "You can't Escape!", Toast.LENGTH_SHORT).show();
        Log.i("Toast", "After alarm dismissed");
    }

    @Override
    //app is brought back to foreground when recent apps button is pressed
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
        Log.i("Inside", "onpause");

    }

}



