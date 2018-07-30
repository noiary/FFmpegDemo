package com.maodq.ffmpegdemo.java;

import android.os.Environment;

public class Constant {
    public static final String SRC_0 = "test.wav";
    public static final String SRC_1 = "test.mp4";

    public static final String fileParent = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ffmpeg";

    public static final String src_0 = fileParent + "/" + SRC_0;
    public static final String src_1 = fileParent + "/" + SRC_1;
    public static final String output = fileParent + "/" + "output.aac";
}
