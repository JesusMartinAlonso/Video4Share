package com.jesusmartin92.ffmpegandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class LoadVideoActivity extends ActionBarActivity {
    private static final int SELECT_VIDEO= 100;
    private Uri selectedVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_load_video);
        Button openVideoButton = (Button) findViewById(R.id.button);

        openVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CODE FROM http://stackoverflow.com/questions/2507898/how-to-pick-an-image-from-gallery-sd-card-for-my-app?rq=1
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("video/*");
                startActivityForResult(photoPickerIntent, SELECT_VIDEO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent videoReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, videoReturnedIntent);

        switch(requestCode) {
            case SELECT_VIDEO:
                if(resultCode == RESULT_OK){
                    selectedVideo = videoReturnedIntent.getData();

                    Intent intent = new Intent(this,Reproductor.class);
                    intent.putExtra("uri", selectedVideo);
                    startActivity(intent);
                    //InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    //Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                }
        }
    }
}
