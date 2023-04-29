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
import android.widget.Toast;

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

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GestureDetector to detect gestures - Raven
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        // Set up FloatingActionButton for controlling volume - Raven
        fab = findViewById(R.id.volumeControl);
        flag = true;

        // Set up FloatingActionButton click listener - Raven
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    // Change FloatingActionButton icon to "volume off" - Raven
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_off_24));
                    flag = false;
                    // Mute the media player
                    mMediaPlayer.setVolume(0, 0);
                } else {
                    // Change FloatingActionButton icon to "volume up" - Raven
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_up_24));
                    flag = true;
                    // Unmute the media player - Raven
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

        // Get the camera ID for controlling the camera flashlight - Raven
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Initialize the Text-to-Speech (TTS) object - Alan
        tts = new TextToSpeech(this, status -> {
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
                    // Change FloatingActionButton icon to "volume off" - Raven
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_off_24));
                    flag = false;
                    // Mute the media player
                    mMediaPlayer.setVolume(0, 0);
                } else {
                    // Change FloatingActionButton icon to "volume up" - Raven
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_up_24));
                    flag = true;
                    // Unmute the media player - Raven
                    mMediaPlayer.setVolume(1, 1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create and start the media player - Raven
        mMediaPlayer = MediaPlayer.create(this, R.raw.dreamy);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);

        // maintains state of fab - Raven

        if (flag) {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_up_24));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_off_24));
            mMediaPlayer.setVolume(0, 0);

        }

        // Retrieve saved response from SharedPreferences - Raven
        mPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = mPrefs.getString("response", "");

        // Set the text view to display the saved response
        tvGravity.setText(s1);

        // Register the sensor listener
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);

        // Initialize the Text-to-Speech (TTS) object - Alan
        tts = new TextToSpeech(this, status -> {
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
        });

    }


    @Override
    protected void onPause() {
        super.onPause();

        // Stop and release the media player - Raven
        mMediaPlayer.stop();
        mMediaPlayer.release();
        manager.unregisterListener(this);
        mMediaPlayer = null;

        // Shutdown Text-to-Speech (TTS) -  Alan
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

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

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass the touch event to the GestureDetector -  Raven
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

//Raven
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

    // Speak the provided text using Text-to-Speech - Alan
    private void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            // Deprecated method for API levels lower than 21 (Android 5.0 Lollipop)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // Handle sensor changes - Alan
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Check if the device is facing up by comparing the z-axis value to 9.81f
        // (approximately equal to gravity)
        if (inRange(z, 9.81f, 0.01f)) {
            // If the device was previously flipped down, update the TextView with a
            // random answer and speak the answer using Text-to-Speech
            if (flippedDown) {
                tvGravity.setText(Answers.getRandomAnswer());
                flippedDown = false;
                speakAnswer();
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
            }
            // Stop any ongoing alerts (vibration, sound, and camera flash): comment out to stop errors on emulator - Raven
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                stopAlerts();
//            }
        }
        // Check if the device is facing down by comparing the x, y, or z-axis values
        // to -9.81f (approximately equal to gravity in the opposite direction)
        else if (inRange(x, -9.81f, 0.01f) || inRange(y, -9.81f, 0.01f) || inRange(z, -9.81f, 0.01f)) {
            // Update the TextView to indicate the device is facing down
            tvGravity.setText("Down");
            speak("Screen pointing down");
            flippedDown = true;
            // Start alerts (vibration, sound, and camera flash)- comment out to stop errors in emulator- Raven
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                startAlerts();
//            }
        }

    }





    // This method starts alerts (vibration, sound, and camera flash) when the device is facing down
    //Alan
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAlerts() {
        // Vibrate the device
        // Check if the device is running Android 8.0 (API level 26) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a one-shot vibration with a duration of 500ms and the default amplitude
            // This method is used for devices with API level 26 or higher
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // Create a one-shot vibration with a duration of 500ms
            // This method is used for devices with API level below 26
            vibrator.vibrate(500);
        }

        // Play the sound using the MediaPlayer instance
        mediaPlayer.start();

        // Turn on the camera flashlight
        // This feature requires the device to be running Android 6.0 (API level 23) or higher
        try {
            // Turn on the camera flashlight by setting the torch mode to 'true' for the camera with the specified ID
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            // Log any exception that occurs while trying to access the camera
            e.printStackTrace();
        }
    }


    // This method stops alerts (vibration, sound, and camera flash) when the device is not facing down
    //Alan
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void stopAlerts() {
        // Cancel any ongoing vibration
        vibrator.cancel();

        // Stop playing the sound using the MediaPlayer instance
        // Check if the media player is currently playing a sound
        if (mediaPlayer.isPlaying()) {
            // Pause the media player and reset its position to the beginning of the audio file
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }

        // Turn off the camera flashlight
        // This feature requires the device to be running Android 6.0 (API level 23) or higher
        try {
            // Turn off the camera flashlight by setting the torch mode to 'false' for the camera with the specified ID
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            // Log any exception that occurs while trying to access the camera
            e.printStackTrace();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }

    // This method checks if the given value is within the specified range - Raven
    // It takes the value, target value, and tolerance as parameters
    private boolean inRange(float value, float target, float tol) {
        // Returns true if the value is greater than or equal to (target - tol)
        // and less than or equal to (target + tol)
        return value >= target - tol && value <= target + tol;
    }

    // This method speaks the answer displayed on the TextView using Text-to-Speech (TTS) - Alan
    private void speakAnswer() {
        // Get the text from the TextView and store it in a variable 'answer'
        String answer = tvGravity.getText().toString();
        // Check if the TTS instance is not null and the answer is not empty
        if (tts != null && !answer.isEmpty()) {
            // Speak the answer using TTS with a flush queue mode,
            // meaning that it will clear any pending speech tasks before starting the new one
            tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }



}

