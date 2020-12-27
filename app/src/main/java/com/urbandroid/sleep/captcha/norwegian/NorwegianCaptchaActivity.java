package com.urbandroid.sleep.captcha.norwegian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.urbandroid.sleep.captcha.CaptchaSupport;
import com.urbandroid.sleep.captcha.CaptchaSupportFactory;
import com.urbandroid.sleep.captcha.RemainingTimeListener;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

// this is the main captcha activity
public class NorwegianCaptchaActivity extends Activity {

    private final int NORWEGIAN = 1;
    private final int ENGLISH = 2;

    private CaptchaSupport captchaSupport; // include this in every captcha

    private final RemainingTimeListener remainingTimeListener = new RemainingTimeListener() {
        @Override
        public void timeRemain(int seconds, int aliveTimeout) {
            final TextView timeoutView = (TextView) findViewById(R.id.timeout);
            timeoutView.setText(seconds + "/" + aliveTimeout);
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        captchaSupport = CaptchaSupportFactory.create(this); // include this in every captcha, in onCreate()

        // show timeout in TextView with id "timeout"
        captchaSupport.setRemainingTimeListener(remainingTimeListener);

        // show difficulty in TextView with id "difficulty", read from captchaSupport.getDifficulty()
        final TextView difficultyView = (TextView) findViewById(R.id.difficulty);
        difficultyView.setText(getResources().getString(R.string.difficulty, captchaSupport.getDifficulty()));

        // read word list
        String csvfileString = this.getApplicationInfo().dataDir + File.separatorChar + "1000.csv";
        File csvfile = new File(csvfileString);
        List<String []> dictionary = new ArrayList<>();
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                        getResources().openRawResource(R.raw.common_words)))) {
            dictionary = reader.readAll();
        } catch (Exception e) {
            System.err.println("COULD NOT READ CSV DICT");
        }
        final TextView captchaTextView = (TextView) findViewById(R.id.captcha_text);
        final String question = dictionary.get(0)[NORWEGIAN];
        final String answer = dictionary.get(0)[ENGLISH];
        captchaTextView.setText(question);

        final EditText input_text = (EditText) findViewById(R.id.input_text);

        // send captchaSupport.solved() when correct string entered and "Done" button tapped
        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ReverseCaptchaText", question);
                Log.i("ReverseCaptchaInput", input_text.getText().toString());
                if (input_text.getText().toString().equals(answer)) {
                    captchaSupport.solved(); // .solved() broadcasts an intent back to Sleep as Android to let it know that captcha is solved
                    finish();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        captchaSupport = CaptchaSupportFactory
                .create(this, intent)
                .setRemainingTimeListener(remainingTimeListener);

    }

    private static String randomString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        Random generator = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = chars[generator.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final EditText input_text = (EditText) findViewById(R.id.input_text);
        input_text.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        captchaSupport.unsolved(); // .unsolved() broadcasts an intent back to AlarmAlertFullScreen that captcha was not solved
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        captchaSupport.alive(); // .alive() refreshes captcha timeout - intended to be sent on user interaction primarily, but can be called anytime anywhere
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captchaSupport.destroy();
    }
}
