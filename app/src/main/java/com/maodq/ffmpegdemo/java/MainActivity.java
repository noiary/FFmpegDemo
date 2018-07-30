package com.maodq.ffmpegdemo.java;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.maodq.ffmpegdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.maodq.ffmpegdemo.java.Constant.*;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_src_0) TextView tvSrc0;
    @BindView(R.id.tv_src_1) TextView tvSrc1;
    @BindView(R.id.tv_output) TextView tvOutput;
    @BindView(R.id.btn_merge) Button btnMerge;
    @BindView(R.id.btn_voice) Button btnVoice;
    @BindView(R.id.btn_speed) Button btnSpeed;
    @BindView(R.id.btn_echo) Button btnEcho;
    @BindView(R.id.tv_log) TextView tvLog;
    @BindView(R.id.nsv) NestedScrollView nsv;
    private IFFmpeg ffmpegHelper;
    private static final String TAG = "MainActivity";
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ffmpegHelper = new FFmpegHelper(this);
        mainPresenter = new MainPresenter();
        mainPresenter.requestPermission(this);
    }

    private void ffmpegRun(String args) {
        ffmpegHelper.execute(args);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (ffmpegHelper != null) {
            ffmpegHelper.close();
        }
    }

    @OnClick({R.id.tv_src_0, R.id.tv_src_1, R.id.tv_output, R.id.btn_merge,
            R.id.btn_voice, R.id.btn_speed, R.id.btn_echo})
    public void onViewClicked(View view) {
        String part;
        String order;
        switch (view.getId()) {
            case R.id.tv_src_0:
                ffmpegHelper.play(src_0);
                break;
            case R.id.tv_src_1:
                ffmpegHelper.play(Constant.src_1);
                break;
            case R.id.tv_output:
                ffmpegHelper.play(Constant.output);
                break;


            // 合并
            case R.id.btn_merge:
                part = "-filter_complex amix=inputs=2:duration=first:dropout_transition=2";
                String args = String.format("-i %s -i %s %s %s", src_0, src_1, part, output);
                ffmpegRun(args);
                break;
            // 音量
            case R.id.btn_voice:
                // https://blog.csdn.net/nil_lu/article/details/52078488
                part = "-af volume=-30dB";
                order = String.format("-i %s %s %s", src_0, part, output);
                ffmpegRun(order);
                break;
            // 速度
            case R.id.btn_speed:
                // https://blog.csdn.net/matrix_laboratory/article/details/53158307
                part = "-filter:a atempo=0.5 -vn";
                order = String.format("-i %s %s %s", src_0, part, output);
                ffmpegRun(order);
                break;
            // 回声
            case R.id.btn_echo:
                part = "-af aecho=0.8:0.9:1000:0.3";
                order = String.format("-i %s %s %s", src_0, part, output);
                ffmpegRun(order);
                break;
        }
    }
}
