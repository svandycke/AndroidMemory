package com.example.steve.memory;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Steve on 09/01/2017.
 */

public class BestScore {

    String name;
    long score;

    public BestScore(String name, long score){
        this.name = name;
        this.score = score;
    }
}
