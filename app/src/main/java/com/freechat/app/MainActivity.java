package com.freechat.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private MaterialButton btnStart;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("freechat", MODE_PRIVATE);
        String savedName = prefs.getString("user_name", null);

        if (savedName != null) {
            goToChat();
            return;
        }

        setContentView(R.layout.activity_main);
        etName = findViewById(R.id.etName);
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "नाम डालो भाई!", Toast.LENGTH_SHORT).show();
                return;
            }
            String uuid = java.util.UUID.randomUUID().toString().substring(0, 4);
            prefs.edit()
                .putString("user_name", name)
                .putString("user_uuid", uuid)
                .apply();
            goToChat();
        });
    }

    private void goToChat() {
        startActivity(new Intent(this, ChatListActivity.class));
        finish();
    }
}
