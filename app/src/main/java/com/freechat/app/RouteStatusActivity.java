package com.freechat.app;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RouteStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_status);
        updateStatus();
    }

    private void updateStatus() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        StringBuilder sb = new StringBuilder();
        sb.append("📡 FreeChat — रास्तों की स्थिति\n\n");

        // BLE
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        sb.append(bt != null && bt.isEnabled() ? "🔵 Bluetooth: ✅ चालू\n" : "🔵 Bluetooth: ❌ बंद\n");

        // WiFi
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sb.append(wm.isWifiEnabled() ? "📶 WiFi: ✅ चालू\n" : "📶 WiFi: ❌ बंद\n");

        // Internet
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        sb.append(ni != null && ni.isConnected() ? "🌍 Internet: ✅ जुड़ा है\n" : "🌍 Internet: ❌ नहीं है\n");

        // NFC
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        sb.append(nfc != null && nfc.isEnabled() ? "📲 NFC: ✅ चालू\n" : "📲 NFC: ❌ बंद\n");

        // IR
        android.hardware.ConsumerIrManager ir = (android.hardware.ConsumerIrManager)
            getSystemService(Context.CONSUMER_IR_SERVICE);
        sb.append(ir != null && ir.hasIrEmitter() ? "🔴 Infrared: ✅ उपलब्ध\n" : "🔴 Infrared: ❌ नहीं\n");

        // SMS
        sb.append("💬 SMS: ✅ हमेशा उपलब्ध\n");
        sb.append("🔊 Ultrasonic: ✅ उपलब्ध\n");
        sb.append("📞 DTMF: ✅ उपलब्ध\n");
        sb.append("📦 Dead Drop: ✅ उपलब्ध\n");
        sb.append("🚗 Vehicle Relay: ✅ उपलब्ध\n");
        sb.append("⏳ Message Queue: ✅ उपलब्ध\n");

        sb.append("\n🌍 Project: OFFLINE WORLD");
        tvStatus.setText(sb.toString());
    }
}
