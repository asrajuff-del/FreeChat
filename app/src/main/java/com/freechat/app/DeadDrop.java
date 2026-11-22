package com.freechat.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DeadDrop {

    private static final String PREFS = "dead_drops";
    private static final String KEY = "drops";
    private SharedPreferences prefs;

    public static class Drop {
        public String id;
        public String message;
        public double lat;
        public double lng;
        public boolean isPublic;
        public String fromUser;
        public long timestamp;

        public Drop(String message, double lat, double lng, boolean isPublic, String fromUser) {
            this.id = java.util.UUID.randomUUID().toString();
            this.message = message;
            this.lat = lat;
            this.lng = lng;
            this.isPublic = isPublic;
            this.fromUser = fromUser;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public DeadDrop(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void dropMessage(Drop drop) {
        try {
            JSONArray array = getArray();
            JSONObject obj = new JSONObject();
            obj.put("id", drop.id);
            obj.put("message", drop.message);
            obj.put("lat", drop.lat);
            obj.put("lng", drop.lng);
            obj.put("isPublic", drop.isPublic);
            obj.put("fromUser", drop.fromUser);
            obj.put("timestamp", drop.timestamp);
            array.put(obj);
            prefs.edit().putString(KEY, array.toString()).apply();
        } catch (Exception e) {}
    }

    public List<Drop> getNearbyDrops(double myLat, double myLng, double radiusKm) {
        List<Drop> nearby = new ArrayList<>();
        try {
            JSONArray array = getArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                double lat = obj.getDouble("lat");
                double lng = obj.getDouble("lng");
                double distance = getDistance(myLat, myLng, lat, lng);
                if (distance <= radiusKm) {
                    Drop drop = new Drop(
                        obj.getString("message"),
                        lat, lng,
                        obj.getBoolean("isPublic"),
                        obj.getString("fromUser")
                    );
                    drop.id = obj.getString("id");
                    nearby.add(drop);
                }
            }
        } catch (Exception e) {}
        return nearby;
    }

    private double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLng/2) * Math.sin(dLng/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    private JSONArray getArray() {
        try {
            return new JSONArray(prefs.getString(KEY, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
