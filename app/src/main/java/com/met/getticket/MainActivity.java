package com.met.getticket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String[] comboOptions;
    private final static String GET_LINE_URL = "http://192.168.51.59/getLine";
    private final static String GET_LINE_INFO_URL = "http://192.168.51.59/getLineInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        comboOptions = new String[4];
        comboOptions[0] = "1";
        comboOptions[1] = "2";
        comboOptions[2] = "3";
        comboOptions[3] = "4";

        final Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, comboOptions);
        spinner.setAdapter(adapter);

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                URL url;
                HttpURLConnection conn = null;

                try {
                    url = new URL("http://127.0.0.1:5000/");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                }catch (Exception e){
                    Log.i("ERROR","Error1");
                }
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Map<String, String> postData = new HashMap<>();
                Random rng = new Random();
                postData.put("id", rng.nextInt(1000)+"");
                postData.put("eta1", rng.nextInt(15)+"");
                postData.put("eta2", rng.nextInt(15)+"");
                postData.put("type", (spinner.getSelectedItem().toString()));

                JSONObject jsonObj = new JSONObject(postData);

                Log.i("JSON", jsonObj.toString());

                try {

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonObj.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                } catch (Exception e){
                    Log.i("ERROR","Error");
                    e.printStackTrace();
                }

                conn.disconnect();
            }
        });
    }
}
