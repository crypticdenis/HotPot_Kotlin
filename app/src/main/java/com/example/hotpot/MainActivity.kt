package com.example.hotpot

import android.os.Bundle
import android.widget.Button
import android.view.View
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotpot.ui.theme.HotPotTheme

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_screen);

        val signInBtn: View = findViewById<Button>(R.id.signInBtn);
        val signUpBtn: View = findViewById(R.id.signUpBtn);

        signInBtn.setOnClickListener {
            startLoginActivity(LoginActivity.LOGIN_SIGN_IN);
        }

        signUpBtn.setOnClickListener {
            startLoginActivity(LoginActivity.LOGIN_SIGN_UP);
        }
    }

    private fun startLoginActivity(fragmentType: String) {
        val intent = Intent(this, LoginActivity::class.java);
        intent.putExtra(LoginActivity.LOGIN_TYPE, fragmentType)
        startActivity(intent);
    }
}
