package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    Switch s;
    private String sharedPrefFile = "com.example.myapplication.sharedprefs";
//    private SharedPreferences sharedPref;
    boolean switchIsChecked;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        s = findViewById(R.id.theme_switch);
        textview = findViewById(R.id.theme);
        switchIsChecked = getIntent().getBooleanExtra("switch_value", true);        //iau valoarea switch-ului transmisa de main activity

        final ViewGroup view = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0); //schimb curlorile de fundal
        if (switchIsChecked) {
            s.setChecked(true);
            view.setBackgroundColor(Color.BLACK);
            textview.setTextColor(Color.WHITE);

        } else {
            s.setChecked(false);
            view.setBackgroundColor(Color.parseColor("#fff5e6"));
            textview.setTextColor(Color.BLACK);
        }

        // daca vreau sa schimb valoarea switch-ului, culorile se modifica la fiecare schimbare si schimb si valoarea din shared preferences
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final ViewGroup view = (ViewGroup) ((ViewGroup) SettingsActivity.this.findViewById(android.R.id.content)).getChildAt(0);
                if (isChecked) {
                    s.setChecked(true);
                    view.setBackgroundColor(Color.BLACK);
                    textview.setTextColor(Color.WHITE);

                } else {
                    s.setChecked(false);
                    view.setBackgroundColor(Color.parseColor("#fff5e6"));
                    textview.setTextColor(Color.BLACK);
                }
                SharedPreferences settings = SettingsActivity.this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = settings.edit();
                e.putBoolean("switch", isChecked);
                e.apply();
                e.commit();
                switchIsChecked = isChecked;
            }
        });
    }
}
