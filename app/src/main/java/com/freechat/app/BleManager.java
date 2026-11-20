package com.freechat.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class BleManager {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private List<BluetoothDevice> foundDevices = new ArrayList<>();
    private OnDeviceFoundListener listener;

    public interface OnDeviceFoundListener {
        void onDeviceFound(String name, String address);
    }

    public BleManager(Context context, OnDeviceFoundListener listener) {
        this.listener = listener;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                scanner.startScan(scanCallback);
            }
        }
    }

    public void stopScan() {
        if (scanner != null) {
            scanner.stopScan(scanCallback);
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!foundDevices.contains(device)) {
                foundDevices.add(device);
                String name = device.getName();
                if (name == null) name = "Unknown";
                listener.onDeviceFound(name, device.getAddress());
            }
        }
    };
}
