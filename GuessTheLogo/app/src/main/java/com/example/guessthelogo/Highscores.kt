package com.example.guessthelogo

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView


class Highscores : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscores)

        val preferences = getSharedPreferences("myData", Context.MODE_PRIVATE)
        val highscore = preferences.all.toList().sortedByDescending { (_, value) -> value as Int}.toMap()
        var length = highscore.count()
        if (length > 5) {
            length = 5
        }
        val lparams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val layout = findViewById<LinearLayout>(R.id.layout)
        for (i in 0..(length-1)) {
            val tv = TextView(this)
            tv.layoutParams = lparams
            tv.setText(highscore.keys.elementAt(i) + ": " + highscore.values.elementAt(i).toString())
            tv.setTextSize(1,24.0f)
            tv.setTextColor(Color.parseColor("#000000"))
            layout.addView(tv)
        }
    }
}
