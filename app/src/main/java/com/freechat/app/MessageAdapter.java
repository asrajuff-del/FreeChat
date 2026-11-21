package com.freechat.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    public static class Message {
        public String text;
        public boolean isSent;
        public String time;

        public Message(String text, boolean isSent, String time) {
            this.text = text;
            this.isSent = isSent;
            this.time = time;
        }
    }

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSent ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == TYPE_SENT ?
            R.layout.item_message_sent : R.layout.item_message_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.tvMessage.setText(msg.text);
        holder.tvTime.setText(msg.time);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        ViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }
}
