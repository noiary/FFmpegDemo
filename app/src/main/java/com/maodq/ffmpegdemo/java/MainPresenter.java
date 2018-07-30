package com.maodq.ffmpegdemo.java;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.maodq.ffmpegdemo.Util;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.maodq.ffmpegdemo.java.Constant.SRC_0;
import static com.maodq.ffmpegdemo.java.Constant.SRC_1;

public class MainPresenter {

    private static final String TAG = "MainPresenter";

    public void requestPermission(Activity context) {
        // button "process" pushed
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            init(context);
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void init(Activity context) {
        // 将assets文件拷贝到磁盘
        Flowable.just(SRC_0, SRC_1)
                .map((Function<String, Object>) s -> {
                    String output = Util.copyAssets(context.getApplicationContext(), s);
                    Log.i(TAG, "output = " + output);
                    if (output != null)
                        Log.i(TAG, "exists = " + new File(output).exists());
                    return s;
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
