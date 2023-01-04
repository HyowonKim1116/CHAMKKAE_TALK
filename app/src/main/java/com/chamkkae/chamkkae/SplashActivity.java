package com.chamkkae.chamkkae;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, MainActivity.class);
        MediaPlayer player = MediaPlayer.create(this, R.raw.sound);
        player.start();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            player.release();
            finish();
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}