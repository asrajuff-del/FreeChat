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
    private ArrayList<String> deviceList = new ArrayList<>();
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        try {
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
                    try {
                        String item = deviceList.get(position);
                        String[] parts = item.split("\n");
                        String devName = parts.length > 0 ? parts[0] : "Unknown";
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("name", devName);
                        startActivity(intent);
                    } catch (Exception e) {}
                });
            }

            LinearLayout tabStatus = findViewById(R.id.tabStatus);
            if (tabStatus != null) {
                tabStatus.setOnClickListener(v -> {
                    try { startActivity(new Intent(this, RouteStatusActivity.class)); }
                    catch (Exception e) {}
                });
            }

            LinearLayout tabGroups = findViewById(R.id.tabGroups);
            if (tabGroups != null) {
                tabGroups.setOnClickListener(v -> {
                    try { startActivity(new Intent(this, PremiumActivity.class)); }
                    catch (Exception e) {}
                });
            }

            deviceList.add("Demo User\n00:00:00:00:00:00");
            adapter.notifyDataSetChanged();

            bleManager = new BleManager(this, (devName, address) -> {
                runOnUiThread(() -> {
                    try {
                        String entry = "🔵 " + devName + "\n" + address;
                        if (!deviceList.contains(entry)) {
                            deviceList.add(entry);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {}
                });
            });

            requestPermissionsAndScan();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermissionsAndScan() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                }, PERM_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
                }, PERM_REQUEST);
            }
        } catch (Exception e) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == PERM_REQUEST && bleManager != null) {
                bleManager.startScan();
            }
        } catch (Exception e) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { if (bleManager != null) bleManager.stopScan(); }
        catch (Exception e) {}
    }
}
