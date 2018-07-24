package com.maodq.ffmpegdemo

interface IFFmpeg {
    fun execute(args: String)
    fun isRunning(): Boolean
    fun close()
    fun play(path: String)
}