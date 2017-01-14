package com.example.steve.memory;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;


public class PlayActivity extends AppCompatActivity{

    private PlayView PlayView;
    private TextView nbCoups;
    private TextView leftTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        PlayView = (PlayView)findViewById(R.id.PlayView);
        PlayView.setVisibility(View.VISIBLE);

        // Définition du titre de l'activité
        this.setTitle("Memory : Jouer");

        // Activation de l'ActionBar
        setupActionBar();

        // Vérouillage du mode paysage
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nbCoups = (TextView)findViewById(R.id.nbCoups);
        leftTime = (TextView)findViewById(R.id.leftTime);
    }

    // Fonction qui permet de mettre à jour le label "Nb coups restants"
    public void setNbCoups(final String txt){
        PlayActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                nbCoups.setText(txt);
            }
        });
    }

    // Fonction qui permet de mettre à jour le label "Temps restant"
    public void setTimeLeft(final String txt){
        PlayActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                leftTime.setText(txt);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayView.pause();
        PlayView.stopTimer();
    }
    @Override
    protected void onResume() {
        super.onResume();
        PlayView.resume();
    }

    @Override
    public void onBackPressed() {
    }


    // Activation de l'ActionBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Action lors d'un clique dans ActionBar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
             if((PlayView.nbCoupsMax - PlayView.nbCoups) > 0 && !PlayView.gameIsFinish){

                 if(PlayView.counterTimeLeft != null){
                     PlayView.counterTimeLeft.cancel();
                 }

                 AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                 builder1.setMessage("Voulez-vous quitter le jeu ?");
                 builder1.setCancelable(true);

                 builder1.setPositiveButton("Oui",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                                 Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                                 startActivity(intent);
                             }
                         });
                 builder1.setNegativeButton("Non",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 PlayView.startTimerLeftTime();
                                 dialog.cancel();
                             }
                         });

                 AlertDialog alert11 = builder1.create();
                 alert11.setCanceledOnTouchOutside(false);
                 alert11.show();

             }else{
                 Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                 startActivity(intent);
             }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}



