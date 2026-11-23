package com.freechat.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import java.util.ArrayList;

public class MmsManager {

    private Context context;

    public MmsManager(Context context) {
        this.context = context;
    }

    public void sendTextViaMms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage("[FreeChat]" + message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        } catch (Exception e) {}
    }

    public void sendImageViaMms(String phoneNumber, Uri imageUri, String caption) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.putExtra("address", phoneNumber);
            intent.putExtra("sms_body", "[FreeChat]" + caption);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {}
    }

    public void sendFileViaMms(String phoneNumber, Uri fileUri, String fileName) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.putExtra("address", phoneNumber);
            intent.putExtra("sms_body", "[FreeChat]File:" + fileName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {}
    }
}
