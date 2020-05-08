package com.tb.genwav;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private TextView tx;

    public int streamType = AudioManager.STREAM_MUSIC;
    public int simpleRate = 44100;
    public int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    public int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public int mode = AudioTrack.MODE_STREAM;
    public float[] freqs = new float[89];

    public static Map<String, Float> yinfu = new HashMap<String, Float>();

    private AudioTrack audioTrack;
    public void genWave(float freq,int interval)
    {
        byte[] mf = new byte[ 1024 *4];

        for(int i = 0; i < 5*interval;i++)
        {
            for(int j = 0;j < 1024;j++) {
                double t = (i*1024 + j) / 44100.0f;

                short st = (short) (50000.0f * Math.sin(2 * Math.PI * freq * t));


                mf[j*4 + 0] = (byte) (st & 0x00FF);
                mf[j*4 + 1] = (byte) ((st & 0xFF00) >> 8);
                mf[j*4 + 2] = (byte) (st & 0x00FF);
                mf[j*4 + 3] = (byte) ((st & 0xFF00) >> 8);
            }
            audioTrack.write(mf , 0 , 1024 *4);
        }

        Arrays.fill(mf,(byte)0);
        audioTrack.write(mf , 0 , 1024 *4);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        freqs[0] =0.f;
        freqs[1] =27.500f;freqs[2] =29.135f;freqs[3] =30.867f;freqs[4] =32.703f;freqs[5] =34.648f;freqs[6] =36.708f;freqs[7] =38.891f;freqs[8] =41.203f;
        freqs[9] =43.654f;freqs[10] =46.249f;freqs[11] =48.999f;freqs[12] =51.913f;freqs[13] =55.000f;freqs[14] =55.000f;freqs[15] =61.735f;freqs[16] =65.406f;
        freqs[17] =69.296f;freqs[18] =73.416f;freqs[19] =77.782f;freqs[20] =82.407f;freqs[21] =87.307f;freqs[22] =92.499f;freqs[23] =97.999f;freqs[24] =103.826f;
        freqs[25] =110.000f;freqs[26] =116.541f;freqs[27] =123.471f;freqs[28] =130.813f;freqs[29] =138.591f;freqs[30] =146.832f;freqs[31] =155.563f;freqs[32] =164.814f;
        freqs[33] =174.614f;freqs[34] =184.997f;freqs[35] =195.998f;freqs[36] =207.652f;freqs[37] =220.000f;freqs[38] =233.082f;freqs[39] =246.942f;freqs[40] =261.626f;
        freqs[41] =277.183f;freqs[42] =293.665f;freqs[43] =311.127f;freqs[44] =329.628f;freqs[45] =349.228f;freqs[46] =369.994f;freqs[47] =391.995f;freqs[48] =415.305f;
        freqs[49] =440.000f;freqs[50] =466.164f;freqs[51] =493.883f;freqs[52] =523.251f;freqs[53] =554.365f;freqs[54] =587.330f;freqs[55] =622.254f;freqs[56] =659.255f;
        freqs[65] =1108.731f;freqs[66] =1174.659f;freqs[67] =1244.508f;freqs[68] =1318.510f;freqs[69] =1396.913f;freqs[70] =1479.978f;freqs[71] =1567.982f;freqs[72] =1661.219f;
        freqs[73] =1760.000f;freqs[74] =1864.655f;freqs[75] =1975.533f;freqs[76] =2093.005f;freqs[77] =2217.461f;freqs[78] =2349.318f;freqs[79] =2489.016f;freqs[80] =2637.020f;
        freqs[81] =2793.826f;freqs[82] =2959.955f;freqs[83] =3135.963f;freqs[84] =3322.438f;freqs[85] =3520.000f;freqs[86] =3729.310f;freqs[87] =3951.066f;freqs[88] =4186.009f;


        yinfu.put("0",freqs[0]);

        yinfu.put("!1",freqs[16]);yinfu.put("!1^",freqs[17]);yinfu.put("!2",freqs[17]);yinfu.put("!2^",freqs[18]);yinfu.put("!3",freqs[20]);yinfu.put("!4",freqs[21]);yinfu.put("!5",freqs[22]);yinfu.put("!5",freqs[23]);yinfu.put("!5^",freqs[24]);yinfu.put("!6",freqs[25]);yinfu.put("!6^",freqs[26]);yinfu.put("!7",freqs[27]);
        yinfu.put("1",freqs[28]);yinfu.put("1^",freqs[29]);yinfu.put("2",freqs[30]);yinfu.put("2^",freqs[31]);yinfu.put("3",freqs[32]);yinfu.put("4",freqs[33]);yinfu.put("4^",freqs[34]);yinfu.put("5",freqs[35]);yinfu.put("5^",freqs[36]);yinfu.put("6",freqs[37]);yinfu.put("6^",freqs[38]);yinfu.put("7",freqs[39]);
        yinfu.put("1!",freqs[40]);yinfu.put("1!^",freqs[41]);yinfu.put("2!",freqs[42]);yinfu.put("2!^",freqs[43]);yinfu.put("3!",freqs[44]);yinfu.put("4!",freqs[45]);yinfu.put("4!^",freqs[46]);yinfu.put("5!",freqs[47]);yinfu.put("5!^",freqs[48]);yinfu.put("6!",freqs[49]);yinfu.put("6!^",freqs[50]);yinfu.put("7!",freqs[51]);

        int minBufferSize = AudioTrack.getMinBufferSize(simpleRate , channelConfig , audioFormat);
        audioTrack = new AudioTrack(streamType , simpleRate , channelConfig , audioFormat ,
                Math.max(minBufferSize , 1024 *4) , mode);
        audioTrack.play();

        tx = (TextView)findViewById(R.id.hello);
        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                genWave(yinfu.get("0"),2);
                genWave(yinfu.get("5"),2);

                genWave(yinfu.get("5"),2);
                genWave(yinfu.get("1"),2);

                genWave(yinfu.get("1"),4);

                genWave(yinfu.get("2"),2);
                genWave(yinfu.get("3"),2);

                genWave(yinfu.get("0"),2);
                genWave(yinfu.get("5"),2);

                genWave(yinfu.get("5"),2);
                genWave(yinfu.get("1"),2);

                genWave(yinfu.get("1"),2);
                genWave(yinfu.get("2"),1);
                genWave(yinfu.get("3"),1);

                genWave(yinfu.get("2"),1);
                genWave(yinfu.get("1"),1);
                genWave(yinfu.get("!5"),2);

                genWave(yinfu.get("0"),2);
                genWave(yinfu.get("5"),2);

                genWave(yinfu.get("5"),2);
                genWave(yinfu.get("1"),2);

                genWave(yinfu.get("1"),4);

                genWave(yinfu.get("2"),2);
                genWave(yinfu.get("3"),2);

                genWave(yinfu.get("0"),2);

                genWave(yinfu.get("3"),4);

                genWave(yinfu.get("2"),2);
                genWave(yinfu.get("3"),2);

                genWave(yinfu.get("4"),1);
                genWave(yinfu.get("3"),1);
                genWave(yinfu.get("2"),1);
                genWave(yinfu.get("4"),1);

                genWave(yinfu.get("3"),1);
                genWave(yinfu.get("2"),1);
                genWave(yinfu.get("1"),2);

                genWave(yinfu.get("!5"),2);
                genWave(yinfu.get("1"),2);

                genWave(yinfu.get("1"),2);
                genWave(yinfu.get("3"),2);

                genWave(yinfu.get("4"),2);
                genWave(yinfu.get("3"),2);


                genWave(yinfu.get("2"),2);
                genWave(yinfu.get("1"),1);
                genWave(yinfu.get("2"),1);

                genWave(yinfu.get("3"),2);
                genWave(yinfu.get("3"),2);

                genWave(yinfu.get("3"),2);
                genWave(yinfu.get("3"),2);


                genWave(yinfu.get("2"),1);
                genWave(yinfu.get("3"),1);
                genWave(yinfu.get("2"),2);

                genWave(yinfu.get("1"),4);


                genWave(yinfu.get("!5"),2);
                genWave(yinfu.get("1"),2);

                genWave(yinfu.get("2"),2);
                genWave(yinfu.get("3"),2);

                genWave(yinfu.get("4^"),2);
                genWave(yinfu.get("3"),2);

                genWave(yinfu.get("2"),2);
                genWave(yinfu.get("1"),1);
                genWave(yinfu.get("2"),1);


            }
        });

    }
}
