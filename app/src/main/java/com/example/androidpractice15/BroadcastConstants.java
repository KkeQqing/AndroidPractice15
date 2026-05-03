package com.example.androidpractice15;

// 广播常量类，统一管理所有自定义广播Action
public class BroadcastConstants {
    // 视频控制广播
    public static final String ACTION_PLAY = "com.example.androidpractice15.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.androidpractice15.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.example.androidpractice15.ACTION_STOP";
    public static final String ACTION_FULL_SCREEN = "com.example.androidpractice15.ACTION_FULL_SCREEN";

    // 视频进度广播
    public static final String ACTION_PROGRESS = "com.example.androidpractice15.ACTION_PROGRESS";
    public static final String EXTRA_PROGRESS = "current_progress";
    public static final String EXTRA_TOTAL = "total_duration";
}