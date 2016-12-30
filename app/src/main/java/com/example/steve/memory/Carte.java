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
    Integer numeroCarte;

    public Carte(Bitmap vueCarte, Bitmap rectoCarte, Bitmap versoCarte, Integer numeroCarte){
        this.vueCarte = vueCarte;
        this.rectoCarte = rectoCarte;
        this.versoCarte = versoCarte;
        this.numeroCarte = numeroCarte;
    }

}
