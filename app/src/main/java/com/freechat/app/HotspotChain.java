package com.freechat.app;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import java.net.HttpURLConnection;
import java.net.URL;

public class HotspotChain {

    private static final String FREECHAT_SSID = "FreeChat_Mesh";
    private static final String FREECHAT_PASS = "freechat123";
    private static final int MESH_PORT = 54322;

    private Context context;
    private OnMessageReceived listener;

    public interface OnMessageReceived {
        void onReceived(String message);
    }

    public HotspotChain(Context context, OnMessageReceived listener) {
        this.context = context;
        this.listener = listener;
    }

    public void sendViaHotspot(String message) {
        new Thread(() -> {
            try {
                String[] gatewayIps = {"192.168.43.1", "192.168.1.1", "10.0.0.1"};
                for (String ip : gatewayIps) {
                    if (trySendToGateway(ip, message)) break;
                }
            } catch (Exception e) {}
        }).start();
    }

    private boolean trySendToGateway(String ip, String message) {
        try {
            URL url = new URL("http://" + ip + ":" + MESH_PORT + "/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(2000);
            conn.setDoOutput(true);
            conn.getOutputStream().write(("[FreeChat]" + message).getBytes());
            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFreeeChatHotspotAvailable() {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
            String ssid = wm.getConnectionInfo().getSSID();
            return ssid != null && ssid.contains("FreeChat");
        } catch (Exception e) {
            return false;
        }
    }

    public String getMeshStatus() {
        if (isFreeeChatHotspotAvailable()) {
            return "🔗 FreeChat Mesh से जुड़ा है";
        }
        return "📵 Mesh उपलब्ध नहीं";
    }
}
