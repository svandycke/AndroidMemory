package com.example.steve.memory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Steve on 28/11/2016.
 */

public class PlayView extends SurfaceView implements SurfaceHolder.Callback, Runnable  {

    Thread  cv_thread = null;
    SurfaceHolder holder;
    private boolean in = true;


    private Resources 	mRes;
    private Context 	mContext;

    ArrayList<Integer> cartesDisponnibles = new ArrayList<Integer>();
    ArrayList<Carte> cartes = new ArrayList<Carte>();
    ArrayList<Integer> cartesTouchees = new ArrayList<Integer>();
    Bitmap recto;
    int tailleImage;
    int tailleMarge;
    int nbPairesTrouvees=0;
    int nbCoups=0;
    MediaPlayer mediaPlayer;
    Boolean soundClick;
    Boolean redimenssionne = false;
    long GameTimeMax = 180000;
    long timeLeft = GameTimeMax;
    boolean gameIsFinish = false;
    CountDownTimer counterTimeLeft = null;
    CountDownTimer counterReturnCard = null;
    String namePlayer;


    boolean canPlay = true;

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
        recto       = BitmapFactory.decodeResource(mRes, R.drawable.recto1);

        // Charge le son Click
        int resID = getResources().getIdentifier("click", "raw", mContext.getPackageName());
        mediaPlayer = MediaPlayer.create(this.mContext,resID);

        // Récupère les paramètres
        SharedPreferences prefs = mContext.getSharedPreferences("params", Context.MODE_PRIVATE);
        soundClick = prefs.getBoolean("soundClick", true);

        // creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);

