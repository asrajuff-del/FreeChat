package com.freechat.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<String> {

    public ChatAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_chat, parent, false);
        }

        String item = getItem(position);
        String[] parts = item.split("\n");
        String name = parts.length > 0 ? parts[0] : "Unknown";
        String address = parts.length > 1 ? parts[1] : "";

        TextView tvAvatar = convertView.findViewById(R.id.tvAvatar);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvLastMsg = convertView.findViewById(R.id.tvLastMsg);
        TextView tvTime = convertView.findViewById(R.id.tvTime);

        tvAvatar.setText(name.substring(0, 1).toUpperCase());
        tvName.setText(name);
        tvLastMsg.setText("📡 BLE — " + address);
        tvTime.setText("अभी");

        return convertView;
    }
}
