package com.example.guessthelogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameEditText = findViewById<EditText>(R.id.name)
        val playButton = findViewById<Button>(R.id.playButton)
        playButton.setOnClickListener() {
            val name = nameEditText.text.toString()
            if (name.equals("Enter your name")) {
                Toast.makeText(this,"Please enter a name!", Toast.LENGTH_LONG).show()
            } else {
                if (name.length > 0) {
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.putExtra("name", name)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Please enter a name!", Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}
