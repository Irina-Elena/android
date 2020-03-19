package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView shopping_list_view;
    TextView text_view;

    ArrayList<String> my_list = new ArrayList<>();

    static final String MY_LIST_KEY = "MY_LIST_KEY";
//    static final String TEXT_VIEW_KEY = "TEXT_VIEW_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shopping_list_view = (ListView) findViewById(R.id.shooping_list);
//        text_view = (TextView) findViewById(R.id.text1);

        if (savedInstanceState != null) {
            my_list = savedInstanceState.getStringArrayList(MY_LIST_KEY);
//            text_view.setText(savedInstanceState.getString(TEXT_VIEW_KEY));
        } else {
            my_list = new ArrayList<>();
            my_list.add("Mar");
            my_list.add("Para");
            my_list.add("Ppepene");
            my_list.add("Banana");
            my_list.add("Ananas");
            my_list.add("Ppepene");
            my_list.add("Banana");
            my_list.add("Ananas");
            my_list.add("Ppepene");
            my_list.add("Banana");
            my_list.add("Ananas");
            my_list.add("Ppepene");
            my_list.add("Banana");
            my_list.add("Ananas");
            my_list.add("Ppepene");
            my_list.add("Banana");
            my_list.add("Ananas");
        }


        ArrayAdapter<String>  my_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,my_list);
        shopping_list_view.setAdapter(my_adapter);

//        text_view.setText("This is my shopping list!");
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

//        text_view.setText(savedInstanceState.getString(TEXT_VIEW_KEY));

        my_list = savedInstanceState.getStringArrayList(MY_LIST_KEY);
//        System.out.println(text_view.getText());
        System.out.println(my_list);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(MY_LIST_KEY, (ArrayList<String>) my_list);
//        outState.putString(TEXT_VIEW_KEY, String.valueOf(text_view.getText()));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("ok");
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
            case R.id.help: {
                showHelp();
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

    void showHelp() {
        Context context = getApplicationContext();
        CharSequence message = "This is a shopping list with your products!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context,message,duration);
        toast.show();
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
