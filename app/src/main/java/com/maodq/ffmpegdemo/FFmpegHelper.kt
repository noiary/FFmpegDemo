//package com.maodq.ffmpegdemo
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.support.v4.content.FileProvider
//import android.util.Log
//import android.widget.TextView
//import com.arthenica.mobileffmpeg.FFmpeg
//import com.maodq.ffmpegdemo.Constant.Companion.SRC_0
//import com.maodq.ffmpegdemo.Constant.Companion.SRC_1
//import com.maodq.ffmpegdemo.Constant.Companion.output
//import io.reactivex.Observable
//import io.reactivex.schedulers.Schedulers
//import java.io.File
//import java.lang.ref.WeakReference
//
//class FFmpegHelper(context: Activity, tvLog: TextView) : IFFmpeg {
//
//    companion object {
//        private const val TAG: String = "IFFmpeg"
//    }
//
//    private val activityRef: WeakReference<Activity> = WeakReference(context)
//
//    init {
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
//        FFmpeg.shutdown()
//    }
//
//    override fun isRunning(): Boolean {
//        return false
//    }
//
//    override fun execute(args: String) {
//        // 手动删除上次生成的output文件
//        File(output).delete()
////        val cmd = "ffmpeg $args"
//        val split = args.split(" ")
//        val toTypedArray = split.toTypedArray()
//        val rc = FFmpeg.execute(*toTypedArray)
//        Log.i(TAG, String.format("Command execution %s.", (if (rc == 0) "completed successfully" else "failed with rc=$rc")))
//        if (rc == 0) {
//            play(output)
//        }
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
//}