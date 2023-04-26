package com.example.magic8ball;

import androidx.annotation.NonNull;
import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager manager;
    TextView tvGravity;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    CameraManager cameraManager;
    String cameraId;
    private boolean flag;
    private MediaPlayer mMediaPlayer;
    private GestureDetectorCompat mDetector;
//    private TextView responses;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

//        responses = findViewById(R.id.gravity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.volumeControl);
        flag = true;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_off_24));
                    flag = false;
                    mMediaPlayer.setVolume(0,0);
                }else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_volume_up_24));
                    flag = true;
                    mMediaPlayer.setVolume(1,1);
                }
            }
        });



        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        tvGravity = findViewById(R.id.gravity);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.spongebobsquarepants);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.dreamy);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);

        SharedPreferences mPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = mPrefs.getString("response", "");

        tvGravity.setText(s1);
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMediaPlayer.stop();
        mMediaPlayer.release();
        manager.unregisterListener(this);
        mMediaPlayer = null;

        SharedPreferences sharedPreferences =getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString("response", tvGravity.getText().toString());
        myEdit.apply();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
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

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            tvGravity.setText(Answers.random());
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            startActivity(new Intent(MainActivity.this, About.class));
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            tvGravity.setText("");
            return true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float g = event.values[2];

        if (inRange(g, 9.81f, 0.01f)) { // up!
            tvGravity.setText("Up");
            stopAlerts();
        } else if (inRange(g, -9.81f, 0.01f)) {
            tvGravity.setText("Down");
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
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }

        // Sound
        mediaPlayer.start();

        // Camera flash
        try {
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
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }

    private boolean inRange(float value, float target, float tol) {
        return value >= target - tol && value <= target + tol;

    }
}