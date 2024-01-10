package com.example.hotpot

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


// User profile activity
class UserProfileActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize UI elements
        usernameTextView = findViewById(R.id.usernameTextView)
        bioTextView = findViewById(R.id.bioTextView)
        profileImageView = findViewById(R.id.profileImageView)

        // Retrieve user data from intent
        val userId = intent.getStringExtra("userId").toString()

        if (userId == null) {   // If no user ID is provided, exit the activity
            finish()
        } else {
            loadUserData(userId)
        }
    }


// Load user data from Firebase
private fun loadUserData(userId: String) {

    val userData = FirebaseDatabase.getInstance().getReference("Users").child(userId)
    userData.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val userProfile = snapshot.getValue(UserProfile::class.java)
            if (userProfile != null) {
                usernameTextView.text = userProfile.name
                bioTextView.text = userProfile.bio
                // Load profile picture using Glide inside the Firebase Storage reference
                Glide.with(this@UserProfileActivity)
                    .load(FirebaseStorage.getInstance().getReference("profilePictures").child(userId))
                    .into(profileImageView)

            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle database error

        }
    })
}
}
