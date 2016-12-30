package com.example.steve.memory;

import android.graphics.Bitmap;

/**
 * Created by Steve on 30/12/2016.
 */

public class Carte {

    Bitmap vueCarte;
    Bitmap rectoCarte;
    Bitmap versoCarte;
    Boolean dejaretourne;

    public Carte(Bitmap vueCarte, Bitmap rectoCarte, Bitmap versoCarte){
        this.vueCarte = vueCarte;
        this.rectoCarte = rectoCarte;
        this.versoCarte = versoCarte;
    }

}
