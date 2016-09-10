package com.example.gg_zapr.humanatm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class GiverDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String googleMapsLink = "https://www.google.co.in/maps/@%f,%fz";
    private TextView mMapTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giver_detail);

        Giver giver = (Giver) getIntent().getSerializableExtra("giver");
        mMapTextView = (TextView) findViewById(R.id.mapLink);
        mMapTextView.setText(String.format(googleMapsLink, giver.lat, giver.lon));
        Button button = (Button) findViewById(R.id.send);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send){
            float amount = getIntent().getFloatExtra("amount",0);
            Intent intent = new Intent(this,PaymentMainActivity.class);
            intent.putExtra("amount", amount);
            startActivity(intent);
        }
    }
}
