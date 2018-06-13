package wenchao.kiosk;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Process;

import java.util.Arrays;

/**
 * Created by ASANDA on 2018/01/13.
 */

public final class AsyncRecorder {
    public static final int ERROR_CODE_UNINITIALIZED = 0;
    public static final int SAMPLING_RATE = 16000;
    public static final byte CHANNELS = 1;
    public static final short BIT_RATE = 16;

    private final RecordingCallback recodingCallback;
    private boolean isRecording;
    private boolean ContinueRecording;
    private Thread backgroundWorker;

    public AsyncRecorder(RecordingCallback pRecordingCallback) {
        this.ContinueRecording = false;
        this.isRecording = false;
        this.recodingCallback = pRecordingCallback;
    }

    public final void Start(){
        if(this.isRecording())
            return;

        this.ContinueRecording = true;
        this.backgroundWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

                int bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLING_RATE * 2;
                }

                byte[] AudioBuffer = new byte[bufferSize];
                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
                if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    if(AsyncRecorder.this.recodingCallback != null)
                        AsyncRecorder.this.recodingCallback.onError(ERROR_CODE_UNINITIALIZED);
                    return;
                }

                recorder.startRecording();
                if(AsyncRecorder.this.recodingCallback != null)
                    AsyncRecorder.this.recodingCallback.onStart();

                long totalDataRead = 0;
                while (AsyncRecorder.this.ContinueRecording) {
                    int readLength = recorder.read(AudioBuffer, 0, AudioBuffer.length);
                    totalDataRead += readLength;
                    if (AsyncRecorder.this.recodingCallback != null)
                        AsyncRecorder.this.recodingCallback.onRead(Arrays.copyOf(AudioBuffer,  readLength));
                }

                recorder.stop();
                recorder.release();
                AsyncRecorder.this.isRecording = false;
                if(AsyncRecorder.this.recodingCallback != null)
                    AsyncRecorder.this.recodingCallback.onFinish(totalDataRead, calculateAudioLength(totalDataRead));
            }
        });

        this.backgroundWorker.start();
        this.isRecording = true;
    }

    private double calculateAudioLength(long audioSize) {
        return (audioSize / (((double) SAMPLING_RATE * (double)CHANNELS * (double)BIT_RATE) / 8.00));
    }

    public void Stop(){
        this.ContinueRecording = !this.isRecording;
    }

    public final boolean isRecording(){
        return this.isRecording;
    }

    public interface RecordingCallback{
        void onStart();
        void onRead(byte[] buffer);
        void onFinish(long rawAudioSize, double rawAudioLength);
        void onError(int errorCode);
    }
}