package com.freechat.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MessageQueue {

    private static final String PREFS = "message_queue";
    private static final String KEY = "pending_messages";
    private SharedPreferences prefs;

    public static class PendingMessage {
        public String id;
        public String to;
        public String text;
        public long timestamp;
        public int ttl;
        public String route;

        public PendingMessage(String to, String text) {
            this.id = java.util.UUID.randomUUID().toString();
            this.to = to;
            this.text = text;
            this.timestamp = System.currentTimeMillis();
            this.ttl = 7;
            this.route = "pending";
        }
    }

    public MessageQueue(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void addMessage(PendingMessage msg) {
        try {
            JSONArray array = getArray();
            JSONObject obj = new JSONObject();
            obj.put("id", msg.id);
            obj.put("to", msg.to);
            obj.put("text", msg.text);
            obj.put("timestamp", msg.timestamp);
            obj.put("ttl", msg.ttl);
            obj.put("route", msg.route);
            array.put(obj);
            prefs.edit().putString(KEY, array.toString()).apply();
        } catch (Exception e) {}
    }

    public List<PendingMessage> getPendingMessages() {
        List<PendingMessage> list = new ArrayList<>();
        try {
            JSONArray array = getArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                PendingMessage msg = new PendingMessage(
                    obj.getString("to"),
                    obj.getString("text")
                );
                msg.id = obj.getString("id");
                msg.ttl = obj.getInt("ttl");
                msg.route = obj.getString("route");
                list.add(msg);
            }
        } catch (Exception e) {}
        return list;
    }

    public void removeMessage(String id) {
        try {
            JSONArray array = getArray();
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.getString("id").equals(id)) {
                    newArray.put(obj);
                }
            }
            prefs.edit().putString(KEY, newArray.toString()).apply();
        } catch (Exception e) {}
    }

    public int getPendingCount() {
        return getPendingMessages().size();
    }

    private JSONArray getArray() {
        try {
            String json = prefs.getString(KEY, "[]");
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
