package com.xyyy.www.audiorecord;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * @author liuml
 * @explain
 * @time 2018/12/18 09:35
 */
public class AudioRecordUtil {

    private AudioRecord audioRecord;
    private int bufferSizeInBytes;
    private boolean start = false;
    private int readSize;
    private OnRecordListener onRecordListener;

    public AudioRecordUtil() {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        /**
         * 参数 1 , 麦克风, 2,采样率, 3,声道数, 4 采样位数,5,最小的数据大小
         */
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes);
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    public void startRecord() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                audioRecord.startRecording();
                start = true;
                byte[] audiodata = new byte[bufferSizeInBytes];

                while (start) {
                    readSize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
                    if (onRecordListener != null) {
                        onRecordListener.recordByte(audiodata, readSize);
                    }
                }

                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audiodata = null;
                }
            }
        }.start();
    }

    public void stopRecord() {
        start = false;
    }

    public interface OnRecordListener {
        void recordByte(byte[] audioData, int readSize);
    }

    public boolean isStart(){
        return start;
    }


}
