package com.example.steve.memory;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
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

    public void setNbCoups(final String txt){
        PlayActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                nbCoups.setText(txt);
            }
        });
    }

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
    }
    @Override
    protected void onResume() {
        super.onResume();
        PlayView.resume();
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
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}



