package com.example.hotpot

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

//TODO: add backbutton, add bottom nav bar
//TODO: add edit profile button (only if it's the user's own profile)
//TODO: add friend button (only if it's not the user's own profile)

class UserProfileActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var userRecipesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize UI elements
        usernameTextView = findViewById(R.id.usernameTextView)
        bioTextView = findViewById(R.id.bioTextView)
        profileImageView = findViewById(R.id.profileImageView)
        userRecipesRecyclerView = findViewById(R.id.userRecipesRecyclerView)

        // Retrieve user data from intent
        val userId = intent.getStringExtra("userId").toString()

        if (userId == null) {   // If no user ID is provided, exit the activity
            finish()
        } else {
            loadUserRecipes(userId)
            loadUserData(userId)
        }
    }

    private fun loadUserRecipes(userId: String) {
        val userRecipesReference = FirebaseDatabase.getInstance().getReference("UserRecipes").child(userId)
        userRecipesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userRecipes = mutableListOf<Recipe>()

                for (recipeSnapshot in snapshot.children) {
                    val recipeId = recipeSnapshot.key ?: ""
                    val recipeReference = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId)
                    recipeReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(recipeDataSnapshot: DataSnapshot) {
                            val recipe = recipeDataSnapshot.getValue(Recipe::class.java)
                            if (recipe != null) {
                                userRecipes.add(recipe)
                            }

                            // Check if this is the last recipe before updating the adapter
                            if (userRecipes.size == snapshot.childrenCount.toInt()) {
                                updateRecipeAdapter(userRecipes)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }


    private fun updateRecipeAdapter(userRecipes: List<Recipe>) {
        val userRecipeAdapter = UserRecipeAdapter(userRecipes, FirebaseStorage.getInstance().reference, FirebaseDatabase.getInstance().reference)
        userRecipesRecyclerView.adapter = userRecipeAdapter
        userRecipesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadUserData(userId: String) {
        val userData = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        userData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(UserProfile::class.java)
                if (userProfile != null) {
                    usernameTextView.text = userProfile.name
                    bioTextView.text = userProfile.bio

                    // Download profile picture from Firebase Storage
                    val storageReference = FirebaseStorage.getInstance().getReference("profilePictures").child(userId)
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        // Load the downloaded image into Glide
                        Glide.with(this@UserProfileActivity)
                            .load(uri)
                            .into(profileImageView)
                    }.addOnFailureListener { exception ->
                        // Handle the failure to download the image
                        // You might want to log or display a default profile picture
                    }
                } else {
                    // Handle the case where userProfile is null
                    // You might want to show a default profile picture or handle it accordingly
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                // You might want to log or display a message indicating the error
            }
        })
    }


}
