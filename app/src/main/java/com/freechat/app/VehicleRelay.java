package com.freechat.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public class VehicleRelay {

    private static final String PREFS = "vehicle_relay";
    private SharedPreferences prefs;
    private Context context;

    public static class RelayMessage {
        public String id;
        public String text;
        public String toUser;
        public double destLat;
        public double destLng;
        public int ttl;
        public long timestamp;

        public RelayMessage(String text, String toUser, double destLat, double destLng) {
            this.id = java.util.UUID.randomUUID().toString();
            this.text = text;
            this.toUser = toUser;
            this.destLat = destLat;
            this.destLng = destLng;
            this.ttl = 7;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public VehicleRelay(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isMovingTowardsDest(
        double myLat, double myLng,
        double destLat, double destLng,
        double vehicleLat, double vehicleLng,
        double vehicleHeading) {

        double angleToDestination = Math.toDegrees(
            Math.atan2(destLng - vehicleLng, destLat - vehicleLat)
        );
        double diff = Math.abs(vehicleHeading - angleToDestination);
        return diff < 45 || diff > 315;
    }

    public void storeForRelay(RelayMessage msg) {
        try {
            JSONArray array = getArray();
            JSONObject obj = new JSONObject();
            obj.put("id", msg.id);
            obj.put("text", msg.text);
            obj.put("toUser", msg.toUser);
            obj.put("destLat", msg.destLat);
            obj.put("destLng", msg.destLng);
            obj.put("ttl", msg.ttl);
            obj.put("timestamp", msg.timestamp);
            array.put(obj);
            prefs.edit().putString("relay_msgs", array.toString()).apply();
        } catch (Exception e) {}
    }

    public void decreaseTTL(String id) {
        try {
            JSONArray array = getArray();
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("id").equals(id)) {
                    int ttl = obj.getInt("ttl") - 1;
                    if (ttl > 0) {
                        obj.put("ttl", ttl);
                        newArray.put(obj);
                    }
                } else {
                    newArray.put(obj);
                }
            }
            prefs.edit().putString("relay_msgs", newArray.toString()).apply();
        } catch (Exception e) {}
    }

    private JSONArray getArray() {
        try {
            return new JSONArray(prefs.getString("relay_msgs", "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
