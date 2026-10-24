package com.freechat.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private ImageButton btnSend;
    private RecyclerView rvMessages;
    private List<String> messages = new ArrayList<>();
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewByI
cat > app/src/main/res/layout/activity_chat.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/dark_bg"
    android:orientation="vertical">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="@color/dark_surface"
        android:titleTextColor="@color/white"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvMessages"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:padding="8dp"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/dark_surface"
        android:orientation="horizontal"
        android:padding="8dp"
        android:gravity="center_vertical">

        <EditText
            android:id="@+id/etMessage"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="@string/type_message"
            android:textColor="@color/white"
            android:textColorHint="@color/grey"
            android:background="@android:color/transparent"
            android:inputType="textMultiLine"
            android:maxLines="4"
            android:padding="8dp"/>

        <ImageButton
            android:id="@+id/btnSend"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@android:drawable/ic_menu_send"
            android:background="@color/green"
            android:tint="@color/white"
            android:padding="8dp"/>

    </LinearLayout>

</LinearLayout>
