package com.example.androidpractice15;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;

public class VideoProgressReceiver extends BroadcastReceiver {
    private final ProgressBar progressBar;

    public VideoProgressReceiver(ProgressBar bar) {
        this.progressBar = bar;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BroadcastConstants.ACTION_PROGRESS.equals(intent.getAction())) {
            // 获取进度
            int current = intent.getIntExtra(BroadcastConstants.EXTRA_PROGRESS, 0);
            int total = intent.getIntExtra(BroadcastConstants.EXTRA_TOTAL, 100);

            // 更新进度条
            progressBar.setMax(total);
            progressBar.setProgress(current);
        }
    }
}