package com.example.magic8ball;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private boolean flag;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.dreamy);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;

    }
}