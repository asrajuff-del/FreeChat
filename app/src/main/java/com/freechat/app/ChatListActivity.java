package com.freechat.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    private BleManager bleManager;
    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        SharedPreferences prefs = getSharedPreferences("freechat", MODE_PRIVATE);
        String name = prefs.getString("user_name", "User");
        String uuid = prefs.getString("user_uuid", "0000");

        TextView tvUser = findViewById(R.id.tvUser);
        if (tvUser != null) {
            tvUser.setText(name + " #" + uuid);
        }

        ListView lvDevices = findViewById(R.id.lvDevices);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        if (lvDevices != null) {
            lvDevices.setAdapter(adapter);
        }

        bleManager = new BleManager(this, (devName, address) -> {
            runOnUiThread(() -> {
                deviceList.add(devName + "\n" + address);
                adapter.notifyDataSetChanged();
            });
        });

        bleManager.startScan();
        Toast.makeText(this, "आस-पास ढूँढ रहा हूँ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bleManager != null) bleManager.stopScan();
    }
}
