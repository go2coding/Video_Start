package com.tb.pcmrecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private Button mStartRecordBtn;
    private Button mStopRecordBtn;
    private Button mPlayAudioBtn;
    private Button mStopAudioBtn;

    private File mAudioFile;

    private RecordUtil mAudioUtil;

    private ExecutorService mExecutorService;

    private static final int BUFFER_SIZE = 1024 * 2;

    private byte[] mBuffer;

    private Button btn_wave;
    private AudioView wave_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioUtil = RecordUtil.getInstance();


        mStartRecordBtn = (Button)findViewById(R.id.start_record_button);
        mStopRecordBtn = (Button)findViewById(R.id.stop_record_button);
        mPlayAudioBtn = (Button)findViewById(R.id.play_audio_button);


        mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/record/encode.pcm");

        mBuffer = new byte[BUFFER_SIZE];

        mExecutorService = Executors.newSingleThreadExecutor();

        mStartRecordBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"start record",Toast.LENGTH_SHORT).show();

                mAudioUtil.startRecord();
                mAudioUtil.recordData();

            }
        });

        mStopRecordBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"stop record",Toast.LENGTH_SHORT).show();
                mAudioUtil.stopRecord();
                mAudioUtil.convertWavFile();

            }
        });

        mPlayAudioBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mAudioFile != null)
                {
                    mExecutorService.submit(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            int streamType = AudioManager.STREAM_MUSIC;
                            int simpleRate = 44100;
                            int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
                            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                            int mode = AudioTrack.MODE_STREAM;

                            int minBufferSize = AudioTrack.getMinBufferSize(simpleRate , channelConfig , audioFormat);
                            AudioTrack audioTrack = new AudioTrack(streamType , simpleRate , channelConfig , audioFormat ,
                                    Math.max(minBufferSize , BUFFER_SIZE) , mode);
                            audioTrack.play();


                            FileInputStream inputStream = null;
                            try
                            {
                                inputStream = new FileInputStream(mAudioFile);
                                int read;
                                while ((read = inputStream.read(mBuffer)) > 0)
                                {


                                    audioTrack.write(mBuffer , 0 , read);
                                }
                            }
                            catch (RuntimeException | IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        btn_wave = (Button) findViewById(R.id.show_wave_button);
        wave_view = (AudioView)findViewById(R.id.waveview);

        btn_wave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                wave_view.showPcmFileWave(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/record/encode.pcm"));
            }
        });
    }
}
