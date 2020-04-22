package com.tb.pcm2aac;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn_start;
    private Button btn_stop;

    private Recorder mAudioUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioUtil = new Recorder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/pcm2aac.aac");


        btn_start = (Button) findViewById(R.id.start_record_button);
        btn_start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"start record",Toast.LENGTH_SHORT).show();

                mAudioUtil.start();


            }
        });

        btn_stop = (Button) findViewById(R.id.stop_record_button);
        btn_stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"stop record",Toast.LENGTH_SHORT).show();

                mAudioUtil.stop();

            }
        });
    }
}
