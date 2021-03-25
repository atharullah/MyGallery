package com.hisenberg.mygallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Video_Detail extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer player;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    View backButton = null;
    TextView videoTitle = null;
    ImageButton btnAspectRation = null;
    ImageButton btnRepeatMode = null;

    private GestureDetector mDetector;

    List<EntityVideoAlbumDetails> folderMediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__video__detail);

        playerView = findViewById(R.id.videoPlayerView);
        backButton = playerView.findViewById(R.id.videoPlayerBack);
        videoTitle = playerView.findViewById(R.id.videoPlayerTitle);
        btnAspectRation = playerView.findViewById(R.id.videoPlayerResizer);
        btnRepeatMode = playerView.findViewById(R.id.videoPlayerRepeat);

        Intent currentIntent = getIntent();
        videoTitle.setText(currentIntent.getStringExtra(Constants.IntExtraFileName));
        currentWindow = currentIntent.getIntExtra(Constants.IntExtraPosition, 0);
        String folderPath = currentIntent.getStringExtra(Constants.IntExtraFolderUrl);
        folderMediaList = EntityVideoAlbumDetails.getVideoAlbumDetail(this, folderPath);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    releasePlayer();
                finish();
            }
        });

        btnAspectRation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    btnAspectRation.setImageResource(R.drawable.exo_controls_fullscreen_exit);
                } else {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    btnAspectRation.setImageResource(R.drawable.exo_controls_fullscreen_enter);
                }
            }
        });
        btnRepeatMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayerRepeat(player);
            }
        });

        mDetector = new GestureDetector(this, new MyGestureListner());
        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();
                return mDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();// ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource();
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
        player.addListener(new PlayerEventListener());
        player.setPlayWhenReady(playWhenReady);
        player.setShuffleModeEnabled(false);
        player.setRepeatMode(SimpleExoPlayer.REPEAT_MODE_OFF);
    }

    private MediaSource buildMediaSource() {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "Gallery");
        ProgressiveMediaSource.Factory mediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);
        MediaSource[] mediaSources = new MediaSource[folderMediaList.size()];
        for (int i = 0; i < folderMediaList.size(); i++) {
            mediaSources[i] = mediaSourceFactory.createMediaSource(Uri.parse(folderMediaList.get(i).VideoPath));
        }
        //LoopingMediaSource repeatMediaSource=new LoopingMediaSource(new ConcatenatingMediaSource(mediaSources),mediaSources.length);
        return new ConcatenatingMediaSource(mediaSources);
    }

    @Override
    public void onStart() {
        super.onStart();
            initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ( player == null) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
            releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
            releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    private class PlayerEventListener implements SimpleExoPlayer.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if(playbackState == player.STATE_ENDED){
                releasePlayer();
                finish();
            }
            else if (playbackState == player.STATE_IDLE || !playWhenReady) {
                playerView.setKeepScreenOn(false);
            } else {
                playerView.setKeepScreenOn(true);
                //sometimes video format unknown
                if (player.getVideoFormat() != null) {
                    int width = player.getVideoFormat().width;
                    int height = player.getVideoFormat().height;
                    if (width > height) {
                        Activity_Video_Detail.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else
                        Activity_Video_Detail.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
            }
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            releasePlayer();
            finish();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }

        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
        }
    }

    private class MyGestureListner implements GestureDetector.OnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        AudioManager audioManager;

        MyGestureListner() {
            audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                player.seekTo(player.getContentPosition()-30);
                return true; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                player.seekTo(currentWindow,player.getContentPosition()+30);
                return true; // Left to right
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //To increase media player volume
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //To decrease media player volume
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true; // Top to bottom
            }
            return true;
        }
    }

    private void setPlayerRepeat(SimpleExoPlayer videoPlayer) {
        int repeatMode = videoPlayer.getRepeatMode();
        switch (repeatMode) {
            case SimpleExoPlayer.REPEAT_MODE_ALL:
                videoPlayer.setRepeatMode(SimpleExoPlayer.REPEAT_MODE_ONE);
                btnRepeatMode.setImageResource(R.drawable.exo_controls_repeat_one);
                break;
            case SimpleExoPlayer.REPEAT_MODE_ONE:
                videoPlayer.setRepeatMode(SimpleExoPlayer.REPEAT_MODE_OFF);
                btnRepeatMode.setImageResource(R.drawable.exo_controls_repeat_off);
                break;
            case SimpleExoPlayer.REPEAT_MODE_OFF:
                videoPlayer.setRepeatMode(SimpleExoPlayer.REPEAT_MODE_ALL);
                btnRepeatMode.setImageResource(R.drawable.exo_controls_repeat_all);
                break;
        }
    }
}


