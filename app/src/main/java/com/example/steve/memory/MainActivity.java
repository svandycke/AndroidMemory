package com.example.steve.memory;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Définition du titre de l'activité
        this.setTitle("Memory : Accueil");

        // Récupération de l'ID du bouton "Setting" et assignation de la fonction
        Button btnPlay = (Button) findViewById(R.id.play);
        btnPlay.setOnClickListener(play);

        // Récupération de l'ID du bouton "Setting" et assignation de la fonction
        Button btnSettings = (Button) findViewById(R.id.settings);
        btnSettings.setOnClickListener(settings);

        // Récupération de l'ID du bouton "About" et assignation de la fonction
        Button btnAbout = (Button) findViewById(R.id.about);
        btnAbout.setOnClickListener(about);

        // Récupération de l'ID du bouton "About" et assignation de la fonction
        Button btnBestScores = (Button) findViewById(R.id.bestScores);
        btnBestScores.setOnClickListener(bestScores);

        // Vérouillage du mode paysage
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return;
        }
    }

    // Fonction qui démarre l'activé "Settings"
    View.OnClickListener settings = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    };

    // Fonction qui démarre l'activé "À propos"
    View.OnClickListener about = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
    };
    // Fonction qui démarre l'activé "Meilleurs scores"
    View.OnClickListener bestScores = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, BestScoresActivity.class);
            startActivity(intent);
        }
    };
    // Fonction qui démarre l'activé "Meilleurs scores"
    View.OnClickListener play = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,PlayActivity.class);
            startActivity(intent);
        }
    };
}
