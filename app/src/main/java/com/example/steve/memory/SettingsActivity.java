package com.example.steve.memory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import static java.security.AccessController.getContext;


public class SettingsActivity extends AppCompatActivity {

    private Resources mRes;
    private Context 	mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext	= this;
        mRes 		= mContext.getResources();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Définition du titre de l'activité
        this.setTitle("Memory : Paramètres");

        // Activation de l'ActionBar
        setupActionBar();

        // Vérouillage du mode paysage
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Récupération de l'ID du bouton "Quitter l'application" et assignation de la fonction
        Button btnSettings = (Button) findViewById(R.id.ExitApplication);
        btnSettings.setOnClickListener(exitApplication);

        // Récupération de l'ID du bouton "Réinitialiser les scores" et assignation de la fonction
        Button btnResetBestScore = (Button) findViewById(R.id.ResetBestScores);
        btnResetBestScore.setOnClickListener(resetBestScores);


        Switch sound = (Switch)findViewById(R.id.switch1);
        final SharedPreferences paramsSave = getSharedPreferences("params", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = paramsSave.edit();
        sound.setChecked(paramsSave.getBoolean("soundClick", true));

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //commit prefs on change
                editor.putBoolean("soundClick", isChecked);
                editor.commit();

            }
        });



    }

    // Fonction qui quitte l'application
    View.OnClickListener exitApplication = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    // Fonction qui réinitialiser les meilleurs scores
    View.OnClickListener resetBestScores = new View.OnClickListener() {
        public void onClick(View v) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
            builder1.setMessage("Êtes-vous sûre de vouloir supprimer les meilleurs scores ?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Oui",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //put your code that needed to be executed when okay is clicked

                            final SharedPreferences data = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
                            final SharedPreferences.Editor editor = data.edit();

                            editor.putString("bestScores", null);
                            editor.commit();

                            dialog.cancel();


                        }
                    });
            builder1.setNegativeButton("Annuler",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    };

    // Activation de l'ActionBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Action lors d'un clique dans ActionBar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
