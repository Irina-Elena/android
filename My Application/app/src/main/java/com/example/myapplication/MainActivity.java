package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView shopping_list_view;

    ArrayList<String> my_list = new ArrayList<>();

    static final String MY_LIST_KEY = "MY_LIST_KEY";
    private String sharedPrefFile = "com.example.myapplication.sharedprefs";
    private SharedPreferences sharedPref;
    boolean switch_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shopping_list_view = findViewById(R.id.shooping_list);
        sharedPref = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE);
        final boolean switch_value = sharedPref.getBoolean("switch",true);
        System.out.println(switch_value);

        if (savedInstanceState != null) {
            my_list = savedInstanceState.getStringArrayList(MY_LIST_KEY);
        } else {
            my_list = new ArrayList<>();
            my_list.add("Mar");my_list.add("Para");my_list.add("Ppepene");my_list.add("Banana");my_list.add("Ananas");my_list.add("Ppepene");my_list.add("Banana");my_list.add("Ananas");
            my_list.add("Ppepene");my_list.add("Banana");my_list.add("Ananas");my_list.add("Ppepene");my_list.add("Banana");my_list.add("Ananas");my_list.add("Ppepene");my_list.add("Banana");my_list.add("Ananas");
        }

        ArrayAdapter<String>  my_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,my_list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                if(switch_value) {
                    parent.setBackgroundColor(Color.BLACK);
                    tv.setTextColor(Color.WHITE);
                } else {
                    parent.setBackgroundColor(Color.parseColor("#fff5e6"));
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        shopping_list_view.setAdapter(my_adapter);
        shopping_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"You have cliked an item!",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        my_list = savedInstanceState.getStringArrayList(MY_LIST_KEY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(MY_LIST_KEY, (ArrayList<String>) my_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE);
        switch_value = sharedPref.getBoolean("switch",true);

        ArrayAdapter<String>  my_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,my_list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                if(switch_value) {
                    parent.setBackgroundColor(Color.BLACK);
                    tv.setTextColor(Color.WHITE);
                } else {
                    parent.setBackgroundColor(Color.parseColor("#fff5e6"));
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        shopping_list_view.setAdapter(my_adapter);
        System.out.println(switch_value);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                showSettings();
                return true;
            }
            case R.id.quit: {
                quit();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showSettings() {
        Context context = getApplicationContext();
        CharSequence message = "Opened settings!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context,message,duration);
        toast.show();

        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("switch_value",switch_value);
        startActivity(intent);
    }

    void quit() {
        finish();
        moveTaskToBack(true);
    }

    public void sendMessage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to send a message?")
                .setTitle("SMS");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("sms_body","I created an intent!!");
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
