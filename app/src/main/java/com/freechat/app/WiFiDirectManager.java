package com.freechat.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import java.util.List;

public class WiFiDirectManager {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Context context;
    private OnDeviceFoundListener listener;
    private BroadcastReceiver receiver;
    private boolean isRegistered = false;

    public interface OnDeviceFoundListener {
        void onDeviceFound(String name, String address);
    }

    public WiFiDirectManager(Context context, OnDeviceFoundListener listener) {
        this.context = context;
        this.listener = listener;
        try {
            manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
            if (manager != null) {
                channel = manager.initialize(context, context.getMainLooper(), null);
            }
        } catch (Exception e) {}
    }

    public void startDiscovery() {
        if (manager == null || channel == null) return;
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctx, Intent intent) {
                    if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(intent.getAction())) {
                        try {
                            manager.requestPeers(channel, peerList -> {
                                List<WifiP2pDevice> devices = (List<WifiP2pDevice>) peerList.getDeviceList();
                                for (WifiP2pDevice device : devices) {
                                    String name = device.deviceName.isEmpty() ? "Unknown" : device.deviceName;
                                    listener.onDeviceFound("📶 " + name, device.deviceAddress);
                                }
                            });
                        } catch (Exception e) {}
                    }
                }
            };
            context.registerReceiver(receiver, filter);
            isRegistered = true;
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override public void onSuccess() {}
                @Override public void onFailure(int reason) {}
            });
        } catch (Exception e) {}
    }

    public void stop() {
        try {
            if (isRegistered && receiver != null) {
                context.unregisterReceiver(receiver);
                isRegistered = false;
            }
            if (manager != null && channel != null) {
                manager.stopPeerDiscovery(channel, null);
            }
        } catch (Exception e) {}
    }
}
