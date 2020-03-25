package com.example.settingsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppActivity extends AppCompatActivity {
    private static final String FILE_NAME = "input_name.txt";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        textView = findViewById(R.id.name);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switch_pref = sharedPref.getBoolean("theme",false);
//        View view = this.getWindow().getDecorView();
        if (switch_pref) {
            textView.setTextColor(Color.BLACK);
        } else {
            textView.setTextColor(Color.MAGENTA);
        }

        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder("Welcome ");
            String text;

            if ((text = br.readLine()) != null) {
                sb.append(text);
            }
            sb.append("!");

            textView.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
