package com.freechat.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ChatListActivity extends AppCompatActivity {

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
    }
}
