package com.example.reservasmedicasmobile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;


public class VideoActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // Configurar el VideoView
        VideoView videoView = findViewById(R.id.videoViewAyuda);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ayuda);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(mp -> videoView.start());

        videoView.setOnCompletionListener(mp -> {
            // Al finalizar el video, regresar a MainActivity
            Intent intent = new Intent(VideoActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Opcional: cierra la actividad actual
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(VideoActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_login) {
                Intent intent = new Intent(VideoActivity.this, login.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_servicios) {
                Intent intent = new Intent(VideoActivity.this, servicios.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        // Configurar el VideoView
        VideoView videoView = findViewById(R.id.videoViewAyuda);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ayuda);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

        // Configuración del BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(VideoActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_login) {
                Intent intent = new Intent(VideoActivity.this, login.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_servicios) {
                Intent intent = new Intent(VideoActivity.this, servicios.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }*/
}