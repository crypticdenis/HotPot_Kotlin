package com.example.hotpot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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
        val signInBtn = view.findViewById<Button>(R.id.signIn_bottom_Btn)

        signUpTab.setOnClickListener {
            // Navigate to the SignUp fragment
            (activity as? LoginActivity)?.showSignUpFragment()
        }

        signInBtn.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            // Aufruf der uploadNewRecipe Funktion
            uploadNewRecipe("Ingredients", "Spicy Miso Ramen", "Rating", "Steps")

            if (isEmailValid(email) && password.isNotBlank()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            val errorMessage = getFriendlyErrorMessage(task.exception)
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter a valid email and password.", Toast.LENGTH_SHORT).show()
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

// Verschiebe die Funktion nach außen
private fun uploadNewRecipe(ingredients: String, spicyMisoRamen: String, rating: String, steps: String) {
    val databaseReference = FirebaseDatabase.getInstance().reference.child("Recipes")

    databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var newRecipeId = 0

            if (snapshot.exists()) {
                val lastRecipe = snapshot.children.first()
                newRecipeId = lastRecipe.key?.toInt() ?: 0
                newRecipeId++
            }

            val newRecipeReference = databaseReference.child(newRecipeId.toString())
            newRecipeReference.child("Ingredients").setValue(ingredients)
            newRecipeReference.child("Spicy Miso Ramen").setValue(spicyMisoRamen)
            newRecipeReference.child("Rating").setValue(rating)
            newRecipeReference.child("Steps").setValue(steps)

            Log.d("Firebase", "Neues Rezept hochgeladen mit ID: $newRecipeId")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Fehler beim Hochladen des Rezepts: ${error.message}")
        }
    })
}

private fun getFriendlyErrorMessage(exception: Exception?): String {
    return when (exception) {
        is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. Please try again."
        is FirebaseAuthInvalidUserException -> "No account found with this email. Please sign up."
        is FirebaseAuthUserCollisionException -> "An account already exists with this email."
        is FirebaseAuthWeakPasswordException -> "Password is too weak. Please use a stronger password."
        // Add more specific cases as needed
        else -> "An error occurred. Please try again."
    }
}
