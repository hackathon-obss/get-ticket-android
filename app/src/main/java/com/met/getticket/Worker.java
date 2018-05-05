package com.met.getticket;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Worker implements Runnable {
    private final String mType;

    public Worker(String type) {
        mType = type;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection conn;

        try {
            url = new URL("http://127.0.0.1:5000/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
        } catch (Exception e) {
            Log.i("ERROR", "Error1");
            return;
        }
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        Map<String, String> postData = new HashMap<>();
        Random rng = new Random();
        postData.put("id", rng.nextInt(1000) + "");
        postData.put("eta1", rng.nextInt(15) + "");
        postData.put("eta2", rng.nextInt(15) + "");
        postData.put("type", (mType));

        JSONObject jsonObj = new JSONObject(postData);

        Log.i("JSON", jsonObj.toString());

        try {

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonObj.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());
        } catch (Exception e) {
            Log.i("ERROR", "Error");
            e.printStackTrace();
        }

        conn.disconnect();
    }
}
