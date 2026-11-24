package com.freechat.app;

import android.content.Context;
import android.telephony.SmsManager;

public class SmsSender {

    private Context context;

    public SmsSender(Context context) {
        this.context = context;
    }

    public void sendMessage(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String smsText = "[FreeChat]" + message;
            smsManager.sendTextMessage(phoneNumber, null, smsText, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
