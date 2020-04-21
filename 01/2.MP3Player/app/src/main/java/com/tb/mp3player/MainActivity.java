package com.tb.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp3player;
    private Button btn_pause;
    private SeekBar seekbar_song;

    Thread updateseekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_pause = (Button) findViewById(R.id.pause);
        seekbar_song = (SeekBar) findViewById(R.id.seekBar);

        btn_pause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                seekbar_song.setMax(mp3player.getDuration());
                if(mp3player.isPlaying()){
                    mp3player.pause();
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                }else {
                    mp3player.start();
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                }
            }
        });

        runtimePermision();

        updateseekBar = new Thread(){

            public void run(){
                int totalDuration = mp3player.getDuration();
                int currentPosition = 0;

                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition= mp3player.getCurrentPosition();
                        seekbar_song.setProgress(currentPosition);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        updateseekBar.start();


        seekbar_song.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp3player.seekTo(seekBar.getProgress());
            }
        });


    }

    public void runtimePermision(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        playmp3();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    void playmp3(){

        Uri u = Uri.parse(Environment.getExternalStorageDirectory() + "/q.mp3");
        mp3player = MediaPlayer.create(getApplicationContext(),u);
        if(mp3player != null){
            mp3player.start();
            seekbar_song.setMax(mp3player.getDuration());
        }


    }
}
