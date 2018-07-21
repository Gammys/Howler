package com.example.gouthamishivaprakash.howler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class SnoozeAlarmActivity extends AppCompatActivity {

    private TextView question;
    private EditText answer;
    private Button checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze_alarm);

        //to display the activity on lock screen
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.i("SnoozeAlarm","After PendingIntent");
        question = findViewById(R.id.textViewQuestion);
        answer = findViewById(R.id.editTextAnswer);
        checkButton = findViewById(R.id.checkButton);

        final int num1 = new Random().nextInt(100);
        final int num2 = new Random().nextInt(100);
        question.setText(Integer.toString(num1) + " + " + Integer.toString(num2));

        //MediaPlayer to play the alarm tone.
        //The alarm tone is hard-coded and the mp3 file is stored in the raw folder.
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.howl_tone);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to make sure the user types an answer
                if (answer.getText().toString().length() == 0)
                    Toast.makeText(getApplicationContext(), "Type an Answer!", Toast.LENGTH_SHORT).show();
                //to check if the entered answer is correct
                else if (Integer.parseInt(answer.getText().toString()) == (num1 + num2)) {
                    Toast.makeText(getApplicationContext(), "You escaped!\nYou are not my kill for the day!", Toast.LENGTH_SHORT).show();
                    DisplayAlarmMainActivity.stopAlarm();
                    mediaPlayer.stop();
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), "One more chance to save yourself...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //app stays on the same activity when back button is pressed
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "You can't Escape!", Toast.LENGTH_SHORT).show();
    }

}

