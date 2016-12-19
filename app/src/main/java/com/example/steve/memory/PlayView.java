package com.example.steve.memory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static java.lang.System.in;

/**
 * Created by Steve on 28/11/2016.
 */

public class PlayView extends SurfaceView implements SurfaceHolder.Callback, Runnable  {

    private Thread  cv_thread;
    SurfaceHolder holder;
    private boolean in = true;
    private int TestThread = 0;

    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);


        // creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);

        initparameters();

    }
    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initparameters();
        Log.i("-> FCT <-", "surfaceChanged "+ width +" - "+ height);
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
    }


    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
    }

    public void pause() {
        in=false;
        //-- Tant que on est en Pause
        while (true) {
            try {
                cv_thread.join(); //--tente de relancer le Thread
            }
            catch (InterruptedException e)
            {e.printStackTrace();}
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

            TestThread = (TestThread+1);
            Log.i("Information",String.valueOf(TestThread));

            try {
                cv_thread.sleep(40);
                try {
                    c = holder.lockCanvas(null);
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

    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {
        Log.i("-> FCT <-", "onTouchEvent: "+ event.getX());
        if (event.getY()<50) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_UP, null);
        } else if (event.getY()>getHeight()-50) {
            if (event.getX()>getWidth()-50) {
                onKeyDown(KeyEvent.KEYCODE_0, null);
            } else {
                onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, null);
            }
        } else if (event.getX()<50) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
        } else if (event.getX()>getWidth()-50) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        }
        return super.onTouchEvent(event);
    }

    public void initparameters() {
        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }


}
