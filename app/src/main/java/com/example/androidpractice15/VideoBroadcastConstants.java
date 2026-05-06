package com.example.androidpractice15;

public class VideoBroadcastConstants {

    // 统一广播 Action
    public static final String ACTION_VIDEO_CONTROL =
            "com.example.androidpractice15.VIDEO_CONTROL";

    // 命令字段
    public static final String EXTRA_CONTROL_CMD =
            "control_command";

    // 播放进度字段
    public static final String EXTRA_PROGRESS =
            "progress";

    public static final String EXTRA_DURATION =
            "duration";

    // 控制命令
    public static final int CMD_PLAY = 1;

    public static final int CMD_PAUSE = 2;

    public static final int CMD_STOP = 3;

    public static final int CMD_FORWARD = 4;

    public static final int CMD_BACKWARD = 5;
}