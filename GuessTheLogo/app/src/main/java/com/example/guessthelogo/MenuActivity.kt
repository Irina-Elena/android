package com.example.guessthelogo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val name = intent.getStringExtra("name")
        val highScoreButton = findViewById<Button>(R.id.highscore)

        val preferences = getSharedPreferences("myData", Context.MODE_PRIVATE)
        val highscore = preferences.getInt(name, 0)

        highScoreButton.setOnClickListener {
            val intent = Intent(this, Highscores::class.java)
            startActivity(intent)
//            if (highScoreButton.text.equals("highscore")) {
//                highScoreButton.setText(name + ": " + highscore.toString())
//            } else highScoreButton.setText("highscore")
        }
        val newGameButton = findViewById<Button>(R.id.new_game)
        newGameButton.setOnClickListener() {
            val intent = Intent(this, GameActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("name",name)
            startActivity(intent)
        }

        val shareScore = findViewById<Button>(R.id.share)
        shareScore.setOnClickListener {
            val message = "Hey! This is my highscore at GuessTheLogo game: " + highscore + "!"
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,message)
            intent.type = "text/plain"

            startActivity(Intent.createChooser(intent, "Share to:"))
        }
    }
}
