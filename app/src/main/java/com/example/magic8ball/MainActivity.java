package com.example.magic8ball;

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

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager manager;
    TextView tvGravity;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    CameraManager cameraManager;
    String cameraId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
        mediaPlayer.release();
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