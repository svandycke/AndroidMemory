package com.example.steve.memory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import static android.R.attr.bitmap;
import static android.R.attr.width;
import static com.example.steve.memory.R.attr.height;
import static java.lang.System.in;

/**
 * Created by Steve on 28/11/2016.
 */

public class PlayView extends SurfaceView implements SurfaceHolder.Callback, Runnable  {

    private Thread  cv_thread;
    SurfaceHolder holder;
    private boolean in = true;


    private Resources 	mRes;
    private Context 	mContext;

    ArrayList<Bitmap> cartesDisponnibles = new ArrayList<Bitmap>();
    ArrayList<Carte> cartes = new ArrayList<Carte>();
    Bitmap recto;
    int tailleImage;
    int tailleMarge;
    int tailleCanvasWidth;
    int tailleCanvasHeight;
    int tailleEcranWidht;
    int tailleEcranHeight;

    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // Fond transparent SurfaceView
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mContext	= context;
        mRes 		= mContext.getResources();
        recto       = BitmapFactory.decodeResource(mRes, R.drawable.recto);

        // creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);

        // Initialisation des paramètres
        initparameters();


    }
    // Callback : cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {

    }


    public void surfaceDestroyed(SurfaceHolder arg0) {

    }

    public void pause() {
        in=false;
        //-- Tant que on est en Pause
        while (true) {
            try {
                cv_thread.join(); //--tente de relancer le Thread
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            break;
        }
        cv_thread=null;
    }

    public void resume() {
        //-- Zone de deessin disponible
        in=true;
        //-- On peut dessiner, donc on créé un Tread pour dessiner !
        cv_thread = new Thread(this); //-- This appele ici la method run() de la class
        cv_thread.start();
    }

    /**
     * run (run du thread créé)
     * on endort le thread, on modifie le compteur d'animation, on prend la main pour dessiner et on dessine puis on libère le canvas
     */
    public void run() {
        Canvas c = null;
        while (in) {
            try {
                cv_thread.sleep(40);
                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN");
            }
        }
    }

    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {

        // Détermination de la taille des images et des marges
        tailleImage = (canvas.getWidth()/5);
        tailleMarge = (tailleImage/3);

        // Taille du canvas
        tailleCanvasWidth = canvas.getHeight();
        tailleCanvasHeight = canvas.getHeight();

        // Taille écran
        tailleEcranWidht = getWidth();
        tailleEcranHeight = getHeight();



        //Log.i("-> INFO <-", "TailleImage = "+tailleImage +" / TailleMarge = " +tailleMarge);

        int carte = 0;
        for(int j=0; j<5; j++){
            for(int k=0; k<4; k++){
                Bitmap image = BITMAP_RESIZER(cartes.get(carte).vueCarte,(tailleImage),(tailleImage));
                canvas.drawBitmap(image, ((tailleImage + tailleMarge)*k), ((tailleImage + tailleMarge)*j), new Paint());
                carte++;
            }
        }
    }

    // Fonction qui permet de redimensionner un bitmap
    public Bitmap BITMAP_RESIZER(Bitmap bitmap,int newWidth,int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {
        Log.i("-> FCT <-", "Toucher X : "+ event.getX() + " / Y : " + event.getY());

        // Première ligne

        if(event.getX() <= tailleImage && (event.getY() <= tailleImage)){
            cartes.get(0).vueCarte = cartes.get(0).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*1) + (tailleMarge*1)) && event.getX() <= ((tailleImage*2) + (tailleMarge*1)) && (event.getY() <= tailleImage)){
            cartes.get(1).vueCarte = cartes.get(1).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*2) + (tailleMarge*2)) && event.getX() <= ((tailleImage*3) + (tailleMarge*2)) && (event.getY() <= tailleImage)){
            cartes.get(2).vueCarte = cartes.get(2).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*3) + (tailleMarge*3)) && event.getX() <= ((tailleImage*4) + (tailleMarge*3)) && (event.getY() <= tailleImage)){
            cartes.get(3).vueCarte = cartes.get(3).versoCarte;
        }

        // Deuxième ligne

        else if(event.getX() <= tailleImage && event.getY() >= ((tailleImage*1) + (tailleMarge*1)) && event.getY() <= ((tailleImage*2) + (tailleMarge*1))){
            cartes.get(4).vueCarte = cartes.get(4).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*1) + (tailleMarge*1)) && event.getX() <= ((tailleImage*2) + (tailleMarge*1)) && event.getY() >= ((tailleImage*1) + (tailleMarge*1)) && event.getY() <= ((tailleImage*2) + (tailleMarge*1))){
            cartes.get(5).vueCarte = cartes.get(5).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*2) + (tailleMarge*2)) && event.getX() <= ((tailleImage*3) + (tailleMarge*2)) && event.getY() >= ((tailleImage*1) + (tailleMarge*1)) && event.getY() <= ((tailleImage*2) + (tailleMarge*1))){
            cartes.get(6).vueCarte = cartes.get(6).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*3) + (tailleMarge*3)) && event.getX() <= ((tailleImage*4) + (tailleMarge*3)) && event.getY() >= ((tailleImage*1) + (tailleMarge*1)) && event.getY() <= ((tailleImage*2) + (tailleMarge*1))){
            cartes.get(7).vueCarte = cartes.get(7).versoCarte;
        }

        // Troisième ligne

        else if(event.getX() <= tailleImage && event.getY() >= ((tailleImage*2) + (tailleMarge*2)) && event.getY() <= ((tailleImage*3) + (tailleMarge*2))){
            cartes.get(8).vueCarte = cartes.get(8).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*1) + (tailleMarge*1)) && event.getX() <= ((tailleImage*2) + (tailleMarge*1)) && event.getY() >= ((tailleImage*2) + (tailleMarge*2)) && event.getY() <= ((tailleImage*3) + (tailleMarge*2))){
            cartes.get(9).vueCarte = cartes.get(9).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*2) + (tailleMarge*2)) && event.getX() <= ((tailleImage*3) + (tailleMarge*2)) && event.getY() >= ((tailleImage*2) + (tailleMarge*2)) && event.getY() <= ((tailleImage*3) + (tailleMarge*2))){
            cartes.get(10).vueCarte = cartes.get(10).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*3) + (tailleMarge*3)) && event.getX() <= ((tailleImage*4) + (tailleMarge*3)) && event.getY() >= ((tailleImage*2) + (tailleMarge*2)) && event.getY() <= ((tailleImage*3) + (tailleMarge*2))){
            cartes.get(11).vueCarte = cartes.get(11).versoCarte;
        }

        // Quatrième ligne

        else if(event.getX() <= tailleImage && event.getY() >= ((tailleImage*3) + (tailleMarge*3)) && event.getY() <= ((tailleImage*4) + (tailleMarge*3))){
            cartes.get(12).vueCarte = cartes.get(12).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*1) + (tailleMarge*1)) && event.getX() <= ((tailleImage*2) + (tailleMarge*1)) && event.getY() >= ((tailleImage*3) + (tailleMarge*3)) && event.getY() <= ((tailleImage*4) + (tailleMarge*3))){
            cartes.get(13).vueCarte = cartes.get(13).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*2) + (tailleMarge*2)) && event.getX() <= ((tailleImage*3) + (tailleMarge*2)) && event.getY() >= ((tailleImage*3) + (tailleMarge*3)) && event.getY() <= ((tailleImage*4) + (tailleMarge*3))){
            cartes.get(14).vueCarte = cartes.get(14).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*3) + (tailleMarge*3)) && event.getX() <= ((tailleImage*4) + (tailleMarge*3)) && event.getY() >= ((tailleImage*3) + (tailleMarge*3)) && event.getY() <= ((tailleImage*4) + (tailleMarge*3))){
            cartes.get(15).vueCarte = cartes.get(15).versoCarte;
        }

        // Cinquième ligne

        else if(event.getX() <= tailleImage && event.getY() >= ((tailleImage*4) + (tailleMarge*4)) && event.getY() <= ((tailleImage*5) + (tailleMarge*4))){
            cartes.get(16).vueCarte = cartes.get(16).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*1) + (tailleMarge*1)) && event.getX() <= ((tailleImage*2) + (tailleMarge*1)) && event.getY() >= ((tailleImage*4) + (tailleMarge*4)) && event.getY() <= ((tailleImage*5) + (tailleMarge*4))){
            cartes.get(17).vueCarte = cartes.get(17).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*2) + (tailleMarge*2)) && event.getX() <= ((tailleImage*3) + (tailleMarge*2)) && event.getY() >= ((tailleImage*4) + (tailleMarge*4)) && event.getY() <= ((tailleImage*5) + (tailleMarge*4))){
            cartes.get(18).vueCarte = cartes.get(18).versoCarte;
        }
        else if(event.getX() >= ((tailleImage*3) + (tailleMarge*3)) && event.getX() <= ((tailleImage*4) + (tailleMarge*3)) && event.getY() >= ((tailleImage*4) + (tailleMarge*4)) && event.getY() <= ((tailleImage*5) + (tailleMarge*4))){
            cartes.get(19).vueCarte = cartes.get(19).versoCarte;
        }

        return super.onTouchEvent(event);
    }

    public void initparameters() {
        repartitionCartes();
        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }

    // Fonction qui réparti les carte
    private void repartitionCartes(){
        int numero;
        for(int i=1; i<=20; i++){

            if(i>10){numero = (i-10);}else{numero = i;}

            int resID = getResources().getIdentifier("animaux" + numero , "drawable", mContext.getPackageName());
            cartesDisponnibles.add(BitmapFactory.decodeResource(mRes, resID));
        }

        for(int z=0; z<20; z++){
            int nbCarteDispo = cartesDisponnibles.size();
            Random r = new Random();
            int aleatoire = r.nextInt(nbCarteDispo);

            Bitmap vueCarte = recto;
            Bitmap rectoCarte = recto;
            Bitmap versoCarte = cartesDisponnibles.get(aleatoire);

            Carte carte = new Carte(vueCarte, rectoCarte, versoCarte);
            cartes.add(carte);

            cartesDisponnibles.remove(aleatoire);
        }
    }

}
