package com.maodq.ffmpegdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import butterknife.Unbinder
import com.maodq.ffmpegdemo.FFmpegHelper.Companion.output
import com.maodq.ffmpegdemo.FFmpegHelper.Companion.src_0
import com.maodq.ffmpegdemo.FFmpegHelper.Companion.src_1
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private var unbinder: Unbinder? = null
    private var ffmpegHelper: FFmpegHelper? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        unbinder = ButterKnife.bind(this)
        requestPermission()
    }

    private fun requestPermission() {
        // button "process" pushed
        if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            init()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    private fun init() {
        ffmpegHelper = FFmpegHelper(this, tv_log)
    }

    @OnTextChanged(R.id.tv_log)
    fun onTextChanged(text: CharSequence) {
        if (nsv.canScrollVertically(1)) {
            nsv.post { nsv.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    @OnClick(R.id.tv_src_0, R.id.tv_src_1, R.id.tv_output, R.id.btn_merge,
            R.id.btn_voice, R.id.btn_speed, R.id.btn_echo)
    fun onClick(view: View) {
        when (view.id) {
            R.id.tv_src_0 -> {
                ffmpegHelper!!.play(src_0)
            }
            R.id.tv_src_1 -> {
                ffmpegHelper!!.play(src_1)
            }
            R.id.tv_output -> {
                ffmpegHelper!!.play(output)
            }

            R.id.btn_merge -> {
                reset()
                val order = "-filter_complex amix=inputs=2:duration=first:dropout_transition=2"
                val s = "-i $src_0 -i $src_1 $order $output"
                ffmpegRun(s)
            }

            R.id.btn_voice -> {
                // https://blog.csdn.net/nil_lu/article/details/52078488
                // 声音-30dB
//                ffmpegRun("-i $src_0 -af volume=-3dB $output")
                // 以上代码会出问题，开始就停不下来，并且不会结束
                showToast("暂未实现")
            }

            R.id.btn_speed -> {
//                showToast("暂未实现")
                // https://blog.csdn.net/matrix_laboratory/article/details/53158307
                ffmpegRun("-i $src_0 -filter:a \"atempo=0.5\" -vn $output")
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

//    private fun ffplay(path: String) {
//        Flowable.just(path)
//                .observeOn(AndroidSchedulers.mainThread())
//                .filter { checkFileExists(it) }
//                .subscribeOn(Schedulers.io())
//                .map { ffmpegHelper!!.execute(arrayOf("ffplay", it), handler) }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe()
//    }

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
                    val fFmpegCommandRunning =
                            ffmpegHelper!!.isRunning()
                    if (fFmpegCommandRunning) {
                        showToast("正在运行，稍后再试")
                    }
                    !fFmpegCommandRunning
                }
                .subscribeOn(Schedulers.io())
                .map {
                    ffmpegHelper!!.execute(order)
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


    override fun onDestroy() {
        ffmpegHelper!!.close()
        super.onDestroy()
        unbinder?.unbind()
    }

}
