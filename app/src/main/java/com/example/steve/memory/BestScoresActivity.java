package com.example.steve.memory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class BestScoresActivity extends AppCompatActivity {

    TableLayout tableBestScores;
    ArrayList<BestScore> bestScores = new ArrayList<BestScore>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestscores);

        // Définition du titre de l'activité
        this.setTitle("Memory : Meilleurs scores");

        // Activation de l'ActionBar
        setupActionBar();

        // Vérouillage du mode paysage
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Récupère les paramètres
        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        String json = data.getString("bestScores", null);

        // Désérialisation du json
        Type listType = new TypeToken<ArrayList<BestScore>>(){}.getType();
        List<BestScore> bestScores = new Gson().fromJson(json, listType);

        tableBestScores = (TableLayout)findViewById(R.id.tableBestScores);

        if(bestScores != null){

            for(int i=bestScores.size()-1; i>=0; i--){

                GradientDrawable border = new GradientDrawable();
                if(i%2 != 0) {
                    border.setColor(0xFFFFFFFF);
                }else{
                    border.setColor(0xe2e2e2);
                }
                TextView rowTextView = new TextView(this);
                rowTextView.setText(bestScores.get(i).name + " - " + bestScores.get(i).nbCoups +" coups - " + bestScores.get(i).timeString);
                rowTextView.setPadding(30,90,30,90);
                rowTextView.setTextSize(20);

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    rowTextView.setBackgroundDrawable(border);
                } else {
                    rowTextView.setBackground(border);
                }

                tableBestScores.addView(rowTextView);
            }
        }else{
            TextView rowTextView = new TextView(this);

            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);

            rowTextView.setPadding(30,90,30,90);
            rowTextView.setTextSize(20);
            rowTextView.setText("Aucun meilleur score.");
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                rowTextView.setBackgroundDrawable(border);
            } else {
                rowTextView.setBackground(border);
            }
            tableBestScores.addView(rowTextView);
        }
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
                Intent intent = new Intent(BestScoresActivity.this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
