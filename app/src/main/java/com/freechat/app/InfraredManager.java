package com.freechat.app;

import android.content.Context;
import android.hardware.ConsumerIrManager;

public class InfraredManager {

    private ConsumerIrManager irManager;
    private static final int CARRIER_FREQ = 38000;
    private static final int BIT_MARK = 560;
    private static final int BIT_SPACE_1 = 1690;
    private static final int BIT_SPACE_0 = 560;

    public InfraredManager(Context context) {
        irManager = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
    }

    public boolean isAvailable() {
        return irManager != null && irManager.hasIrEmitter();
    }

    public void sendMessage(String message) {
        if (!isAvailable()) return;
        new Thread(() -> {
            try {
                byte[] bytes = message.getBytes();
                int[] pattern = buildPattern(bytes);
                irManager.transmit(CARRIER_FREQ, pattern);
            } catch (Exception e) {}
        }).start();
    }

    private int[] buildPattern(byte[] data) {
        int totalBits = data.length * 8;
        int[] pattern = new int[2 + totalBits * 2 + 1];
        int idx = 0;
        pattern[idx++] = 9000;
        pattern[idx++] = 4500;
        for (byte b : data) {
            for (int i = 7; i >= 0; i--) {
                pattern[idx++] = BIT_MARK;
                pattern[idx++] = ((b >> i) & 1) == 1 ? BIT_SPACE_1 : BIT_SPACE_0;
            }
        }
        pattern[idx] = BIT_MARK;
        return pattern;
    }
}
