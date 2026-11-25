package com.freechat.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    private static final int PERM_REQUEST = 100;
    private BleManager bleManager;
    private WiFiDirectManager wifiDirectManager;
    private ArrayList<String> deviceList = new ArrayList<>();
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        SharedPreferences prefs = getSharedPreferences("freechat", MODE_PRIVATE);
        String name = prefs.getString("user_name", "User");
        String uuid = prefs.getString("user_uuid", "0000");

        TextView tvUser = findViewById(R.id.tvUser);
        if (tvUser != null) tvUser.setText(name + " #" + uuid);

        ListView lvDevices = findViewById(R.id.lvDevices);
        adapter = new ChatAdapter(this, deviceList);
        if (lvDevices != null) {
            lvDevices.setAdapter(adapter);
            lvDevices.setOnItemClickListener((parent, view, position, id) -> {
                String item = deviceList.get(position);
                String[] parts = item.split("\n");
                String devName = parts.length > 0 ? parts[0] : "Unknown";
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("name", devName);
                startActivity(intent);
            });
        }

        LinearLayout tabStatus = findViewById(R.id.tabStatus);
        if (tabStatus != null) {
            tabStatus.setOnClickListener(v -> {
                startActivity(new Intent(this, RouteStatusActivity.class));
            });
        }

        bleManager = new BleManager(this, (devName, address) -> {
            runOnUiThread(() -> {
                String entry = "🔵 " + devName + "\n" + address;
                if (!deviceList.contains(entry)) {
                    deviceList.add(entry);
                    adapter.notifyDataSetChanged();
                }
            });
        });

        wifiDirectManager = new WiFiDirectManager(this, (devName, address) -> {
            runOnUiThread(() -> {
                String entry = devName + "\n" + address;
                if (!deviceList.contains(entry)) {
                    deviceList.add(entry);
                    adapter.notifyDataSetChanged();
                }
            });
        });

        requestPermissionsAndScan();
    }

    private void requestPermissionsAndScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.NEARBY_WIFI_DEVICES
            }, PERM_REQUEST);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
            }, PERM_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST) {
            bleManager.startScan();
            wifiDirectManager.startDiscovery();
            Toast.makeText(this, "🔍 ढूँढ रहा हूँ...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bleManager != null) bleManager.stopScan();
        if (wifiDirectManager != null) wifiDirectManager.stop();
    }
}
