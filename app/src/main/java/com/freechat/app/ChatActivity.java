package com.freechat.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private TextView btnSend;
    private RecyclerView rvMessages;
    private List<MessageAdapter.Message> messages;
    private MessageAdapter adapter;
    private ChatStorage storage;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String name = getIntent().getStringExtra("name");
        if (name == null) name = "Unknown";
        chatId = name.replaceAll("[^a-zA-Z0-9]", "_");

        storage = new ChatStorage(this);
        messages = storage.loadMessages(chatId);

        if (messages.isEmpty()) {
            messages.add(new MessageAdapter.Message("नमस्ते! 👋", false, "1:00 pm"));
            messages.add(new MessageAdapter.Message("FreeChat से message आया!", false, "1:01 pm"));
            storage.saveMessage(chatId, "नमस्ते! 👋", false, "1:00 pm");
            storage.saveMessage(chatId, "FreeChat से message आया!", false, "1:01 pm");
        }

        TextView tvBack = findViewById(R.id.tvBack);
        TextView tvAvatar = findViewById(R.id.tvAvatar);
        TextView tvName = findViewById(R.id.tvName);
        tvAvatar.setText(name.substring(0, 1).toUpperCase());
        tvName.setText(name);
        tvBack.setOnClickListener(v -> finish());

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        rvMessages = findViewById(R.id.rvMessages);

        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);
        rvMessages.scrollToPosition(messages.size() - 1);

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
                messages.add(new MessageAdapter.Message(msg, true, time));
                storage.saveMessage(chatId, msg, true, time);
                adapter.notifyItemInserted(messages.size() - 1);
                rvMessages.scrollToPosition(messages.size() - 1);
                etMessage.setText("");
            }
        });
    }
}
