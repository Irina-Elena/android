package com.example.settingsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    EditText input;
    Button  loginButton;
    private static final String FILE_NAME = "input_name.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this,R.xml.root_preferences,false);

        textView = findViewById(R.id.hello);
        textView.setTextColor(Color.MAGENTA);
        input = findViewById(R.id.input);
        loginButton = findViewById(R.id.button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switch_pref = sharedPref.getBoolean("theme",false);
//        View view = this.getWindow().getDecorView();
        if (switch_pref) {
            textView.setTextColor(Color.BLACK);
//            input.setTextColor(Color.WHITE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                settings();
                return true;
            case R.id.quit:
                quit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void login(View v) {
        String text = input.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            input.getText().clear();

            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
    }

    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void quit() {
        finish();
        moveTaskToBack(true);
    }

}
