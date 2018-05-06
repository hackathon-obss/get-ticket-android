package com.met.getticket;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ETAUpdater implements LocationListener {
    private final static String GET_ETA =
            "https://maps.googleapis.com/maps/api/directions/json?origin=%1$s&destination=%2$s"
                    + "&mode=%3$s";
    private final static String UPDATE_ETA_URL = "http://54.171.171.134:5000/eta";
    private final static String SUBE_1_LOCATION = "41.089183, 29.005068";
    private final static String SUBE_2_LOCATION = "41.083858, 29.004442";
    private final Context mContext;
    private final User mUser;

    public ETAUpdater(Context context, User user) {
        mContext = context;
        mUser = user;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mUser.getSube().equals("1")) {
            findAndUpdateEta(location, SUBE_1_LOCATION);
        } else {
            findAndUpdateEta(location, SUBE_2_LOCATION);
        }

        try{
            ((LineActivity) mContext).updateLocation(location.getLatitude()+","+location.getLongitude());
        }catch (Exception e){

        }

    }

    private void findAndUpdateEta(Location origin, final String url) {
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
                            updateEta(stringTokenizer.nextToken());
                        } catch (PathNotFoundException ex) {
                        }
                    }
                }, null);
        requestQueue.add(myReq);
    }

    private void updateEta(String duration) {
        if (mUser.getSube().equals("1")) {
            mUser.setEta1(duration);
        } else {
            mUser.setEta2(duration);
        }
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, UPDATE_ETA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("update_eta:", response);
                        try{
                            JSONObject jsonObj = new JSONObject(response);
                            ((LineActivity) mContext).updateLine(jsonObj.getString("lineNo"));
                        }catch(Exception e){

                        }
                    }
                },
                null) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("uid", mUser.getUid());
                if (mUser.getSube().equals("1")) {
                    map.put("eta", mUser.getEta1());
                } else {
                    map.put("eta", mUser.getEta1());
                }
                return map;
            }
        };
        requestQueue.add(stringRequest);
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
