package com.freechat.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GatewayNode {

    private static final String SERVER = "https://freechat-relay.onrender.com";
    private Context context;
    private OnMessageReceived listener;

    public interface OnMessageReceived {
        void onReceived(String from, String message);
    }

    public GatewayNode(Context context, OnMessageReceived listener) {
        this.context = context;
        this.listener = listener;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public void sendViaInternet(String toUser, String message, OnSentListener sent) {
        if (!isInternetAvailable()) {
            sent.onResult(false);
            return;
        }
        new Thread(() -> {
            try {
                URL url = new URL(SERVER + "/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                String json = "{\"to\":\"" + toUser + "\",\"msg\":\"" + message + "\"}";
                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.close();
                int code = conn.getResponseCode();
                conn.disconnect();
                sent.onResult(code == 200);
            } catch (Exception e) {
                sent.onResult(false);
            }
        }).start();
    }

    public void checkMessages(String myUser) {
        if (!isInternetAvailable()) return;
        new Thread(() -> {
            try {
                URL url = new URL(SERVER + "/receive?user=" + myUser);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                conn.disconnect();
                String response = sb.toString();
                if (!response.isEmpty() && !response.equals("[]")) {
                    listener.onReceived("Internet", response);
                }
            } catch (Exception e) {}
        }).start();
    }

    public interface OnSentListener {
        void onResult(boolean success);
    }
}
