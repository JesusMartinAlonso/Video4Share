package com.jesusmartin92.ffmpegandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


public class LoadActivity extends ActionBarActivity {
    protected boolean active = true;
    protected int splashTime = 3000;
    protected boolean terminadoSplash = false;
    protected boolean terminadaCargaBinario = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*Dimensiones de la imagen*/
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getSize(size);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            size.x = display.getWidth();
            size.y = display.getHeight();
        }
        int width = size.x;
        ImageView image = new ImageView(this);
        int id = getResources().getIdentifier("drawable/splash_screen" , null, this.getPackageName());
        image.setImageResource(id);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,-1);
        parms.gravity = 48;
        image.setLayoutParams(parms);


        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFFe2f0b5);
        layout.addView(image);


        setContentView(layout);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.commit();
            //Create app directory
           // File f = new File(getCacheDir(),File.separator+"Video4Share/");
            //f.mkdirs();
            //File myDir = new File(Environment.getExternalStorageDirectory(), "/Video4Share");
            //myDir.mkdir();
        }
    /*
        File folder = new File(Environment.getExternalStorageDirectory() + "/Video4Share");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }*/


        Thread splashThread = new Thread(){
            @Override
            public void run(){
                try{
                    int waited = 0;
                    while(active && (waited < splashTime)){
                        sleep(200);
                        if(active){
                            waited += 100;
                        }

                    }
                } catch(InterruptedException e){

                } finally{
                    terminadoSplash = true;
                    if(terminadaCargaBinario) {
                        Log.d("PRUEBA", "ABRIENDO MAIN ACTIVITY");
                        openApp();
                        finish();
                    }
                }

            }
        };
        splashThread.start();
        //Load binary
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                    Log.println(0, "PRUBEA", "Error");
                }

                @Override
                public void onSuccess() {
                    terminadaCargaBinario = true;
                    if(terminadoSplash){
                        openApp();
                    }
                    Log.d("PRUEBA", "CARGADO BINARIO CON EXITO");
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            Log.println(0, "TAG", "No soportado");
        }
    }



    private void openApp(){
        //finish();
        startActivity(new Intent(this,LoadVideoActivity.class));
    }


}
