package com.example.guessthelogo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class EndGame : AppCompatActivity() {

    private lateinit var score: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_game)

        val name = intent.getStringExtra("name")
        score = findViewById(R.id.score)
        score.setText(intent.getStringExtra("score"))

        val playAgainButton = findViewById<Button>(R.id.play_again)
        playAgainButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("name",name)
            startActivity(intent)
        }

        val menuButton = findViewById<Button>(R.id.menu)
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }
}
