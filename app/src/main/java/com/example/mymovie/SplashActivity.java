package com.example.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Menunggu beberapa detik lalu pindah ke MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pindah ke MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Tutup SplashActivity setelah pindah
            }
        }, 3000); // Menunggu selama 3 detik (3000 ms)
    }
}
