package com.met.getticket;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final static String GET_TICKET_URL = "http://54.171.171.134:5000/user";

    private final String[] comboOptions = new String[]{"1", "2", "3", "4"};
    private User user;
    private LocationManager locationManager;
    private InitialETACalculator initialListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User();
        Random random = new Random();
        user.setUid(random.nextInt(Integer.MAX_VALUE) + "");
        user.setAge((random.nextInt(65)+20) + "");

        final Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, comboOptions);
        spinner.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        initialListener = new InitialETACalculator(this, user);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100,
                0, initialListener );


        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (user.getEta1() == null || user.getEta2() == null) {
                    return;
                }
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_TICKET_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("sube", response);
                                user.setSube(response);

                                locationManager.removeUpdates(initialListener);
                                locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER, 100,
                                        0, new ETAUpdater(MainActivity.this, user));


                                Intent intent = new Intent(MainActivity.this, LineActivity.class);
                                startActivity(intent);
                            }
                        },
                        null) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<>();
                        map.put("uid", user.getUid());
                        map.put("eta1", user.getEta1());
                        map.put("eta2", user.getEta2());
                        map.put("operation", spinner.getSelectedItem().toString());
                        map.put("age", user.getAge());
                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }
}
