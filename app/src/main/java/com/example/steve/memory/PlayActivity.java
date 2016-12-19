package com.example.steve.memory;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class PlayActivity extends Activity {

    private PlayView PlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        PlayView = (PlayView)findViewById(R.id.PlayView);
        PlayView.setVisibility(View.VISIBLE);

        // Définition du titre de l'activité
        this.setTitle("Memory : Jouer");

        // Activation de l'ActionBar
        //setupActionBar();
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


    /*
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
    }*/
}
