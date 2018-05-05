package com.met.getticket;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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
    private static final int SUBE1 = 1;
    private static final int SUBE2 = 2;
    private final static String GET_TICKET_URL = "http://54.171.171.134:5000/user";
    private final static String UPDATE_LOCATION_URL = "http://54.171.171.134:5000/eta";
    private final static String GET_ETA = "https://maps.googleapis.com/maps/api/directions/json?origin=%1$s&destination=%2$s&mode=%3$s";
    private final String[] comboOptions = new String[]{"1", "2", "3", "4"};
    private RequestQueue requestQueue;
    private volatile boolean clicked = false;
    private User user;
    private Random random = new Random();
    private String sube1 = "41.085342,29.009800";
    private String sube2 = "41.079708,29.007711";
    private Location lastLocation;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Log.d("Latitude", Double.toString(location.getLatitude()));
            Log.d("Longtitude", Double.toString(location.getLongitude()));

            lastLocation = location;

            if(clicked){
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, UPDATE_LOCATION_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response.toString());
                            }
                        },
                        null) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<>();
                        Random random = new Random();
                        map.put("uid", user.getUid());
                        map.put("eta", Integer.toString(random.nextInt(15)));

                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User();
        user.setUid(random.nextInt(Integer.MAX_VALUE)+"");

        final Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, comboOptions);
        spinner.setAdapter(adapter);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
                0, mLocationListener);

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clicked = true;
                requestQueue = Volley.newRequestQueue(MainActivity.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_TICKET_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response.toString());
                            }
                        },
                        null) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<>();
                        map.put("uid", user.getUid());
                        map.put("eta1", getETA(SUBE1));
                        map.put("eta2", getETA(SUBE2));
                        map.put("operation", spinner.getSelectedItem().toString());
                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }
    public String getETA(int sube){

        final boolean[] isOK = new boolean[1];
        isOK[0] = false;

        while(!isOK[0]){
            String URI = String.format(GET_ETA,
                    lastLocation.getLatitude()+","+lastLocation.getLongitude(),
                    sube == SUBE1 ? sube1 : sube2, "walking");

            StringRequest myReq = new StringRequest(Request.Method.GET,
                    URI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response.toString());

                            if(!response.contains("error_message")) isOK[0] = true;
                        }
                    },null);
            requestQueue.add(myReq);
        }
        return "";
    }
}
