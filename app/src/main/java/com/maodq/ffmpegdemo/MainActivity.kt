package com.maodq.ffmpegdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private var unbinder: Unbinder? = null


    companion object {
        private const val TAG = "MainActivity"
        private const val SRC_0 = "test.wav"
        private const val SRC_1 = "test.mp4"
    }

    private var src_0: String = ""
    private var src_1: String = ""
    private var output: String = ""
    private var fileParent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        unbinder = ButterKnife.bind(this)
        requestPermission()
    }

    private fun requestPermission() {
        // button "process" pushed
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            init()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    private fun init() {
        val ffmpeg = FFmpeg.getInstance(applicationContext)
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onStart() {
                    Log.i("FFmpeg", "onStart")
                }

                override fun onFailure() {
                    Log.i("FFmpeg", "onFailure")
                }

                override fun onSuccess() {
                    Log.i("FFmpeg", "onSuccess")
                }

                override fun onFinish() {
                    Log.i("FFmpeg", "onFinish")
                }

            })
        } catch (e: FFmpegNotSupportedException) {
            // Handle if FFmpeg is not supported by device
            e.printStackTrace()
        }


        FFmpeg.getInstance(applicationContext).execute(arrayOf("version"), handler)
//        tv_log.text = runCommand

        fileParent = Environment.getExternalStorageDirectory().absolutePath + "/" + "ffmpeg"
        src_0 = "$fileParent/$SRC_0"
        src_1 = "$fileParent/$SRC_1"
        output = "$fileParent/output.aac"

        Observable.just(SRC_0, SRC_1)
                .map {
                    val output = Util.copyAssets(applicationContext, it)
                    Log.i(TAG, "output = $output")
                    Log.i(TAG, "exists = ${File(output).exists()}")
                    it
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    @OnClick(R.id.tv_src_0, R.id.tv_src_1, R.id.tv_output, R.id.btn_merge, R.id.btn_voice, R.id.btn_speed, R.id.btn_echo)
    fun onClick(view: View) {
        when (view.id) {
            R.id.tv_src_0 -> {
                playWavFile(src_0)
            }
            R.id.tv_src_1 -> {
                playWavFile(src_1)
            }
            R.id.tv_output -> {
                playWavFile(output)
            }

            R.id.btn_merge -> {
                reset()
                val order = "-filter_complex amix=inputs=2:duration=first:dropout_transition=2"
                val s = "-i $src_0 -i $src_1 $order $output"
                ffmpegRun(s)
            }

            R.id.btn_voice -> {
                showToast("暂未实现")
            }

            R.id.btn_speed -> {
                showToast("暂未实现")
            }

            R.id.btn_echo -> {
                reset()
                ffmpegRun("-i $src_0 -af aecho=0.8:0.9:1000:0.3 $output")
            }
        }
    }

    private fun reset() {
        File(output).delete()
    }

    private fun ffplay(path: String) {
        Flowable.just(path)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { checkFileExists(it) }
                .subscribeOn(Schedulers.io())
                .map { FFmpeg.getInstance(applicationContext).execute(arrayOf("ffplay", it), handler) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun checkFileExists(path: String): Boolean {
        val file = File(path)
        if (!file.exists()) {
            showToast("文件不存在")
            return false
        }
        return true
    }

    private fun ffmpegRun(order: String) {
        Flowable.just(order)
                .filter {
                    val fFmpegCommandRunning = FFmpeg.getInstance(applicationContext).isFFmpegCommandRunning
                    if (fFmpegCommandRunning) {
                        showToast("正在运行，稍后再试")
                    }
                    !fFmpegCommandRunning
                }
                .subscribeOn(Schedulers.io())
                .map {
                    val split = order.split(" ")
                    val toTypedArray = split.toTypedArray()
                    FFmpeg.getInstance(applicationContext).execute(toTypedArray, handler)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

    private fun showToast(s: String) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runOnUiThread { Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
        }
    }

    /// Play audio file
    protected fun playWavFile(fileName: String) {
        val file2play = File(fileName)
        val uri: Uri
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, "com.maodq.ffmpegdemo.fileprovider", file2play)
            intent.data = uri
            // 授予临时权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(file2play)
            intent.setDataAndType(uri, "audio/wav")
        }


        startActivity(intent)
    }

    var handler: FFmpegExecuteResponseHandler = object : FFmpegExecuteResponseHandler {
        override fun onFinish() {
            Log.i(TAG, "onFinish")
        }

        override fun onSuccess(message: String?) {
            Log.i(TAG, "onSuccess")
            runOnUiThread { tv_log.text = message }
            playWavFile(output)
        }

        override fun onFailure(message: String?) {
            Log.i(TAG, "onFailure")
            runOnUiThread { tv_log.text = message }
        }

        override fun onProgress(message: String?) {
            Log.i(TAG, "onProgress: $message")
            runOnUiThread { tv_log.append(message) }
        }

        override fun onStart() {
            Log.i(TAG, "onStart")
            runOnUiThread { tv_log.text = "" }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder?.unbind()
    }

}
