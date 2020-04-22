package com.tb.mp3wave;

/**
 * Created by hc on 2019.4.28.
 */
public class NativeMP3Decoder {

    static {
        System.loadLibrary("mad");
    }

    public native int initAudioPlayer(String file, int StartAddr);

    public native int getAudioBuf(short[] audioBuffer, int numSamples);

    public native int getAudioSamplerate();

    public native int getAudioFileSize();

    public native void rePlayAudioFile();

    public native void closeAudioFile();
}
