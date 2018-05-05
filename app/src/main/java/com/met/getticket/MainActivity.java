package com.met.getticket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private final static String GET_LINE_URL = "http://192.168.51.59/getLine";
    private final static String GET_LINE_INFO_URL = "http://192.168.51.59/getLineInfo";
    private String[] comboOptions;

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
                new Thread(new Worker(spinner.getSelectedItem().toString())).start();
            }
        });
    }
}
