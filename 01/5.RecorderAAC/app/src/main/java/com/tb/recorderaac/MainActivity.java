package com.tb.recorderaac;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btn_start;
    private Button btn_stop;

    private MediaRecorder recorder;
    private File audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (Button) findViewById(R.id.start_record_button);
        btn_stop = (Button) findViewById(R.id.stop_record_button);

        btn_start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"start record", Toast.LENGTH_SHORT).show();

                recorder = new MediaRecorder();
                //设置音频输入源
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //设置音频的输出格式
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                //设置音频的编码格式
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/");
                path.mkdirs();

                try {
                    audioFile = File.createTempFile("recording", ".aac", path);
                } catch(IOException e) {
                    Log.v("Exception in CreateFile", e.toString());
                }
                //设置音频输出位置
                recorder.setOutputFile(audioFile.getAbsolutePath());
                //开始录制音频
                try {
                    recorder.prepare();
                } catch(Exception e) {
                    Log.v("MediaRecorder prepare error", e.toString());
                }
                recorder.start();

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"stop record",Toast.LENGTH_SHORT).show();

                recorder.stop();
                recorder.release();

            }
        });

    }
}
