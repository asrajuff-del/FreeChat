package com.freechat.app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PremiumActivity extends AppCompatActivity {

    private static final int GOAL = 70000;
    private static final int CURRENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        TextView tvProgress = findViewById(R.id.tvProgress);
        TextView tvPercent = findViewById(R.id.tvPercent);
        TextView tvRoutes = findViewById(R.id.tvRoutes);

        tvProgress.setText(CURRENT + " / " + GOAL + " लोग जुड़े");

        int percent = (CURRENT * 100) / GOAL;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            bar.append(i < (percent / 5) ? "█" : "░");
        }
        tvPercent.setText(bar + " " + percent + "%");

        StringBuilder routes = new StringBuilder();
        routes.append("🔒 Li-Fi (LED बल्ब से message)\n");
        routes.append("🔒 PLC (बिजली तार से message)\n");
        routes.append("🔒 Satellite SMS\n");
        routes.append("🔒 LoRa Radio (100km range)\n");
        routes.append("🔒 5G Sidelink (D2D)\n");
        routes.append("🔒 Drone Relay\n");
        routes.append("🔒 Ham Radio Network\n");
        routes.append("🔒 Ultrasonic Advanced\n");
        routes.append("🔒 Magnetic Field\n");
        routes.append("🔒 Laser Communication\n");
        routes.append("🔒 ... और 60 रास्ते\n");
        tvRoutes.setText(routes.toString());
    }
}
