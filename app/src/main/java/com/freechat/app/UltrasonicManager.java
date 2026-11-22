package com.freechat.app;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class UltrasonicManager {

    private static final int SAMPLE_RATE = 44100;
    private static final int FREQ_0 = 18000;
    private static final int FREQ_1 = 19000;
    private static final int BIT_DURATION_MS = 100;

    private OnMessageReceived listener;
    private boolean isListening = false;

    public interface OnMessageReceived {
        void onReceived(String message);
    }

    public UltrasonicManager(OnMessageReceived listener) {
        this.listener = listener;
    }

    public void sendMessage(String message) {
        new Thread(() -> {
            byte[] bytes = message.getBytes();
            for (byte b : bytes) {
                for (int i = 7; i >= 0; i--) {
                    int bit = (b >> i) & 1;
                    playTone(bit == 1 ? FREQ_1 : FREQ_0, BIT_DURATION_MS);
                }
            }
            playTone(FREQ_0, BIT_DURATION_MS * 2);
        }).start();
    }

    private void playTone(int frequency, int durationMs) {
        int samples = (SAMPLE_RATE * durationMs) / 1000;
        short[] buffer = new short[samples];
        for (int i = 0; i < samples; i++) {
            buffer[i] = (short) (Short.MAX_VALUE *
                Math.sin(2.0 * Math.PI * frequency * i / SAMPLE_RATE));
        }
        AudioTrack track = new AudioTrack(
            AudioManager.STREAM_MUSIC, SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            buffer.length * 2, AudioTrack.MODE_STATIC);
        track.write(buffer, 0, buffer.length);
        track.play();
        try { Thread.sleep(durationMs); } catch (Exception e) {}
        track.stop();
        track.release();
    }

    public void startListening() {
        isListening = true;
        new Thread(() -> {
            int bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
            recorder.startRecording();
            StringBuilder received = new StringBuilder();
            while (isListening) {
                short[] buffer = new short[bufferSize];
                recorder.read(buffer, 0, bufferSize);
            }
            recorder.stop();
            recorder.release();
        }).start();
    }

    public void stopListening() {
        isListening = false;
    }
}
