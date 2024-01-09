package com.example.hotpot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


@Suppress("DEPRECATION")
class AccountActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val profilePicture = findViewById<ImageView>(R.id.profilePictureImageView)

        var auth = FirebaseAuth.getInstance()
        var database = FirebaseDatabase.getInstance()

        // get user data from database
        val user = auth.currentUser
        val uid = user?.uid

        val ref = database.getReference("Users/$uid")
        ref.get().addOnSuccessListener {
            val profilePictureURL = it.child("ProfilePictureURL").value.toString()
            val username = it.child("name").value.toString()

            it.children.forEach { child ->
                Log.d("AccountActivity", child.key.toString() + " " + child.value.toString())

            }

            // set username
            val usernameTextView = findViewById<TextView>(R.id.profileNameTextView)
            usernameTextView.text = username

            val storageRef = FirebaseStorage.getInstance().reference
            val fileRef = storageRef.child(profilePictureURL)
            Log.d("AccountActivity", fileRef.toString())
            Log.d("AccountActivity", fileRef.path)

            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()

                Glide.with(this).load(downloadUrl).into(profilePicture)
            }.addOnFailureListener {
                Toast.makeText(this, "Error while loading profile picture", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Error while loading user data", Toast.LENGTH_LONG).show()
        }


        profilePicture.setOnClickListener {
            // TODO: Open gallery and let user choose a picture

            // open file gallery and let user choose a picture
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)



        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_list -> {
                    val intent = Intent(this, ShoppingListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_favs -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }


    }
}