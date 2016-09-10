package com.example.gg_zapr.humanatm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GiverDetailActivity extends AppCompatActivity {

    private String googleMapsLink = "https://www.google.co.in/maps/@%f,%fz";
    private TextView mMapTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giver_detail);

        Giver giver = (Giver) getIntent().getSerializableExtra("giver");
        mMapTextView = (TextView) findViewById(R.id.mapLink);
        mMapTextView.setText(String.format(googleMapsLink, giver.lat, giver.lon));
    }
}
