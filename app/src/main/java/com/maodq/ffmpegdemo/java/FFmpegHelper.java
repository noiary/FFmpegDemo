package com.maodq.ffmpegdemo.java;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.maodq.ffmpegdemo.java.Constant.output;

public class FFmpegHelper implements IFFmpeg {

    private static final String TAG = "FFmpegHelper";
    private WeakReference<Activity> activityRef;

    public FFmpegHelper(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    @Override public void execute(String args) {
        _execute(args);
    }

    private void _execute(String args) {
        // 手动删除上次生成的output文件
        boolean delete = new File(output).delete();
        String[] split = args.split(" ");
        int rc = FFmpeg.execute(split);
        Log.i(TAG, String.format("Command execution %s.", (rc == 0 ? "completed successfully" :
                "failed with rc=$rc")));
        if (rc == 0) {
            play(output);
        }
    }

    @Override public boolean isRunning() {
        return false;
    }

    @Override public void close() {
        FFmpeg.shutdown();
    }

    @Override public void play(String path) {
        Activity activity = activityRef.get();
        if (activity == null) {
            return;
        }
        File file2play = new File(path);
        Uri uri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity,
                    "com.maodq.ffmpegdemo.fileprovider", file2play);
            intent.setData(uri);
            // 授予临时权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file2play);
            intent.setDataAndType(uri, "audio/wav");
        }
        activity.startActivity(intent);
    }
}
