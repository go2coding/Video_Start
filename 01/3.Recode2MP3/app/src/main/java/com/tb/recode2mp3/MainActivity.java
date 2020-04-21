package com.tb.recode2mp3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn_start;
    private Button btn_stop;

    private RecordUtil mAudioUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioUtil = RecordUtil.getInstance();

        btn_start = (Button) findViewById(R.id.start_record_button);
        btn_start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"start record",Toast.LENGTH_SHORT).show();

                mAudioUtil.startRecord();
                mAudioUtil.recordData();

            }
        });

        btn_stop = (Button) findViewById(R.id.stop_record_button);
        btn_stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"stop record",Toast.LENGTH_SHORT).show();

                mAudioUtil.stopRecord();

            }
        });

    }
}
