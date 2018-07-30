package com.maodq.ffmpegdemo.java;

public interface IFFmpeg {
    /**
     * 异步执行ffmpeg命令
     *
     * @param args 命令参数
     */
    void execute(String args);

    boolean isRunning();

    void close();

    void play(String path);
}
