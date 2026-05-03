package com.example.androidpractice15;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.VideoView;

public class VideoControlReceiver extends BroadcastReceiver {
    private final VideoView videoView;

    public VideoControlReceiver(VideoView videoView) {
        this.videoView = videoView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || videoView == null) return;

        switch (intent.getAction()) {
            case BroadcastConstants.ACTION_PLAY:
                if (!videoView.isPlaying()) {
                    videoView.start();
                }
                break;

            case BroadcastConstants.ACTION_PAUSE:
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                break;

            case BroadcastConstants.ACTION_STOP:
                videoView.stopPlayback();
                // 重置视频源，方便下次播放
                videoView.setVideoPath("android.resource://" + context.getPackageName() + "/" + R.raw.test_video);
                break;
        }
    }
}