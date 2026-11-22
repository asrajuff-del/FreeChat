package com.freechat.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SmartRouter {

    private Context context;
    private MessageQueue queue;

    public enum Route {
        BLE, WIFI_DIRECT, SMS, INTERNET, QUEUE
    }

    public interface OnRoutedListener {
        void onRouted(Route route, boolean success);
    }

    public SmartRouter(Context context) {
        this.context = context;
        this.queue = new MessageQueue(context);
    }

    public void sendMessage(String to, String text, OnRoutedListener listener) {
        new Thread(() -> {
            if (tryBLE(to, text)) {
                listener.onRouted(Route.BLE, true);
            } else if (tryWifiDirect(to, text)) {
                listener.onRouted(Route.WIFI_DIRECT, true);
            } else if (tryInternet(to, text)) {
                listener.onRouted(Route.INTERNET, true);
            } else if (trySMS(to, text)) {
                listener.onRouted(Route.SMS, true);
            } else {
                saveToQueue(to, text);
                listener.onRouted(Route.QUEUE, false);
            }
        }).start();
    }

    private boolean tryBLE(String to, String text) {
        return false;
    }

    private boolean tryWifiDirect(String to, String text) {
        return false;
    }

    private boolean tryInternet(String to, String text) {
        ConnectivityManager cm = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private boolean trySMS(String to, String text) {
        return false;
    }

    private void saveToQueue(String to, String text) {
        MessageQueue.PendingMessage msg = new MessageQueue.PendingMessage(to, text);
        queue.addMessage(msg);
    }

    public String getRouteStatus() {
        int pending = queue.getPendingCount();
        if (pending > 0) return "⏳ " + pending + " message pending";
        return "✅ सब ठीक है";
    }
}
