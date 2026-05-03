package com.example.androidpractice15;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // 1. 定义广播 Action 常量，用于区分指令
    public static final String ACTION_PLAY = "com.example.androidpractice15.PLAY";
    public static final String ACTION_PAUSE = "com.example.androidpractice15.PAUSE";
    public static final String ACTION_STOP = "com.example.androidpractice15.STOP";

    // 控件声明
    private VideoView videoView;
    private Button btnPlay, btnPause, btnStop;

    // 广播接收器实例
    private VideoControlReceiver videoControlReceiver;
    // 意图过滤器
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        initViews();

        // 初始化视频路径 (从 assets 读取)
        setupVideo();

        // 初始化广播接收器和过滤器
        initBroadcast();

        // 设置按钮点击监听器（发送广播）
        setupClickListeners();
    }

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
    }

    private void setupVideo() {
        // 获取 assets 中的视频文件路径
        // 注意：VideoView 播放 assets 文件需要特殊处理，或者放在 raw 文件夹
        // 这里演示使用 assets 的方式，通过文件路径构建 Uri
        try {
            // 简单方式：如果放在 raw 文件夹 (R.raw.test_video)，直接用 videoView.setVideoResource(R.raw.test_video);
            // 既然要求文件名为 test_video.mp4，我们假设你放在了 assets 目录
            String path = "android.resource://" + getPackageName() + "/" + R.raw.test_video;
            // 如果你的视频在 assets 中，VideoView 直接播放比较麻烦，建议将视频放入 res/raw 目录
            // 这里为了代码稳健性，假设你已将 test_video.mp4 放入了 res/raw 目录
            // 如果没有放入 raw，请去 res 目录创建 raw 文件夹并放入视频
            videoView.setVideoPath(path);
        } catch (Exception e) {
            Toast.makeText(this, "请确保视频文件 test_video.mp4 已放入 res/raw 目录", Toast.LENGTH_LONG).show();
        }

        // 监听视频播放完成，完成后重置位置以便重播
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
            }
        });
    }

    private void initBroadcast() {
        // 实例化接收器
        videoControlReceiver = new VideoControlReceiver();
        // 创建过滤器，注册我们要监听的三个动作
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_STOP);
    }

    private void setupClickListeners() {
        // 点击播放按钮 -> 发送播放广播
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_PLAY);
                // 【修改点】添加这一行，指定只发送给自己应用的包名
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
            }
        });

        // 点击暂停按钮 -> 发送暂停广播
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_PAUSE);
                // 【修改点】添加这一行
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
            }
        });

        // 点击停止按钮 -> 发送停止广播
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_STOP);
                // 【修改点】添加这一行
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
            }
        });
    }

    // 2. 动态注册广播接收器
    @Override
    protected void onResume() {
        super.onResume();
        // 在 Activity 可见时注册
        // 修改点：添加了第三个参数 Context.RECEIVER_NOT_EXPORTED
        registerReceiver(videoControlReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
    }

    // 3. 销毁时解绑广播，避免内存泄漏
    @Override
    protected void onPause() {
        super.onPause();
        if (videoControlReceiver != null) {
            unregisterReceiver(videoControlReceiver);
        }
    }

    // 自定义广播接收器类
    private class VideoControlReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            switch (action) {
                case ACTION_PLAY:
                    if (!videoView.isPlaying()) {
                        videoView.start();
                        Toast.makeText(context, "▶ 播放指令已接收", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ACTION_PAUSE:
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        Toast.makeText(context, "⏸ 暂停指令已接收", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ACTION_STOP:
                    // 停止并重置到开头
                    videoView.stopPlayback();
                    // 重新加载视频源以便下次播放（VideoView stopPlayback 后需要重新设置路径或调用 resume）
                    // 简单处理：seekTo(0) 通常用于暂停后的重置，stopPlayback 会释放资源
                    // 这里为了演示“停止”效果，我们重新设置一下路径或简单地 seekTo
                    // 注意：VideoView.stopPlayback() 后通常需要重新 prepare
                    try {
                        String path = "android.resource://" + getPackageName() + "/" + R.raw.test_video;
                        videoView.setVideoPath(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "⏹ 停止并重置指令已接收", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}