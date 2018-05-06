package com.met.getticket;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.InputStream;
import java.util.Scanner;
import java.util.StringTokenizer;

public class InitialETACalculator implements LocationListener {
    private final static String GET_ETA =
            "https://maps.googleapis.com/maps/api/directions/json?origin=%1$s&destination=%2$s"
                    + "&mode=%3$s";
    private final static String SUBE_1_LOCATION = "41.085342,29.009800";
    private final static String SUBE_2_LOCATION = "41.079708,29.007711";
    private final Context mContext;
    private final User mUser;

    public InitialETACalculator(Context context, User user) {
        mContext = context;
        mUser = user;
    }

    @Override
    public void onLocationChanged(Location location) {
        findAndSetEta(location, SUBE_1_LOCATION, mUser);
        findAndSetEta(location, SUBE_2_LOCATION, mUser);
    }

    private void findAndSetEta(Location origin, final String url, final User user) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String URI = String.format(GET_ETA, origin.getLatitude() + "," + origin.getLongitude(),
                url, "walking");

        StringRequest myReq = new StringRequest(Request.Method.GET,
                URI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*InputStream inputStream = mContext.getResources().openRawResource(
                                R.raw.json);
                        String content = new Scanner(inputStream).useDelimiter(
                                "\\Z").next();*/
                        try {
                            String duration = JsonPath.read(response,
                                    "$.routes[0].legs[0].duration.text");
                            StringTokenizer stringTokenizer = new StringTokenizer(duration, " ");
                            if(url.equals(SUBE_1_LOCATION)) {
                                user.setEta1(stringTokenizer.nextToken());
                            } else {
                                user.setEta2(stringTokenizer.nextToken());
                            }
                        } catch (PathNotFoundException ex) {
                        }
                    }
                }, null);
        requestQueue.add(myReq);
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
}
