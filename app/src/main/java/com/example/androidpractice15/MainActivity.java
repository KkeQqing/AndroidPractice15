package com.example.androidpractice15;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;

    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;
    private Button btnForward;
    private Button btnBackward;

    private SeekBar seekBar;

    private TextView tvCurrent;
    private TextView tvTotal;

    private VideoControlReceiver receiver;

    private IntentFilter intentFilter;

    private Handler handler = new Handler();

    private Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        initViews();

        setupVideo();

        initBroadcast();

        setupButtons();

        setupSeekBar();
    }

    private void initViews() {

        videoView = findViewById(R.id.videoView);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnForward = findViewById(R.id.btnForward);
        btnBackward = findViewById(R.id.btnBackward);

        seekBar = findViewById(R.id.seekBar);

        tvCurrent = findViewById(R.id.tvCurrent);
        tvTotal = findViewById(R.id.tvTotal);
    }

    private void setupVideo() {

        String path =
                "android.resource://"
                        + getPackageName()
                        + "/"
                        + R.raw.test_video;

        videoView.setVideoPath(path);

        // 视频准备完成
        videoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        // 循环播放
                        mp.setLooping(true);

                        tvTotal.setText(
                                formatTime(videoView.getDuration())
                        );
                    }
                });

        // 进度更新任务
        progressRunnable = new Runnable() {

            @Override
            public void run() {

                if (videoView.isPlaying()) {

                    int progress =
                            videoView.getCurrentPosition();

                    int duration =
                            videoView.getDuration();

                    seekBar.setMax(duration);

                    seekBar.setProgress(progress);

                    tvCurrent.setText(
                            formatTime(progress)
                    );
                }

                handler.postDelayed(this, 1000);
            }
        };
    }

    private void initBroadcast() {

        receiver = new VideoControlReceiver();

        intentFilter = new IntentFilter();

        intentFilter.addAction(
                VideoBroadcastConstants.ACTION_VIDEO_CONTROL
        );
    }

    private void setupButtons() {

        // 播放
        btnPlay.setOnClickListener(v -> {

            sendControlBroadcast(
                    VideoBroadcastConstants.CMD_PLAY
            );
        });

        // 暂停
        btnPause.setOnClickListener(v -> {

            sendControlBroadcast(
                    VideoBroadcastConstants.CMD_PAUSE
            );
        });

        // 停止
        btnStop.setOnClickListener(v -> {

            sendControlBroadcast(
                    VideoBroadcastConstants.CMD_STOP
            );
        });

        // 快进
        btnForward.setOnClickListener(v -> {

            sendControlBroadcast(
                    VideoBroadcastConstants.CMD_FORWARD
            );
        });

        // 快退
        btnBackward.setOnClickListener(v -> {

            sendControlBroadcast(
                    VideoBroadcastConstants.CMD_BACKWARD
            );
        });
    }

    // 发送广播
    private void sendControlBroadcast(int cmd) {

        Intent intent = new Intent(
                VideoBroadcastConstants.ACTION_VIDEO_CONTROL
        );

        intent.putExtra(
                VideoBroadcastConstants.EXTRA_CONTROL_CMD,
                cmd
        );

        intent.setPackage(getPackageName());

        sendBroadcast(intent);
    }

    // SeekBar 拖动
    private void setupSeekBar() {

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser) {

                            videoView.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {

                    }
                });
    }

    // 动态注册广播
    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            registerReceiver(
                    receiver,
                    intentFilter,
                    Context.RECEIVER_NOT_EXPORTED
            );

        } else {

            registerReceiver(receiver, intentFilter);
        }
    }

    // 注销广播
    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    // 时间格式化
    private String formatTime(int ms) {

        int totalSeconds = ms / 1000;

        int minute = totalSeconds / 60;

        int second = totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minute,
                second
        );
    }

    // 广播接收器
    private class VideoControlReceiver
            extends BroadcastReceiver {

        @Override
        public void onReceive(
                Context context,
                Intent intent) {

            int cmd = intent.getIntExtra(
                    VideoBroadcastConstants.EXTRA_CONTROL_CMD,
                    0
            );

            switch (cmd) {

                // 播放
                case VideoBroadcastConstants.CMD_PLAY:

                    if (!videoView.isPlaying()) {

                        videoView.start();

                        handler.post(progressRunnable);

                        Toast.makeText(
                                context,
                                "▶ 开始播放",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    break;

                // 暂停
                case VideoBroadcastConstants.CMD_PAUSE:

                    if (videoView.isPlaying()) {

                        videoView.pause();

                        Toast.makeText(
                                context,
                                "⏸ 已暂停",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    break;

                // 停止
                case VideoBroadcastConstants.CMD_STOP:

                    videoView.pause();

                    videoView.seekTo(0);

                    seekBar.setProgress(0);

                    tvCurrent.setText("00:00");

                    handler.removeCallbacks(
                            progressRunnable
                    );

                    Toast.makeText(
                            context,
                            "⏹ 已停止",
                            Toast.LENGTH_SHORT
                    ).show();

                    break;

                // 快进
                case VideoBroadcastConstants.CMD_FORWARD:

                    int forwardPos =
                            videoView.getCurrentPosition()
                                    + 10000;

                    if (forwardPos <
                            videoView.getDuration()) {

                        videoView.seekTo(forwardPos);
                    }

                    Toast.makeText(
                            context,
                            "⏩ 快进10秒",
                            Toast.LENGTH_SHORT
                    ).show();

                    break;

                // 快退
                case VideoBroadcastConstants.CMD_BACKWARD:

                    int backPos =
                            videoView.getCurrentPosition()
                                    - 10000;

                    if (backPos > 0) {

                        videoView.seekTo(backPos);

                    } else {

                        videoView.seekTo(0);
                    }

                    Toast.makeText(
                            context,
                            "⏪ 快退10秒",
                            Toast.LENGTH_SHORT
                    ).show();

                    break;
            }
        }
    }
}