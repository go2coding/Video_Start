package com.tb.mp3wave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MainActivity extends AppCompatActivity {


    private AudioTrack mAudioTrack;
    private int mAudioMinBufSize;
    private short[] audioBuffer;
    private boolean mThreadFlag;
    private int playCurrentPos = 0;
    private int samplerate;
    private String TAG = "MP3Wave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play();
    }


    private void play() {
        final NativeMP3Decoder mp3Decoder = new NativeMP3Decoder();

        File file = new File(Environment.getExternalStorageDirectory(), "q.mp3");
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");

            int i = mp3Decoder.initAudioPlayer(file.getAbsolutePath(), 0);

            initParams(mp3Decoder);
            if (i == -1) {
                Log.e(TAG , "打开文件失败");
                return;
            } else {

                mThreadFlag = true;
                audioBuffer = new short[1024 * 1024];

                Thread mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mThreadFlag) {
                            if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED && mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                                // ****从libmad处获取data******/
                                playCurrentPos = mp3Decoder.getAudioBuf(audioBuffer, mAudioMinBufSize);
                                mAudioTrack.write(audioBuffer, 0, mAudioMinBufSize);
                                Log.d(TAG, "====播放缓冲大小:  " + mAudioMinBufSize + "====播放的文件位置: ========" + playCurrentPos+"=========文件大小: "+mp3Decoder.getAudioFileSize());
                                if (playCurrentPos == 0) {
                                    mAudioTrack.stop();
                                    mAudioTrack.release();
                                }
                            } else {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                mThread.start();
                mAudioTrack.play();
            }

        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initParams(NativeMP3Decoder mp3Decoder) {
        samplerate = mp3Decoder.getAudioSamplerate();
        samplerate = samplerate / 2;
        // 声音文件一秒钟buffer的大小
        mAudioMinBufSize = AudioTrack.getMinBufferSize(samplerate,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, // 指定在流的类型
                samplerate,// 设置音频数据的采样率
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,// 设置输出声道为双声道立体声
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                mAudioMinBufSize, AudioTrack.MODE_STREAM);// 设置模式类型，在这里设置为流类型
    }

    public static void toSelfSetting(Context context) {

        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        context.startActivity(mIntent);
    }
}
