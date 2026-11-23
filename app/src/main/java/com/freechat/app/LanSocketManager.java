package com.freechat.app;

import android.content.Context;
import android.net.wifi.WifiManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LanSocketManager {

    private static final int PORT = 54321;
    private Context context;
    private OnMessageReceived listener;
    private ServerSocket serverSocket;
    private boolean isRunning = false;

    public interface OnMessageReceived {
        void onReceived(String message, String fromIp);
    }

    public LanSocketManager(Context context, OnMessageReceived listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startServer() {
        isRunning = true;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                while (isRunning) {
                    Socket client = serverSocket.accept();
                    String fromIp = client.getInetAddress().getHostAddress();
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                    String message = reader.readLine();
                    if (message != null && message.startsWith("[FreeChat]")) {
                        listener.onReceived(message.replace("[FreeChat]", ""), fromIp);
                    }
                    client.close();
                }
            } catch (Exception e) {}
        }).start();
    }

    public void sendMessage(String ip, String message) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(ip, PORT);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("[FreeChat]" + message);
                socket.close();
            } catch (Exception e) {}
        }).start();
    }

    public String getMyIp() {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
            int ip = wm.getConnectionInfo().getIpAddress();
            return String.format("%d.%d.%d.%d",
                (ip & 0xff), (ip >> 8 & 0xff),
                (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }

    public void stop() {
        isRunning = false;
        try { if (serverSocket != null) serverSocket.close(); }
        catch (Exception e) {}
    }
}
