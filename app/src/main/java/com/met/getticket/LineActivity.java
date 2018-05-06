package com.met.getticket;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LineActivity extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        user = (User) getIntent().getSerializableExtra("user");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        ETAUpdater updateListener = new ETAUpdater(LineActivity.this, user);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 100,
                0, updateListener);

        final TextView line = findViewById(R.id.textView);
        line.setText(user.getLine());

        final TextView age = findViewById(R.id.textView5);
        age.setText(user.getAge());

        final TextView subeNo = findViewById(R.id.textView7);
        subeNo.setText(user.getSube());
    }
}