        // Initialisation des paramètres
        initparameters();
    }
    // Callback : cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

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
        while (in == true) {
            //-- On peut dessiner si le holder est disponible
            if (!holder.getSurface().isValid())
                continue;
            //-- Définition d'un canevas, et veroullage le temps que l'on dessine dessus
            Canvas c = holder.lockCanvas();
            nDraw(c);
            holder.unlockCanvasAndPost(c); //-- Libération du dessin

            // Pour dessiner à 50 fps
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {}
        }
    }

    // dessin du jeu
    private void nDraw(Canvas canvas) {

        if(in == true) {
            if (!redimenssionne) {
                // Détermination de la taille des images et des marges
                tailleImage = (canvas.getWidth() / 5);
                tailleMarge = (tailleImage / 3);

                redimenssionne_image(tailleImage);
                redimenssionne = true;
            }

            int carte = 0;
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 4; k++) {
                    canvas.drawBitmap(cartes.get(carte).vueCarte, ((tailleImage + tailleMarge) * k), ((tailleImage + tailleMarge) * j), new Paint());
                    carte++;
                }
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

    // Fonction qui compare une paire de carte
    public boolean comparePaire(){

        if(cartes.get(cartesTouchees.get(0)).numeroCarte == cartes.get(cartesTouchees.get(1)).numeroCarte){
            return true;
        }else{
            return false;
        }
    }

    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {

        if (canPlay && !gameIsFinish) {
            int carteTouche = 20;

            // Première ligne
            if (event.getX() <= tailleImage && (event.getY() <= tailleImage)) {
                carteTouche = 0;
                cartes.get(0).vueCarte = cartes.get(0).versoCarte;
            } else if (event.getX() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getX() <= ((tailleImage * 2) + (tailleMarge * 1)) && (event.getY() <= tailleImage)) {
                carteTouche = 1;
            } else if (event.getX() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getX() <= ((tailleImage * 3) + (tailleMarge * 2)) && (event.getY() <= tailleImage)) {
                carteTouche = 2;
            } else if (event.getX() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getX() <= ((tailleImage * 4) + (tailleMarge * 3)) && (event.getY() <= tailleImage)) {
                carteTouche = 3;
            }

            // Deuxième ligne

            else if (event.getX() <= tailleImage && event.getY() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getY() <= ((tailleImage * 2) + (tailleMarge * 1))) {
                carteTouche = 4;
            } else if (event.getX() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getX() <= ((tailleImage * 2) + (tailleMarge * 1)) && event.getY() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getY() <= ((tailleImage * 2) + (tailleMarge * 1))) {
                carteTouche = 5;
            } else if (event.getX() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getX() <= ((tailleImage * 3) + (tailleMarge * 2)) && event.getY() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getY() <= ((tailleImage * 2) + (tailleMarge * 1))) {
                carteTouche = 6;
            } else if (event.getX() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getX() <= ((tailleImage * 4) + (tailleMarge * 3)) && event.getY() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getY() <= ((tailleImage * 2) + (tailleMarge * 1))) {
                carteTouche = 7;
            }

            // Troisième ligne

            else if (event.getX() <= tailleImage && event.getY() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getY() <= ((tailleImage * 3) + (tailleMarge * 2))) {
                carteTouche = 8;
            } else if (event.getX() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getX() <= ((tailleImage * 2) + (tailleMarge * 1)) && event.getY() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getY() <= ((tailleImage * 3) + (tailleMarge * 2))) {
                carteTouche = 9;
            } else if (event.getX() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getX() <= ((tailleImage * 3) + (tailleMarge * 2)) && event.getY() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getY() <= ((tailleImage * 3) + (tailleMarge * 2))) {
                carteTouche = 10;
            } else if (event.getX() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getX() <= ((tailleImage * 4) + (tailleMarge * 3)) && event.getY() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getY() <= ((tailleImage * 3) + (tailleMarge * 2))) {
                carteTouche = 11;
            }

            // Quatrième ligne

            else if (event.getX() <= tailleImage && event.getY() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getY() <= ((tailleImage * 4) + (tailleMarge * 3))) {
                carteTouche = 12;
            } else if (event.getX() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getX() <= ((tailleImage * 2) + (tailleMarge * 1)) && event.getY() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getY() <= ((tailleImage * 4) + (tailleMarge * 3))) {
                carteTouche = 13;
            } else if (event.getX() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getX() <= ((tailleImage * 3) + (tailleMarge * 2)) && event.getY() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getY() <= ((tailleImage * 4) + (tailleMarge * 3))) {
                carteTouche = 14;
            } else if (event.getX() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getX() <= ((tailleImage * 4) + (tailleMarge * 3)) && event.getY() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getY() <= ((tailleImage * 4) + (tailleMarge * 3))) {
                carteTouche = 15;
            }

            // Cinquième ligne

            else if (event.getX() <= tailleImage && event.getY() >= ((tailleImage * 4) + (tailleMarge * 4)) && event.getY() <= ((tailleImage * 5) + (tailleMarge * 4))) {
                carteTouche = 16;
            } else if (event.getX() >= ((tailleImage * 1) + (tailleMarge * 1)) && event.getX() <= ((tailleImage * 2) + (tailleMarge * 1)) && event.getY() >= ((tailleImage * 4) + (tailleMarge * 4)) && event.getY() <= ((tailleImage * 5) + (tailleMarge * 4))) {
                carteTouche = 17;
            } else if (event.getX() >= ((tailleImage * 2) + (tailleMarge * 2)) && event.getX() <= ((tailleImage * 3) + (tailleMarge * 2)) && event.getY() >= ((tailleImage * 4) + (tailleMarge * 4)) && event.getY() <= ((tailleImage * 5) + (tailleMarge * 4))) {
                carteTouche = 18;
            } else if (event.getX() >= ((tailleImage * 3) + (tailleMarge * 3)) && event.getX() <= ((tailleImage * 4) + (tailleMarge * 3)) && event.getY() >= ((tailleImage * 4) + (tailleMarge * 4)) && event.getY() <= ((tailleImage * 5) + (tailleMarge * 4))) {
                carteTouche = 19;
            }

            if(carteTouche !=20 && cartes.get(carteTouche).active){

                if(nbCoups==0){
                    counterTimeLeft = new CountDownTimer(timeLeft, 1000) {

                        public void onTick(long millisUntilFinished) {
                            setTimeLeft(millisUntilFinished);
                            timeLeft = millisUntilFinished;
                        }

                        public void onFinish() {
                            gameIsFinish = true;
                            gameIsFinish();
                        }
                    }.start();

                }

                if(cartesTouchees.size() == 0){
                    if(soundClick)
                        mediaPlayer.start();
                    cartesTouchees.add(carteTouche);
                    cartes.get(carteTouche).vueCarte = cartes.get(carteTouche).versoCarte;
                    nbCoups++;
                }else if(cartesTouchees.size() == 1){
                    if(cartesTouchees.get(0) != carteTouche){
                        if(soundClick)
                            mediaPlayer.start();
                        cartesTouchees.add(carteTouche);
                        cartes.get(carteTouche).vueCarte = cartes.get(carteTouche).versoCarte;
                        nbCoups++;
                    }
                }

                if (cartesTouchees.size() == 2) {
                    if (comparePaire()) {

                        cartes.get(cartesTouchees.get(0)).active = false;
                        cartes.get(cartesTouchees.get(1)).active = false;

                        cartesTouchees.clear();
                        nbPairesTrouvees++;
                        canPlay = true;
                        Log.i("-> FCT <-", "BRAVO");
                    } else {
                        Log.i("-> FCT <-", "PERDU");

                        canPlay = false;
                        counterReturnCard = new CountDownTimer(1500, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                cartes.get(cartesTouchees.get(0)).vueCarte = cartes.get(cartesTouchees.get(0)).rectoCarte;
                                cartes.get(cartesTouchees.get(1)).vueCarte = cartes.get(cartesTouchees.get(1)).rectoCarte;
                                cartesTouchees.clear();
                                canPlay = true;
                                counterReturnCard = null;
                            }
                        }.start();
                    }
                }


                // Met à jour le label pour afficher le nombre de coups
                ((PlayActivity) getContext()).setNbCoups("Nombre de coups : "+ nbCoups);
            }

            gameIsFinish();

        }

        return super.onTouchEvent(event);
    }

    public void initparameters() {
        nbCoups = 0;
        nbPairesTrouvees = 0;
        cartes.clear();
        cartesTouchees.clear();
        cartesDisponnibles.clear();
        canPlay = true;
        redimenssionne = false;
        gameIsFinish = false;

        repartitionCartes();
        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
        }

        in=true;
    }

    // Fonction qui réparti les carte
    private void repartitionCartes(){
        int numero;
        for(int i=1; i<=20; i++){

            if(i>10){numero = (i-10);}else{numero = i;}

            cartesDisponnibles.add(numero);
        }

        for(int z=0; z<20; z++){
            int nbCarteDispo = cartesDisponnibles.size();
            Random r = new Random();
            int aleatoire = r.nextInt(nbCarteDispo);

            Bitmap vueCarte = recto;
            Bitmap rectoCarte = recto;

            int resID = getResources().getIdentifier("animaux" + cartesDisponnibles.get(aleatoire) , "drawable", mContext.getPackageName());
            Bitmap versoCarte = BitmapFactory.decodeResource(mRes, resID);

            Carte carte = new Carte(vueCarte, rectoCarte, versoCarte, cartesDisponnibles.get(aleatoire));
            cartes.add(carte);

            cartesDisponnibles.remove(aleatoire);
        }
    }

    // Fonction qui est appelé pour redimenssionner les images
    private void redimenssionne_image(int taille){
        for(int e=0; e<cartes.size(); e++){
            cartes.get(e).vueCarte = BITMAP_RESIZER(cartes.get(e).vueCarte,(tailleImage),(tailleImage));
            cartes.get(e).rectoCarte = BITMAP_RESIZER(cartes.get(e).rectoCarte,(tailleImage),(tailleImage));
            cartes.get(e).versoCarte = BITMAP_RESIZER(cartes.get(e).versoCarte,(tailleImage),(tailleImage));
        }
    }

    // Fonction qui affiche le temps restant
    private void setTimeLeft(long timeleft){


        long secondes = (timeleft/1000)%60;
        long minutes = (timeleft/1000)/60;

        String text = String.format("Temps restant : %02d:%02d",minutes,secondes);

        // Met à jour le label pour afficher le nombre de coups
        ((PlayActivity) getContext()).setTimeLeft(text);


    }

    // Fonction qui indique que la partie est terminée
    private void gameIsFinish(){
        if(nbPairesTrouvees == 10){
            if(counterTimeLeft != null){
                counterTimeLeft.cancel();
            }
            gameIsFinish = true;

            if(isBestScore()){
                addBestScore();
            }else {

                Long timeElasped = (GameTimeMax - timeLeft);
                long secondes = (timeElasped/1000)%60;
                long minutes = (timeElasped/1000)/60;
                String timeString = String.format("%02d:%02d",minutes,secondes);

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Partie terminée");
                alertDialog.setMessage("Nombre de coups : " + nbCoups + "\nTemps écoulé : " + timeString);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Retour à l'accueil",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                in = false;

                                Intent intent = new Intent(mContext, MainActivity.class);
                                mContext.startActivity(intent);

                            }
                        });
                alertDialog.show();
            }
        }else if(gameIsFinish && nbPairesTrouvees < 10){

            if(counterReturnCard != null){
                counterReturnCard.cancel();
            }

            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("Vous avez perdu");
            alertDialog.setMessage("Le temps restant est écoulé");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Retour à l'accueil",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            in = false;

                            Intent intent = new Intent(mContext, MainActivity.class);
                            mContext.startActivity(intent);

                        }
                    });
            alertDialog.show();
        }
    }

    // Fonction qui arrête le timer
    public void stopTimer(){
        if(counterTimeLeft != null){
            counterTimeLeft.cancel();
        }

        if(counterReturnCard != null){
            counterReturnCard.cancel();
        }
    }

    public boolean isBestScore() {

        final SharedPreferences data = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = data.edit();

        // Récupère les paramètres
        String json = data.getString("bestScores", null);

        if (json != null) {

            Type listType = new TypeToken<ArrayList<BestScore>>() {
            }.getType();
            List<BestScore> bestScoresSave = new Gson().fromJson(json, listType);

            BestScore lastMeilleurScoreSave = bestScoresSave.get((bestScoresSave.size() - 1));


            if (nbCoups < lastMeilleurScoreSave.nbCoups) {
                return true;
            }
            else if  (nbCoups == lastMeilleurScoreSave.nbCoups) {
                if ((GameTimeMax - timeLeft) < lastMeilleurScoreSave.time) {
                    return true;
                }else{
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    public void addBestScore(){
        final SharedPreferences data = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = data.edit();



        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Nouveau meilleur score");

        // Set up the input
        final EditText input = new EditText(mContext);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!input.getText().toString().isEmpty()){
                    namePlayer = input.getText().toString();
                }else {
                    namePlayer = "Anonyme";
                }

                // Récupère les paramètres
                String json = data.getString("bestScores", null);

                if(json != null){

                    Type listType = new TypeToken<ArrayList<BestScore>>(){}.getType();
                    List<BestScore> bestScoresSave = new Gson().fromJson(json, listType);

                    Long timeElasped = (GameTimeMax - timeLeft);
                    long secondes = (timeElasped/1000)%60;
                    long minutes = (timeElasped/1000)/60;
                    String timeString = String.format("%02d:%02d",minutes,secondes);

                    BestScore nouveau = new BestScore(namePlayer,nbCoups, timeElasped, timeString);

                    bestScoresSave.add(nouveau);

                    // Serialisation
                    Gson gson = new Gson();
                    json = gson.toJson(bestScoresSave);

                    editor.putString("bestScores", json);
                    editor.commit();
                }else{

                    // Ajout du premier meilleur score
                    ArrayList<BestScore> bestScores = new ArrayList<BestScore>();

                    Long timeElasped = (GameTimeMax - timeLeft);
                    long secondes = (timeElasped/1000)%60;
                    long minutes = (timeElasped/1000)/60;
                    String timeString = String.format("%02d:%02d",minutes,secondes);

                    BestScore nouveau = new BestScore(namePlayer,nbCoups, timeElasped, timeString);

                    bestScores.add(nouveau);

                    // Serialisation
                    Gson gson = new Gson();
                    json = gson.toJson(bestScores);

                    editor.putString("bestScores", json);
                    editor.commit();
                }

                Long timeElasped = (GameTimeMax - timeLeft);
                long secondes = (timeElasped/1000)%60;
                long minutes = (timeElasped/1000)/60;
                String timeString = String.format("%02d:%02d",minutes,secondes);

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Partie terminée");
                alertDialog.setMessage("Nombre de coups : " + nbCoups + "\nTemps écoulé : " + timeString);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Retour à l'accueil",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                in = false;

                                Intent intent = new Intent(mContext, MainActivity.class);
                                mContext.startActivity(intent);

                            }
                        });
                alertDialog.show();
            }
        });

        builder.show();


    }

    public void getPlayerName(){


    }
}
