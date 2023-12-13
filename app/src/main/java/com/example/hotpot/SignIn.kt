package com.example.hotpot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class SignIn : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Additional initialization code can go here if needed
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val signUpTab = view.findViewById<Button>(R.id.login_signUp_tab)
        val editEmail = view.findViewById<EditText>(R.id.signInEmailAddress)
        val editPassword = view.findViewById<EditText>(R.id.signInPassword)
        val signInBtn = view.findViewById<Button>(R.id.signIn_signIn_Btn)

        signUpTab.setOnClickListener {
            // Navigate to the SignUp fragment
            (activity as? LoginActivity)?.showSignUpFragment()
        }

        signInBtn.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            if (isEmailValid(email) && password.isNotBlank()) {
                // Use Firebase to sign in the user
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(requireContext(), "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Invalid input. Try again", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle touch outside of EditTexts to clear focus
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (editEmail.isFocused || editPassword.isFocused) {
                    val outRect = android.graphics.Rect()
                    editEmail.getGlobalVisibleRect(outRect)
                    editPassword.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        editEmail.clearFocus()
                        editPassword.clearFocus()
                    }
                }
            }
            false
        }

        return view
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
