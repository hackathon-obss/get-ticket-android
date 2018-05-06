package com.met.getticket;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LineActivity extends AppCompatActivity {

    User user;
    private final static String DELETE_USER_URL = "http://54.171.171.134:5000/userx";
    private ETAUpdater updateListener;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        user = (User) getIntent().getSerializableExtra("user");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        updateListener = new ETAUpdater(LineActivity.this, user);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 100,
                0, updateListener);

        final TextView line = findViewById(R.id.textView);
        line.setText(user.getLine());

        final TextView age = findViewById(R.id.textView5);
        age.setText(user.getAge());

        final TextView subeNo = findViewById(R.id.textView7);
        subeNo.setText(user.getSube());

        final Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(LineActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_USER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                locationManager.removeUpdates(updateListener);
                                finish();
                            }
                        },
                        null) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<>();
                        map.put("uid", user.getUid());
                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }
}
