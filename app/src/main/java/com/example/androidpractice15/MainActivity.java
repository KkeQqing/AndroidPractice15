package com.example.androidpractice15;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private ProgressBar progressBar;
    private VideoControlReceiver videoControlReceiver;
    private VideoProgressReceiver progressReceiver;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        View btnPlay = findViewById(R.id.btn_play);
        View btnPause = findViewById(R.id.btn_pause);
        View btnStop = findViewById(R.id.btn_stop);
        View btnFull = findViewById(R.id.btn_fullscreen);

        // 初始化视频
        initVideo();

        // ================== 注册本地广播接收器 ==================
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 1. 视频控制广播接收器
        videoControlReceiver = new VideoControlReceiver(videoView);
        IntentFilter controlFilter = new IntentFilter();
        controlFilter.addAction(BroadcastConstants.ACTION_PLAY);
        controlFilter.addAction(BroadcastConstants.ACTION_PAUSE);
        controlFilter.addAction(BroadcastConstants.ACTION_STOP);
        localBroadcastManager.registerReceiver(videoControlReceiver, controlFilter);

        // 2. 视频进度广播接收器
        progressReceiver = new VideoProgressReceiver(progressBar);
        IntentFilter progressFilter = new IntentFilter(BroadcastConstants.ACTION_PROGRESS);
        localBroadcastManager.registerReceiver(progressReceiver, progressFilter);

        // ================== 按钮点击：发送本地广播 ==================
        btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(BroadcastConstants.ACTION_PLAY);
            localBroadcastManager.sendBroadcast(intent);
        });

        btnPause.setOnClickListener(v -> {
            Intent intent = new Intent(BroadcastConstants.ACTION_PAUSE);
            localBroadcastManager.sendBroadcast(intent);
        });

        btnStop.setOnClickListener(v -> {
            Intent intent = new Intent(BroadcastConstants.ACTION_STOP);
            localBroadcastManager.sendBroadcast(intent);
        });

        btnFull.setOnClickListener(v -> {
            toggleFullScreen();
        });

        // 启动进度更新线程
        new Thread(this::updateProgressLoop).start();
    }

    // 初始化视频
    private void initVideo() {
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.test_video);
        videoView.setOnPreparedListener(mp -> mp.setLooping(true));
    }

    // 进度更新循环，每500ms发送一次进度广播
    private void updateProgressLoop() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        while (isRunning && !isFinishing()) {
            if (videoView != null && videoView.isPlaying()) {
                int current = videoView.getCurrentPosition();
                int total = videoView.getDuration();

                Intent intent = new Intent(BroadcastConstants.ACTION_PROGRESS);
                intent.putExtra(BroadcastConstants.EXTRA_PROGRESS, current);
                intent.putExtra(BroadcastConstants.EXTRA_TOTAL, total);
                localBroadcastManager.sendBroadcast(intent);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    // 切换全屏
    private void toggleFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        videoView.getLayoutParams().height = getResources().getDisplayMetrics().heightPixels;
        videoView.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        // 注销本地广播接收器
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        if (videoControlReceiver != null) {
            localBroadcastManager.unregisterReceiver(videoControlReceiver);
        }
        if (progressReceiver != null) {
            localBroadcastManager.unregisterReceiver(progressReceiver);
        }
    }
}