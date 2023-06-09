package com.example.magic8ball;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Declare necessary variables
    SensorManager manager;
    TextView tvGravity;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    CameraManager cameraManager;
    String cameraId;
    private boolean flag;
    private MediaPlayer mMediaPlayer;
    private GestureDetectorCompat mDetector;
    private TextToSpeech tts;
    private boolean flippedDown = false;
    private FloatingActionButton fab;
    private SharedPreferences mPrefs;
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GestureDetector to detect gestures
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        // Set up FloatingActionButton for controlling volume
        FloatingActionButton fab = findViewById(R.id.volumeControl);
        flag = true;

        // Set up FloatingActionButton click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    // Change FloatingActionButton icon to "volume off"
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_off_24));
                    flag = false;
                    // Mute the media player
                    mMediaPlayer.setVolume(0, 0);
                } else {
                    // Change FloatingActionButton icon to "volume up"
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_up_24));
                    flag = true;
                    // Unmute the media player
                    mMediaPlayer.setVolume(1, 1);
                }
            }
        });

        // Initialize SensorManager, TextView, Vibrator, MediaPlayer, and CameraManager
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        tvGravity = findViewById(R.id.gravity);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.spongebobsquarepants);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // Get the camera ID for controlling the camera flashlight
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Initialize the Text-to-Speech (TTS) object
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Check if TTS initialization is successful
                if (status == TextToSpeech.SUCCESS) {
                    // Set TTS language to US English
                    int result = tts.setLanguage(Locale.US);
                    // Check if the language is supported
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }

    // called when orientation changes
    // updates textsize and maitains state of fab - Raven
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);

        tvGravity = findViewById(R.id.gravity);
        // Set up FloatingActionButton for controlling volume - Raven
        fab = findViewById(R.id.volumeControl);
        if (flag) {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_up_24));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_off_24));
        }

        // Retrieve saved response from SharedPreferences - Raven
        mPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = mPrefs.getString("response", "");

        // Set the text view to display the saved response
        tvGravity.setText(s1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    // Change FloatingActionButton icon to "volume off"
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_off_24));
                    flag = false;
                    // Mute the media player
                    mMediaPlayer.setVolume(0, 0);
                } else {
                    // Change FloatingActionButton icon to "volume up"
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_up_24));
                    flag = true;
                    // Unmute the media player
                    mMediaPlayer.setVolume(1, 1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create and start the media player
        mMediaPlayer = MediaPlayer.create(this, R.raw.dreamy);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);

        // Retrieve saved response from SharedPreferences
        SharedPreferences mPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = mPrefs.getString("response", "");

        // Set the text view to display the saved response
        tvGravity.setText(s1);

        // Register the sensor listener
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop and release the media player
        mMediaPlayer.stop();
        mMediaPlayer.release();
        manager.unregisterListener(this);
        mMediaPlayer = null;

        // Shutdown Text-to-Speech (TTS)
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        // Save the current response to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString("response", tvGravity.getText().toString());
        myEdit.apply();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass the touch event to the GestureDetector
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    private class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        // Handle single tap gesture
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Get a random answer and update the TextView
            String answer = Answers.getRandomAnswer();
            tvGravity.setText(answer);

            // Save the current response to SharedPreferences - Raven
            // Get a reference to the SharedPreferences object using "MySharedPref" as the storage key
            sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

            // Create an editor object to modify the shared preferences
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            // Put the current text displayed in the TextView tvGravity into the shared preferences
            // Save it under the key "response"
            myEdit.putString("response", tvGravity.getText().toString());

            // Apply the changes to the shared preferences
            myEdit.apply();

            // Speak the answer using TTS
            speak(answer);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        // Handle long press gesture
        @Override
        public void onLongPress(MotionEvent e) {
            // Start the About activity
            startActivity(new Intent(MainActivity.this, About.class));
        }

        // Handle fling gesture
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Clear the TextView
            tvGravity.setText("");
            return true;
        }
    }

    // Speak the provided text using Text-to-Speech
    private void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // Handle sensor changes - starting code from Dr. Lutz
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Check if the device is facing up
        if (inRange(z, 9.81f, 0.01f)) {
            if (flippedDown) {
                // Get a random answer, update the TextView, and speak the answer
                tvGravity.setText(Answers.getRandomAnswer());
                flippedDown = false;
                speakAnswer(); // Add this line to activate Text-to-Speech
            }
            stopAlerts();
            // Check if the device is facing down
        } else if (inRange(x, -9.81f, 0.01f) || inRange(y, -9.81f, 0.01f) || inRange(z, -9.81f, 0.01f)) {
            tvGravity.setText("Down");
            flippedDown = true;
            startAlerts();
        } else {
            tvGravity.setText(" ");
            stopAlerts();
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAlerts() {
        // Vibration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a one-shot vibration for devices with API level 26 or higher
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // Create a one-shot vibration for devices with API level below 26
            vibrator.vibrate(500);
        }

        // Sound
        mediaPlayer.start();

        // Camera flash
        try {
            // Turn on the camera flashlight
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void stopAlerts() {
        // Stop vibration
        vibrator.cancel();

        // Stop sound
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }

        // Turn off camera flash
        try {
            // Turn off the camera flashlight
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }

    // Check if the given value is within the specified range
    private boolean inRange(float value, float target, float tol) {
        return value >= target - tol && value <= target + tol;
    }

    // Speak the answer displayed on the TextView using TTS
    private void speakAnswer() {
        String answer = tvGravity.getText().toString();
        if (tts != null && !answer.isEmpty()) {
            tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }


}

