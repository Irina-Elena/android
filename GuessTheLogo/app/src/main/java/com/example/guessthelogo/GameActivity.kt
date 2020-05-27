package com.example.guessthelogo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import android.widget.LinearLayout

class GameActivity : AppCompatActivity() {
    val images = mutableListOf(R.drawable.blogger, R.drawable.deviantart, R.drawable.digg, R.drawable.dropbox, R.drawable.evernote,
        R.drawable.facebook, R.drawable.flickr, R.drawable.google, R.drawable.googleplus, R.drawable.hyves,
        R.drawable.linkedin, R.drawable.myspace, R.drawable.picasa, R.drawable.pinterest, R.drawable.reddit, R.drawable.rss, R.drawable.twitter,
        R.drawable.vimeo, R.drawable.wordpress, R.drawable.yahoo, R.drawable.youtube, R.drawable.stumbleupon, R.drawable.instagram,
        R.drawable.soundcloud)
    var index_images = 0
    var alphabet = ('a'..'z').toMutableList()
    var letter_buttons = ArrayList<Button>()
    private lateinit var logoPicture: ImageView
    private lateinit var layout: LinearLayout
    private lateinit var answer: EditText
    private lateinit var timerTextView: TextView
    private lateinit var timer: CountDownTimer
    private lateinit var nextButton: Button
    private lateinit var refreshButton: Button
    private var name: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game2)
        name = intent.getStringExtra("name")
        logoPicture = findViewById(R.id.logoPicture)
        layout = findViewById(R.id.linear_layout)
        answer = findViewById(R.id.editText)
        nextButton = findViewById(R.id.nextButton)
        timerTextView = findViewById(R.id.timer)
        refreshButton = findViewById(R.id.refresh_button)

        images.shuffle()
        setImages()
        setView()
        setTimer()
        setRefreshButton()
        setNextButton()
    }

    fun setRefreshButton() {
        // fac butoanele apasate vizibile din nou si sterg ceea ce a scris jucatorul
        refreshButton.setOnClickListener() {
            for(button in letter_buttons) {
                answer.text.clear()
                button.visibility = View.VISIBLE
            }
        }
    }

    fun setTimer() {
        timer = object: CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.setText("Seconds remaining: " + millisUntilFinished/1000)
            }

            override fun onFinish() {
                endGame()
            }
        }
        timer.start()
    }

    fun setNextButton() {
        // verific daca numele logourilor corespund, altfel afisez un mesaj
        nextButton.setOnClickListener() {
            var word = getResources().getResourceName(images[index_images])
            word = word.substring(word.lastIndexOf("/")+1)

            if(answer.text.toString().equals(word)) {
                index_images += 1
                if (index_images == images.count()) {
                    endGame()
                } else {
                    answer.text.clear()
                    layout.removeAllViews()
                    setImages()
                    setView()
                    timer.start()
                }
            } else {
                Toast.makeText(this, "Wrong logo name! Try again!",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setImages() {
        logoPicture.setImageResource(images[index_images])
        logoPicture.getLayoutParams().height = 400
        logoPicture.getLayoutParams().width = 400
    }

    fun setView() {
        alphabet.shuffle()
        //iau numele logoului din numele pozei si concatenez cu alte 5 litere din alfabet, apoi le amestec
        val word = getResources().getResourceName(images[index_images])
        val letters = word.substring(word.lastIndexOf("/")+1).toMutableList()
        answer.setHint(letters.count().toString() + " characters")
        letters.addAll(alphabet.take(5))
        letters.shuffle()

        //setes marimea butonului ce va contine o litera, apoi fac doua randuri unde vor fi butoanele cu litere din care va alege jucatorul
        val params = LinearLayout.LayoutParams(130, 130)
        var first= 0
        var last = letters.count()/2 - 1
        for (i in 0..1) {
            val row = LinearLayout(this)
            row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            if(i != 0) {
                first = last + 1
                last = letters.count()-1
            }
            for (j in first..last) {
                val button_letters = Button(this)
                button_letters.layoutParams = params
                button_letters.text = letters[j].toString()
                button_letters.setOnClickListener() {
                    //daca apas pe o litera atunci aceasta devine invizibila
                    answer.text.append(letters[j])
                    button_letters.visibility = View.INVISIBLE
                }
                letter_buttons.add(button_letters)
                row.addView(button_letters)
            }
            layout.addView(row)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
    }

    fun endGame() {
        // caut in sharedPrefrences highscorul jucatorului, iar in caz ca exista il modific daca este mai mare, si daca nu exista, atunci il setez pe acesta obtinut
        val preferences = getSharedPreferences("myData", Context.MODE_PRIVATE)
        val highscore = preferences.getInt(name, 0)
        if (highscore == 0) {
            preferences.edit().putInt(name, index_images).apply()
        } else if(highscore < index_images) {
            preferences.edit().putInt(name, index_images).apply()
        }
        timer.cancel()
        val end_game = Intent(this, EndGame::class.java)
        end_game.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        end_game.putExtra("score", "Your score is " + index_images)
        end_game.putExtra("name", name)
        startActivity(end_game)
    }
}
