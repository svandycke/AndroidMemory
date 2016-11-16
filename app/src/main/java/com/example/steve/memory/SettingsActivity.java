package com.example.steve.memory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Définition du titre de l'activité
        this.setTitle("Memory : Paramètres");

        // Récupération de l'ID du bouton "Quitter l'application" et assignation de la fonction
        Button btnSettings = (Button) findViewById(R.id.ExitApplication);
        btnSettings.setOnClickListener(exitApplication);

    }

    // Fonction qui quitte l'application
    View.OnClickListener exitApplication = new View.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", true);
            startActivity(intent);
            finish();
        }
    };
}
