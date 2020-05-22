package com.tb.androidtuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format

    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final String[] pitchClasses = new String[] {"A","A#/Bb","B","C","C#/Db",
            "D","D#/Eb","E","F","F#/Fb","G","G#/Gb","A"};
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private TextView results = null;



    private void permissionCheckAndProceed() {
        ActivityCompat.requestPermissions(
                this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO}, 1);
    }

    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        for (int i=0; i<permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                this.finish();
                System.exit(0);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Check permissions. Only do this once app starts.
        permissionCheckAndProceed();

        setContentView(R.layout.activity_main);
    }

    public void tunerButtonPress(View view) {
        TextView tv = (TextView) findViewById(R.id.content);

        int bufferSize = AudioRecord.getMinBufferSize(SmoothedFrequency.RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        if(isRecording) {
            tv.setText("Analysis Ended!");
            stopRecordingAnalysis();
            Button i = (Button)findViewById(R.id.tuner_button);
            i.setText(R.string.button_tuner_stop);
        } else {
            tv.setText("Analysis Started...");
            startRecordingAnalysis();
            Button i = (Button)findViewById(R.id.tuner_button);
            i.setText(R.string.button_tuner_start);
        }
    }

    private void startRecordingAnalysis() {
        results = (TextView) findViewById(R.id.result);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SmoothedFrequency.RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                analyzeAudioData();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void stopRecordingAnalysis() {

        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    private void analyzeAudioData(){
        short sData[] = new short[BufferElements2Rec];
        int pitchLoc;
        int pitchHz;
        String pianoKeyMessage;

        final int LOOK_BEHIND_WINDOW = 3;

        SmoothedFrequency buf = new SmoothedFrequency(LOOK_BEHIND_WINDOW, BufferElements2Rec);

        while (isRecording) {
            /* gets the voice output from microphone to byte format */

            List<Double> sl = new ArrayList<Double>();

            for(int j = 0;j < BufferElements2Rec;j++) {
                double t = j / 44100.0f;

                //sData[j] = (short) (1000.0f * Math.sin(2 * Math.PI * 440.f * t));
                sl.add(1000.0f * Math.sin(2 * Math.PI * 440.f * t));
            }

            recorder.read(sData, 0, BufferElements2Rec);
            pitchHz = buf.evaluate(sData);

            /*DSP.init(BufferElements2Rec);
            double[] dbuf = new double[DSP.fftlen];

            for (int i = 0; i < DSP.fftlen; ++i) {
                dbuf[i] = sData[i] / 1024.0f;
            }
            final double freq = DSP.freq(dbuf, 44100);

            FrequencyFinder ff = new FrequencyFinder();
            double df = ff.getFrequency(sData,44100);
            ff.calculatePowerSpectrum(sl);*/


            pianoKeyMessage = buf.pianoKeyLocation(pitchHz);

            final int frequency = pitchHz;

            final String displayMessage = pianoKeyMessage;


            results.post(new Runnable() {
                public void run() {
                    results.setText(displayMessage);
                }
            });
        }
    }
}
