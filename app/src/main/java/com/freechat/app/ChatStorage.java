package com.freechat.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ChatStorage {

    private SharedPreferences prefs;
    private static final String KEY_PREFIX = "chat_";

    public ChatStorage(Context context) {
        prefs = context.getSharedPreferences("freechat_messages", Context.MODE_PRIVATE);
    }

    public void saveMessage(String chatId, String text, boolean isSent, String time) {
        try {
            JSONArray array = getMessages(chatId);
            JSONObject obj = new JSONObject();
            obj.put("text", text);
            obj.put("isSent", isSent);
            obj.put("time", time);
            array.put(obj);
            prefs.edit().putString(KEY_PREFIX + chatId, array.toString()).apply();
        } catch (Exception e) {}
    }

    public JSONArray getMessages(String chatId) {
        try {
            String json = prefs.getString(KEY_PREFIX + chatId, "[]");
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public List<MessageAdapter.Message> loadMessages(String chatId) {
        List<MessageAdapter.Message> list = new ArrayList<>();
        try {
            JSONArray array = getMessages(chatId);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new MessageAdapter.Message(
                    obj.getString("text"),
                    obj.getBoolean("isSent"),
                    obj.getString("time")
                ));
            }
        } catch (Exception e) {}
        return list;
    }
}
