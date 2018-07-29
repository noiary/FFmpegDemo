package com.maodq.ffmpegdemo

import android.os.Environment

class Constant {
    companion object {

        const val SRC_0 = "test.wav"
        const val SRC_1 = "test.mp4"

        var fileParent: String = Environment.getExternalStorageDirectory().absolutePath + "/" + "ffmpeg"

        var src_0: String = "$fileParent/$SRC_0"
        var src_1: String = "$fileParent/$SRC_1"
        var output: String = "$fileParent/output.aac"
    }
}