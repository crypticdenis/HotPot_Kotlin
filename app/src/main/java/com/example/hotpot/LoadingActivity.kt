package com.example.hotpot

import android.content.Context
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class LoadingActivity : AppCompatActivity() {
    private lateinit var loadingTextView: TextView
    private val words = "Let's get you started".split(" ")
    private var currentWordIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        loadingTextView = findViewById(R.id.loadingTextView)
        updateText()
    }

    private fun updateText() {
        if (currentWordIndex < words.size) {
            loadingTextView.text = words[currentWordIndex]
            loadingTextView.alpha = 0f // Set text to fully transparent
            animateTextOpacity()

            currentWordIndex++
            handler.postDelayed({
                updateText()
            }, 1500) // Delay between words
        } else {
            val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("loadingComplete", true).apply()

            finish()// Finish the activity when done
        }
    }

    private fun animateTextOpacity() {
        val opacityAnimation = ObjectAnimator.ofFloat(loadingTextView, "alpha", 0f, 1f)
        opacityAnimation.duration = 1000 // Duration of the opacity animation
        opacityAnimation.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
