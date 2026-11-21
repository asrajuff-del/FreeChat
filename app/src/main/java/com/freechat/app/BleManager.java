package com.freechat.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class BleManager {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private List<BluetoothDevice> foundDevices = new ArrayList<>();
    private OnDeviceFoundListener listener;
    private Context context;

    public interface OnDeviceFoundListener {
        void onDeviceFound(String name, String address);
    }

    public BleManager(Context context, OnDeviceFoundListener listener) {
        this.context = context;
        this.listener = listener;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void startScan() {
        if (!hasPermissions()) return;
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                scanner.startScan(scanCallback);
            }
        }
    }

    public void stopScan() {
        if (scanner != null) {
            try { scanner.stopScan(scanCallback); } catch (Exception e) {}
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!foundDevices.contains(device)) {
                foundDevices.add(device);
                String name = "Unknown";
                try { name = device.getName(); if (name == null) name = "Unknown"; }
                catch (Exception e) {}
                final String finalName = name;
                listener.onDeviceFound(finalName, device.getAddress());
            }
        }
    };
}
