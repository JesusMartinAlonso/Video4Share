package com.jesusmartin92.ffmpegandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.util.Calendar;


public class Reproductor extends ActionBarActivity {
    private static final int SELECT_VIDEO= 100;
    private static final int SHARE = 200;
    private Uri selectedVideo;
    private  VideoView videoView;
    private ProgressDialog progress;

    private boolean isShowingShareDialog;


    //Share dialog attributes
    private Dialog dialog;
    private ImageButton dialogPlayButton;
    private ImageButton dialogShareButton;

    //Cut video dialog attributes
    private Dialog cutVideoDialog1;
    private Dialog cutVideoDialog2;
    private NumberPicker numberPickerMinute1;
    private NumberPicker numberPickerSecond1;
    private NumberPicker numberPickerMinute2;
    private NumberPicker numberPickerSecond2;
    private Button accept1;
    private Button accept2;

    //SpeedUp/SlowDown dialog
    private Dialog speedUpDialog;






    private Comando comando;
    private String fileName;
    enum Comando{
        ToGIF, ToImage, removeAudio, speedUpVideo , slowDownVideo, cutVideo , toAVI ,toMP4 ,toMOV,toAAC
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        videoView =
                (VideoView) findViewById(R.id.videoView);


        this.selectedVideo = getIntent().getExtras().getParcelable("uri");
        isShowingShareDialog = false;

