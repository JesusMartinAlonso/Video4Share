package com.jesusmartin92.ffmpegandroid;

/**
 * Created by jesusmartin92 on 28/5/15.
 */

import android.content.Context;

        import android.view.View;
        import android.widget.MediaController;

public class CustomMediaController extends MediaController {
    public CustomMediaController(Context context, View anchor)     {
        super(context);
        super.setAnchorView(anchor);
    }
    @Override
    public void setAnchorView(View view)     {
        // Do nothing
    }
}