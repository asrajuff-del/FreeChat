package com.freechat.app;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import java.nio.charset.Charset;

public class QrNfcManager {

    private Context context;
    private NfcAdapter nfcAdapter;

    public interface OnMessageReceived {
        void onReceived(String message);
    }

    public QrNfcManager(Context context) {
        this.context = context;
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public boolean isNfcAvailable() {
        return nfcAdapter != null;
    }

    public boolean isNfcEnabled() {
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    public String readNfcTag(Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                NdefMessage msg = ndef.getNdefMessage();
                if (msg != null) {
                    NdefRecord[] records = msg.getRecords();
                    if (records.length > 0) {
                        byte[] payload = records[0].getPayload();
                        String text = new String(payload, 3, payload.length - 3, Charset.forName("UTF-8"));
                        ndef.close();
                        return text;
                    }
                }
                ndef.close();
            }
        } catch (Exception e) {}
        return null;
    }

    public String generateQrData(String userName, String uuid, String message) {
        return "freechat://" + userName + "#" + uuid + "?msg=" + message;
    }

    public String[] parseQrData(String qrData) {
        try {
            if (qrData.startsWith("freechat://")) {
                String data = qrData.replace("freechat://", "");
                String[] parts = data.split("\\?msg=");
                return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
            }
        } catch (Exception e) {}
        return null;
    }
}
