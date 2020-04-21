package com.tb.recode2mp3;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.tb.libmp3.LameEncode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lqp on 2020/4/13.
 */
public class RecordUtil {

    private static RecordUtil mInstance;
    private AudioRecord recorder;
    //声音源
    private static int audioSource = MediaRecorder.AudioSource.MIC;
    //录音的采样频率
    private static int audioRate = 44100;
    //录音的声道，单声道
    private static int audioChannel = AudioFormat.CHANNEL_IN_STEREO;
    //量化的精度
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //缓存的大小
    private static int bufferSize = AudioRecord.getMinBufferSize(audioRate , audioChannel , audioFormat);
    //记录播放状态
    private boolean isRecording = false;

    private String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/";

    private String filePath;


    //文件流
    private FileOutputStream fileOutputStream;


    //mp3_buff
    private byte[] mp3_buff;


    private RecordUtil()
    {
        filePath = getFilePath();
        initFileStream(filePath);

        //创建文件
        createFile();
        recorder = new AudioRecord(audioSource , audioRate ,
                audioChannel , audioFormat , bufferSize);

        mp3_buff = new byte[(int) ((int) (7200 + (bufferSize * 2 * 1.25 * 2)))];

        LameEncode.init(44100, 2, 16, 7);
    }

    /**
     * 初始化输出流
     *
     * @param filePath ：文件路径
     */
    private void initFileStream(String filePath) {
        try {
            fileOutputStream = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileOutputStream = null;
        }
    }


    //创建文件夹,首先创建目录，然后创建对应的文件
    private void createFile()
    {
        File baseFile = new File(basePath);
        if (!baseFile.exists())
            baseFile.mkdirs();

    }

    public synchronized static RecordUtil getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new RecordUtil();
        }
        return mInstance;
    }

    //读取录音数字数据线程
    class WriteThread implements Runnable
    {
        @Override
        public void run()
        {
            writeData();
        }
    }

    //录音线程执行体
    private void writeData()
    {


        while (isRecording)
        {
            short[] pcmBuffer = new short[bufferSize];
            int readRecord = recorder.read(pcmBuffer, 0, bufferSize);
            if (readRecord > 0) {
                convertMp3(pcmBuffer,readRecord);
            }
        }


    }

    //开始录音
    public void startRecord()
    {
        isRecording = true;
        recorder.startRecording();
    }

    //记录数据
    public void recordData()
    {
        new Thread(new WriteThread()).start();
    }

    //停止录音
    public void stopRecord()
    {
        if (recorder != null)
        {
            isRecording = false;
            recorder.stop();
            recorder.release();

            writeFlush();
        }
    }





    private String getFilePath() {
        String currentDate = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.CHINA).format(new Date());
        String fileName = "/record_".concat(currentDate).concat(".mp3");
        return basePath + fileName;
    }

    /**
     * 回写lame缓冲区剩余字节数据
     */
    private void writeFlush() {
        int flushResult = LameEncode.flush(mp3_buff);
        if (flushResult > 0) {
            try {
                fileOutputStream.write(mp3_buff, 0, flushResult);
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * pcm转换mp3
     *
     * @param audioSource   :pcm音频数据源
     * @param audioReadSize ：采样数
     */
    private void convertMp3(short[] audioSource, int audioReadSize) {

        if (audioReadSize <= 0) {
            return;
        }
        if (fileOutputStream == null) {
            return;
        }
        int mp3_byte = LameEncode.encoder(audioSource, mp3_buff, audioReadSize);
        if (mp3_byte < 0) {
            Log.d("TAG", "onCaptureListener: MP3编码失败 :" + mp3_byte);
            return;
        }
        try {
            Log.d("TAG", "onCaptureListener: 编码长度" + mp3_byte);
            fileOutputStream.write(mp3_buff, 0, mp3_byte);
        } catch (IOException e) {
            Log.d("TAG", "onCaptureListener: MP3文件写入失败");
            e.printStackTrace();
        }
    }


}