        dialog = new Dialog(Reproductor.this);
        dialog.setContentView(R.layout.share_custom_dialog);
        dialog.setTitle("Conversión realizada");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialogPlayButton = (ImageButton) dialog.findViewById(R.id.playButton);
        dialogShareButton = (ImageButton) dialog.findViewById(R.id.shareButton);
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        dialogCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        speedUpDialog = new Dialog(Reproductor.this);
        speedUpDialog.setContentView(R.layout.speedup_dialog);
        speedUpDialog.setTitle("Seleccionar opción");
        Button speedUpButton = (Button) speedUpDialog.findViewById(R.id.speedUp);
        speedUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.speedUpVideo;
                ejecutaComando(getComando(comando));
                speedUpDialog.dismiss();
            }
        });
        Button slowDownButton = (Button) speedUpDialog.findViewById(R.id.slowDown);
        slowDownButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.slowDownVideo;
                ejecutaComando(getComando(comando));
                speedUpDialog.dismiss();
            }
        });

        cutVideoDialog1 = new Dialog(Reproductor.this);
        cutVideoDialog1.setContentView(R.layout.time_picker_start);
        cutVideoDialog1.setTitle("Seleccionar inicio");
        numberPickerMinute1 = (NumberPicker)cutVideoDialog1.findViewById(R.id.numberPickerMinute);
        numberPickerMinute1.setMinValue(0);
        numberPickerMinute1.setMaxValue(59);
        numberPickerMinute1.setWrapSelectorWheel(false);
        numberPickerSecond1 = (NumberPicker)cutVideoDialog1.findViewById(R.id.numberPickerSecond);
        numberPickerSecond1.setMinValue(0);
        numberPickerSecond1.setMaxValue(59);
        numberPickerSecond1.setWrapSelectorWheel(false);
        accept1 = (Button) cutVideoDialog1.findViewById(R.id.botonAceptar);
        accept1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cutVideoDialog1.dismiss();
                cutVideoDialog2.show();
            }
        });

        cutVideoDialog2 = new Dialog(Reproductor.this);
        cutVideoDialog2.setContentView(R.layout.time_picker_end);
        cutVideoDialog2.setTitle("Seleccionar final");
        numberPickerMinute2 = (NumberPicker)cutVideoDialog2.findViewById(R.id.numberPickerMinute);
        numberPickerMinute2.setMinValue(0);
        numberPickerMinute2.setMaxValue(59);
        numberPickerMinute2.setWrapSelectorWheel(false);
        numberPickerSecond2 = (NumberPicker)cutVideoDialog2.findViewById(R.id.numberPickerSecond);
        numberPickerSecond2.setMinValue(0);
        numberPickerSecond2.setMaxValue(59);
        numberPickerSecond2.setWrapSelectorWheel(false);

        accept2 = (Button)cutVideoDialog2.findViewById(R.id.botonAceptar);
        accept2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cutVideoDialog2.dismiss();
                ejecutaComando(getComando(Comando.cutVideo));

            }
        });








        initVideo();
        FloatingActionButton toGIF = (FloatingActionButton) findViewById(R.id.toGIF);
        toGIF.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.ToGIF;
                String comandoAEjecutar = getComando(Comando.ToGIF);
                ejecutaComando(comandoAEjecutar);
                Toast.makeText(Reproductor.this, "To gif", Toast.LENGTH_LONG);
            }
        });
        FloatingActionButton extractImage = (FloatingActionButton) findViewById(R.id.extractImage);
        extractImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.ToImage;
                final String comandoAEjecutar = getComando(Comando.ToImage);
                new AlertDialog.Builder(Reproductor.this)
                        .setTitle("Extraer fotograma")
                        .setMessage("Pausa el video en el fotograma que quieres extraer")
                        .setPositiveButton("Extraer fotograma", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                ejecutaComando(comandoAEjecutar);
                            }
                        })
                        .setNegativeButton("Ir al video", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                Toast.makeText(Reproductor.this, "Extract Image", Toast.LENGTH_LONG);
            }
        });


        FloatingActionButton removeAudio = (FloatingActionButton) findViewById(R.id.removeAudio);
        removeAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.removeAudio;
                String comandoAEjecutar = getComando(Comando.removeAudio);
                ejecutaComando(comandoAEjecutar);
            }
        });

        FloatingActionButton slowDownVideo = (FloatingActionButton) findViewById(R.id.slowDownVideo);
        slowDownVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                speedUpDialog.show();
            }
        });


        FloatingActionButton cutVideo = (FloatingActionButton) findViewById(R.id.cutVideo);
        cutVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.cutVideo;
                //String comandoAEjecutar = getComando(Comando.cutVideo);
                //ejecutaComando(comandoAEjecutar);
                cutVideoDialog1.show();
            }
        });

        FloatingActionButton toMP4 = (FloatingActionButton) findViewById(R.id.toMP4);
        toMP4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.toMP4;
                ejecutaComando(getComando(Comando.toMP4));
            }
        });

        FloatingActionButton toAVI = (FloatingActionButton) findViewById(R.id.toAVI);
        toAVI.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.toAVI;
                ejecutaComando(getComando(Comando.toAVI));
            }
        });

        FloatingActionButton toMOV = (FloatingActionButton) findViewById(R.id.toMOV);
        toMOV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.toMOV;
                ejecutaComando(getComando(Comando.toMOV));
            }
        });

        FloatingActionButton toAAC = (FloatingActionButton) findViewById(R.id.toAAC);
        toAAC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                comando = Comando.toAAC;
                ejecutaComando(getComando(Comando.toAAC));
            }
        });



    }



    public void ejecutaComando(String comando){
        //new executeCommand().execute(comando);
        try {
            FFmpeg ffmpeg = FFmpeg.getInstance(this);
            progress = new ProgressDialog(this);
            progress.setIndeterminate(true);

            File folder = new File(Environment.getExternalStorageDirectory() + "/Video4Share");

            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            /*if (success) {

            } else {
                // Do something else on failure
            }*/

            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(comando, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("PRUBEA", "EMPEZANDO");
                    progress.setMessage("Comenzando tarea");
                    progress.show();
                }

                @Override
                public void onProgress(String message) {
                    progress.setMessage(message);
                    Log.d("PROGRESO", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("MAINACTIVITY",message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("PRUBEA", message);
                    progress.dismiss();
                    muestraOpciones();
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.println(0, "PRUBEA", "Error");
            // Handle if FFmpeg is already running
        }

    }

    /**
     * Recibe un enumerado y devuelve el comando correspondiente a ejecutar por FFMPEG
     * @param comando
     * @return
     */
    public String getComando(Comando comando){

        String stringCommand = "";

        Calendar c = Calendar.getInstance();
        long seconds = c.getTimeInMillis();
        fileName = Long.toString(seconds);
        //String filePath = getFilePathFromURI(this.selectedVideo);
        String filePath = getRealPathFromURI(selectedVideo);


        switch (comando){
            case ToGIF:
                fileName = fileName + ".gif";
                stringCommand = "-y -i " +  filePath + " -vf scale=320:-1 -t 10 -r 10 /sdcard/Video4Share/" + fileName;
                break;
            case ToImage:
                fileName = fileName +  ".jpeg";
               int milliseconds =  videoView.getCurrentPosition();
               double secondsv = (double) (milliseconds / 1000);
                stringCommand = "-y -i  " + filePath + " -r 1 -vframes 1 -ss  " +secondsv + " /sdcard/Video4Share/" + fileName ;
                break;

            case removeAudio:
                int i = filePath.indexOf(".");
                fileName = fileName + filePath.substring(i);
                stringCommand = "-y -i " + filePath + " -an /sdcard/Video4Share/" + fileName;
                break;

            case speedUpVideo:
                i = filePath.indexOf(".");
                fileName = fileName + filePath.substring(i);
                stringCommand = "-y -i " + filePath + " -strict -2  -an -vf setpts=0.125*PTS /sdcard/Video4Share/" + fileName;
                break;
            case slowDownVideo:
                i = filePath.indexOf(".");
                fileName = fileName + filePath.substring(i);
                stringCommand = "-y -i " + filePath + " -strict -2  -an -vf setpts=3.0*PTS /sdcard/Video4Share/" + fileName;
                break;

            case cutVideo:
                i = filePath.indexOf(".");
                fileName = fileName + filePath.substring(i);
                int startmin = numberPickerMinute1.getValue();
                int startsec = numberPickerSecond1.getValue();
                int endmin = numberPickerMinute2.getValue();
                int endsec = numberPickerSecond2.getValue();
                int minduration = endmin - startmin;
                int secduration = endsec - startsec;
                if(secduration < 0 ){
                    secduration += 60;
                    minduration -= 1;

                }
                stringCommand = "-y -i " + filePath + " -ss 00:"+startmin+":"+startsec+ " -codec copy -t 00:" + minduration + ":" + secduration +"  /sdcard/Video4Share/" + fileName;

                break;

            case toAVI:
                fileName = fileName + ".avi";
                stringCommand = "-y -i " +  filePath + "  -qscale 0  -strict -2 /sdcard/Video4Share/" + fileName;
                break;

            case toMP4:
                fileName = fileName + ".mp4";
                stringCommand = "-y -i " +  filePath + "  -qscale 0  -strict -2 /sdcard/Video4Share/" + fileName;
                break;

            case toMOV:
                fileName = fileName + ".mov";
                stringCommand = "-y -i " +  filePath + "  -qscale 0  -strict -2 /sdcard/Video4Share/" + fileName;
                break;

            case toAAC:
                fileName = fileName + ".aac";
                stringCommand = "-y -i " +  filePath + "  -qscale 0  -strict -2 /sdcard/Video4Share/" + fileName;
                break;

        }
        return stringCommand;
    }

    public String getFilePathFromURI(Uri uri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(
                uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    public String getRealPathFromURI(Uri contentUri)
    {
        String[] proj = { MediaStore.Audio.Media.DATA };
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null); //Since manageQuery is deprecated
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String path  = cursor.getString(column_index);
        if(path.contains("WhatsApp")) {
            //http://stackoverflow.com/questions/23654929/android-video-compression-using-ffmpeg
            //http://stackoverflow.com/questions/29602770/android-ffmpeg-path-with-spaces
            String []split = path.split("/");
            path = "/sdcard/WhatsApp/Media/WhatsApp Video/" + split[split.length-1];


        }

        return path;
    }
    public void muestraOpciones(){
        final Uri newVideo = Uri.parse("file://" + "/sdcard/Video4Share/" + fileName);

                dialogShareButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, newVideo);
                        if(comando == Comando.ToGIF){
                            shareIntent.setType("image/gif");
                        }else if(comando == Comando.ToImage){
                            shareIntent.setType("image/*");
                        }else if(comando == Comando.toAAC){
                            shareIntent.setType("audio/*");
                        }else{
                            shareIntent.setType("video/*");
                        }
                        startActivityForResult(Intent.createChooser(shareIntent, "Compartir"), SHARE);
                    }
                });

                dialogPlayButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        if(comando == Comando.ToGIF){
                            intent.setDataAndType(newVideo, "image/gif");
                        }else if(comando == Comando.ToImage){
                            intent.setDataAndType(newVideo, "image/*");
                        }else if(comando == Comando.toAAC){
                            intent.setDataAndType(newVideo, "audio/*");
                        }else {
                            intent.setDataAndType(newVideo, "video/*");
                        }
                        startActivityForResult(intent,SHARE);
                    }
                });
                dialog.show();



    }




    public void initVideo() {


        videoView.setVideoURI(this.selectedVideo);
        //MediaController mediaController = new MediaController(this);
        //mediaController.setAnchorView(videoView);
        //videoView.setMediaController(mediaController);

        final CustomMediaController mc = new CustomMediaController(this, videoView);
        mc.setMediaPlayer(videoView);
        mc.setAnchorView(videoView);
        videoView.setMediaController(mc);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mp) {
                                                mc.show();
                                            }
                                        });

        videoView.start();
    }

            public void abrirVideo() {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("video/*");
                startActivityForResult(photoPickerIntent, SELECT_VIDEO);
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent videoReturnedIntent) {
                super.onActivityResult(requestCode, resultCode, videoReturnedIntent);

                switch (requestCode) {
                    case SELECT_VIDEO:
                        if (resultCode == RESULT_OK) {
                            selectedVideo = videoReturnedIntent.getData();
                            //Open new Video
                            initVideo();
                        }
                        break;
                    case SHARE:
                        dialog.show();
                        break;

                }
            }

            public void compartir() {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, selectedVideo);
                shareIntent.setType("video/*");
                startActivity(Intent.createChooser(shareIntent, "Compartir"));
            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_reproductor, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.action_open:
                        abrirVideo();
                        return true;
                    case R.id.action_share:
                        compartir();
                        return true;
                    default:
                        return super.onOptionsItemSelected(item);
                }

            }


        }
