package com.example.videoplayer.Services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.videoplayer.Activities.MainActivity;
import com.example.videoplayer.Activities.VideoPlayerActivity;
import com.example.videoplayer.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class BackgroundPlayerService extends Service {

    //====================2-=21------------------=
    //===============================
    // CHEK OUT WHAT DEFINES THE POSITION OF THE WINDOWS, IF ITS THE GRAVITY OR PARAMS.X AND Y

    Uri videoUri;
    WindowManager windowManager;
    View floatingWidget;
    SimpleExoPlayer exoPlayer;
    WindowManager.LayoutParams params;
    PlayerView playerView;
    ImageView closeBackground;
    RelativeLayout taskBar;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null){

            String videoPath = intent.getStringExtra("videoUri");
            //Toast.makeText(getApplicationContext(), videoPath, Toast.LENGTH_SHORT).show();
            videoUri = Uri.parse(videoPath);


            if(windowManager!=null & floatingWidget != null & exoPlayer!=null){

                windowManager.removeView(floatingWidget);
                floatingWidget=null;
                windowManager=null;
                exoPlayer.setPlayWhenReady(false);
                exoPlayer=null;
                exoPlayer.release();

            }


            playerView = floatingWidget.findViewById(R.id.backgroundPlayerView);
            closeBackground = floatingWidget.findViewById(R.id.closeBackground);


            if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

            }
            else{

                params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

            }

            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 200;
            params.y = 200;

            windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            windowManager.addView(floatingWidget, params);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            closeBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.release();
                    windowManager.removeView(floatingWidget);
                    stopSelf();
                }
            });

            playVideos();
            moveFloatingWidget(params);


        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void moveFloatingWidget(final WindowManager.LayoutParams params) {

        taskBar = floatingWidget.findViewById(R.id.taskBar);

        taskBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = 0, y = 0;
                float touchX = 0, touchY = 0;

                WindowManager.LayoutParams updatedParams = null;

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        x = params.x;
                        y = params.y;
                        touchX = motionEvent.getRawX();
                        touchY = motionEvent.getRawX();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParams.x = (int) (x + (motionEvent.getRawX() - touchX));
                        updatedParams.y = (int) (y + (motionEvent.getRawY() - touchY));

                        windowManager.updateViewLayout(floatingWidget, updatedParams);
                        break;

                    default:
                        break;

                }

                return false;
            }
        });

    }

    private void playVideos() {

        String userAgent = Util.getUserAgent(this, "MyAplication");

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
        ExtractorsFactory extractorsFactory =new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null,
                null);

        playerView.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

    }
}
