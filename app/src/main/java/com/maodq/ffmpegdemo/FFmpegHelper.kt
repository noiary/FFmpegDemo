//package com.maodq.ffmpegdemo
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.os.Environment
//import android.os.Handler
//import android.os.Looper
//import android.support.v4.content.FileProvider
//import android.util.Log
//import android.widget.TextView
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg
//import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
//import io.reactivex.Observable
//import io.reactivex.schedulers.Schedulers
//import java.io.File
//import java.lang.ref.WeakReference
//
//class FFmpegHelper(context: Activity, tvLog: TextView) : IFFmpeg {
//    companion object {
//        private const val TAG: String = "IFFmpeg"
//        private const val SRC_0 = "test.wav"
//        private const val SRC_1 = "test.mp4"
//
//        var src_0: String = ""
//        var src_1: String = ""
//        var output: String = ""
//    }
//
//
//    private var fileParent: String = ""
//    private val context: Context = context.applicationContext
//    private val activityRef: WeakReference<Activity> = WeakReference(context)
//    private var tvLogRef: WeakReference<TextView>? = WeakReference(tvLog)
//    private var handler: Handler = Handler(Looper.getMainLooper())
//
//    init {
//        val ffmpeg = getFFmpeg()
//        try {
//            ffmpeg.setTimeout(10 * 1000)
//            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
//                override fun onStart() {
//                    Log.i("FFmpeg", "onStart")
//                }
//
//                override fun onFailure() {
//                    Log.i("FFmpeg", "onFailure")
//                }
//
//                override fun onSuccess() {
//                    Log.i("FFmpeg", "onSuccess")
//                }
//
//                override fun onFinish() {
//                    Log.i("FFmpeg", "onFinish")
//                }
//
//            })
//        } catch (e: FFmpegNotSupportedException) {
//            // Handle if FFmpeg is not supported by device
//            e.printStackTrace()
//        }
//
//        execute("version")
////        tvLogRef.get()?.text = runCommand
//
//        fileParent = Environment.getExternalStorageDirectory().absolutePath + "/" + "ffmpeg"
//        src_0 = "$fileParent/$SRC_0"
//        src_1 = "$fileParent/$SRC_1"
//        output = "$fileParent/output.aac"
//
//        Observable.just(SRC_0, SRC_1)
//                .map {
//                    val output = Util.copyAssets(context, it)
//                    Log.i(TAG, "output = $output")
//                    Log.i(TAG, "exists = ${File(output).exists()}")
//                    it
//                }
//                .subscribeOn(Schedulers.io())
//                .subscribe()
//    }
//
//    override fun close() {
//        getFFmpeg().killRunningProcesses()
//    }
//
//    override fun isRunning(): Boolean {
//        return getFFmpeg().isFFmpegCommandRunning
//    }
//
//    override fun execute(args: String) {
//        val split = args.split(" ")
//        val array = split.toTypedArray()
//        getFFmpeg().execute(array, ffmpegExecuteResponseHandler)
//    }
//
//    override fun play(path: String) {
//        val activity = activityRef.get() ?: return
//        val file2play = File(path)
//        val uri: Uri
//        val intent = Intent(Intent.ACTION_VIEW)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(activity,
//                    "com.maodq.ffmpegdemo.fileprovider", file2play)
//            intent.data = uri
//            // 授予临时权限
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        } else {
//            uri = Uri.fromFile(file2play)
//            intent.setDataAndType(uri, "audio/wav")
//        }
//        activityRef.get()?.startActivity(intent)
//    }
//
//    private fun getFFmpeg(): FFmpeg {
//        return FFmpeg.getInstance(context)
//    }
//
//    var ffmpegExecuteResponseHandler: FFmpegExecuteResponseHandler = object : FFmpegExecuteResponseHandler {
//        override fun onFinish() {
//            Log.i(TAG, "-------------------------- onFinish --------------------------")
//        }
//
//        override fun onSuccess(message: String?) {
//            Log.i(TAG, "-------------------------- onSuccess --------------------------")
//            handler.post { tvLogRef?.get()?.text = message }
//            play(output)
//        }
//
//        override fun onFailure(message: String?) {
//            Log.i(TAG, " -------------------------- onFailure -------------------------- ")
//            handler.post { tvLogRef?.get()?.text = message }
//        }
//
//        override fun onProgress(message: String?) {
//            Log.i(TAG, " -- onProgress -- $message")
//            handler.post { tvLogRef?.get()?.append(message) }
//        }
//
//        override fun onStart() {
//            Log.i(TAG, " -------------------------- onStart -------------------------- ")
//            handler.post { tvLogRef?.get()?.text = "" }
//        }
//    }
//
//}