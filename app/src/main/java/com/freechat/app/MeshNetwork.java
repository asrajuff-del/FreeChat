package com.freechat.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.Set;

public class MeshNetwork {

    private static final int DEFAULT_TTL = 7;
    private Context context;
    private SharedPreferences prefs;
    private Set<String> seenMessages = new HashSet<>();
    private OnMessageReceived listener;

    public interface OnMessageReceived {
        void onReceived(MeshMessage msg);
    }

    public static class MeshMessage {
        public String id;
        public String from;
        public String to;
        public String text;
        public int ttl;
        public long timestamp;
        public String route;

        public MeshMessage(String from, String to, String text) {
            this.id = java.util.UUID.randomUUID().toString();
            this.from = from;
            this.to = to;
            this.text = text;
            this.ttl = DEFAULT_TTL;
            this.timestamp = System.currentTimeMillis();
            this.route = "mesh";
        }

        public String toJson() {
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", id);
                obj.put("from", from);
                obj.put("to", to);
                obj.put("text", text);
                obj.put("ttl", ttl);
                obj.put("timestamp", timestamp);
                obj.put("route", route);
                return obj.toString();
            } catch (Exception e) { return "{}"; }
        }

        public static MeshMessage fromJson(String json) {
            try {
                JSONObject obj = new JSONObject(json);
                MeshMessage msg = new MeshMessage(
                    obj.getString("from"),
                    obj.getString("to"),
                    obj.getString("text")
                );
                msg.id = obj.getString("id");
                msg.ttl = obj.getInt("ttl");
                msg.timestamp = obj.getLong("timestamp");
                msg.route = obj.getString("route");
                return msg;
            } catch (Exception e) { return null; }
        }
    }

    public MeshNetwork(Context context, OnMessageReceived listener) {
        this.context = context;
        this.listener = listener;
        this.prefs = context.getSharedPreferences("mesh", Context.MODE_PRIVATE);
    }

    public boolean shouldRelay(MeshMessage msg) {
        if (seenMessages.contains(msg.id)) return false;
        if (msg.ttl <= 0) return false;
        seenMessages.add(msg.id);
        return true;
    }

    public MeshMessage decreaseTTL(MeshMessage msg) {
        msg.ttl--;
        return msg;
    }

    public boolean isForMe(MeshMessage msg, String myId) {
        return msg.to.equals(myId) || msg.to.equals("ALL");
    }

    public void broadcast(MeshMessage msg, String myId) {
        if (isForMe(msg, myId)) {
            listener.onReceived(msg);
        } else if (shouldRelay(msg)) {
            relayMessage(decreaseTTL(msg));
        }
    }

    private void relayMessage(MeshMessage msg) {
        try {
            JSONArray pending = getPendingRelay();
            pending.put(new JSONObject(msg.toJson()));
            prefs.edit().putString("relay_queue", pending.toString()).apply();
        } catch (Exception e) {}
    }

    private JSONArray getPendingRelay() {
        try {
            return new JSONArray(prefs.getString("relay_queue", "[]"));
        } catch (Exception e) { return new JSONArray(); }
    }

    public int getRelayQueueSize() {
        return getPendingRelay().length();
    }
}
