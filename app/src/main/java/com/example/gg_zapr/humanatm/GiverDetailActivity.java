package com.example.gg_zapr.humanatm;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GiverDetailActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private String googleMapsLink = "https://www.google.co.in/maps/@%f,%fz";
    private TextView mMapTextView;
    private Giver giver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giver_detail);

         giver = (Giver) getIntent().getSerializableExtra("giver");
        mMapTextView = (TextView) findViewById(R.id.mapLink);
        mMapTextView.setText(String.format(googleMapsLink, giver.lat, giver.lon));
        Button button = (Button) findViewById(R.id.send);
        button.setOnClickListener(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            float amount = getIntent().getFloatExtra("amount", 0);
            Intent intent = new Intent(this, PaymentMainActivity.class);
            intent.putExtra("amount", amount);
            intent.putExtra("giverId",giver.id);
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        LatLng sydney = new LatLng(giver.lat, giver.lon);
       LatLng sydney = new LatLng(12.971599,77.594563);
//        LatLng sydney = new LatLng(-33.867, 151.206);

//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        googleMap.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
                .position(sydney));
;
//
    }
}
