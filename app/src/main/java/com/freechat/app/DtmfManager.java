package com.freechat.app;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class DtmfManager {

    private ToneGenerator toneGenerator;
    private static final int TONE_DURATION = 150;
    private static final int GAP_DURATION = 50;

    private static final char[] DTMF_CHARS = {
        '0','1','2','3','4','5','6','7','8','9','*','#','A','B','C','D'
    };
    private static final int[] DTMF_TONES = {
        ToneGenerator.TONE_DTMF_0, ToneGenerator.TONE_DTMF_1,
        ToneGenerator.TONE_DTMF_2, ToneGenerator.TONE_DTMF_3,
        ToneGenerator.TONE_DTMF_4, ToneGenerator.TONE_DTMF_5,
        ToneGenerator.TONE_DTMF_6, ToneGenerator.TONE_DTMF_7,
        ToneGenerator.TONE_DTMF_8, ToneGenerator.TONE_DTMF_9,
        ToneGenerator.TONE_DTMF_S, ToneGenerator.TONE_DTMF_P,
        ToneGenerator.TONE_DTMF_A, ToneGenerator.TONE_DTMF_B,
        ToneGenerator.TONE_DTMF_C, ToneGenerator.TONE_DTMF_D
    };

    public DtmfManager() {
        toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 100);
    }

    public void sendMessage(String message) {
        new Thread(() -> {
            try {
                String encoded = encodeToBase16(message);
                for (char c : encoded.toCharArray()) {
                    int toneIndex = getIndex(c);
                    if (toneIndex >= 0) {
                        toneGenerator.startTone(DTMF_TONES[toneIndex], TONE_DURATION);
                        Thread.sleep(TONE_DURATION + GAP_DURATION);
                    }
                }
            } catch (Exception e) {}
        }).start();
    }

    private String encodeToBase16(String message) {
        StringBuilder sb = new StringBuilder();
        for (byte b : message.getBytes()) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private int getIndex(char c) {
        for (int i = 0; i < DTMF_CHARS.length; i++) {
            if (DTMF_CHARS[i] == Character.toUpperCase(c)) return i;
        }
        return -1;
    }

    public void release() {
        if (toneGenerator != null) toneGenerator.release();
    }
}
