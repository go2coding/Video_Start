package com.tb.pcm2aac;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaRecorder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * AudioRecord (PCM -> AAC)
 *
 * @author JYangkai
 * @since 2019-11-09
 */
public class Recorder {
    private static final int STATUS_NOT_READY = 0;
    private static final int STATUS_READY = 1;
    private static final int STATUS_RECORDING = 2;
    private static final int STATUS_PAUSE = 3;
    private static final int STATUS_STOP = 4;

    private int status;

    private AudioRecord audioRecord;
    private MediaCodec mediaCodec;

    private String outputPath;

    private int bufferSizeInBytes;
    private int channelCount;

    public Recorder(String outputPath) {
        this(outputPath, MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
    }

    public Recorder(String outputPath, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        this.outputPath = outputPath;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            if (channelConfig == AudioFormat.CHANNEL_IN_STEREO) {
                channelCount = 2;
            } else {
                channelCount = 1;
            }
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRateInHz, channelCount);

            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
            //mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);

            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            status = STATUS_READY;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     */
    public void start() {
        if (status == STATUS_READY) {
            status = STATUS_RECORDING;
            recording();
        } else if (status == STATUS_PAUSE) {
            status = STATUS_RECORDING;
        }
    }

    /**
     * 暂停录音
     */
    public void pause() {
        if (status == STATUS_RECORDING) {
            status = STATUS_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public void stop() {
        if (status == STATUS_RECORDING || status == STATUS_PAUSE) {
            status = STATUS_STOP;
        }
    }

    /**
     * 开启录音线程
     */
    private void recording() {
        if (audioRecord == null || mediaCodec == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioRecord.startRecording();
                mediaCodec.start();
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(outputPath);
                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                    boolean isEnd = false;
                    while (status != STATUS_NOT_READY && status != STATUS_READY && status != STATUS_STOP && !isEnd) {
                        if (status == STATUS_PAUSE) {
                            continue;
                        }
                        int inputBufferId = mediaCodec.dequeueInputBuffer(-1);
                        if (inputBufferId >= 0) {
                            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferId);
                            int readSize = -1;
                            if (inputBuffer != null) {
                                readSize = audioRecord.read(inputBuffer, bufferSizeInBytes);
                            }
                            if (readSize >= 0) {
                                mediaCodec.queueInputBuffer(inputBufferId, 0, readSize, 0, 0);
                            } else {
                                mediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                isEnd = true;
                            }
                        }
                        int outputBufferId = mediaCodec.dequeueOutputBuffer(info, -1);
                        if (outputBufferId >= 0) {
                            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferId);
                            int size = info.size;
                            if (size > 0 && outputBuffer != null) {
                                byte[] data = new byte[size + 7];
                                addADTSHeader(data, size + 7);
                                outputBuffer.get(data, 7, size);
                                fos.write(data);
                                outputBuffer.clear();
                            }
                            mediaCodec.releaseOutputBuffer(outputBufferId, false);
                        }
                    }
                    fos.flush();
                    fos.close();
                    audioRecord.stop();
                    audioRecord.release();
                    mediaCodec.stop();
                    mediaCodec.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 添加AAC帧文件头
     * @param packet packet
     * @param packetLen packetLen
     */
    private void addADTSHeader(byte[] packet, int packetLen) {
        int profile = 2; // AAC
        int freqIdx = 4; // 44.1kHz
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (channelCount >> 2));
        packet[3] = (byte) (((channelCount & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}
