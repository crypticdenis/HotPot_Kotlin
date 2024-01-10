package com.example.hotpot

import RecipeDetailsFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

// Add an interface for the callback
interface AdapterCallback {
    fun deactivateUIElements()
}

class SearchAdapter(
    private var dataset: List<Any>,
    private val storageReference: StorageReference,
    private val databaseReference: DatabaseReference,
    private val activity: AppCompatActivity,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterCallback {



    private val USER_TYPE = 1
    private val RECIPE_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            USER_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user, parent, false)
                UserViewHolder(view)
            }
            RECIPE_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recipe_in_search, parent, false)
                RecipeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                val user = dataset[position] as User
                holder.bind(user, storageReference, databaseReference)
            }
            is RecipeViewHolder -> {
                val recipe = dataset[position] as RecipeInSearch
                holder.bind(recipe, storageReference)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataset[position]) {
            is User -> USER_TYPE
            is RecipeInSearch -> RECIPE_TYPE
            else -> throw IllegalArgumentException("Invalid data type")
        }
    }

    fun updateData(newDataset: List<Any>) {
        dataset = newDataset
        notifyDataSetChanged()
    }

    fun clearData() {
        dataset = emptyList()
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val userIconImageView: ImageView = itemView.findViewById(R.id.userIconImageView)

        fun bind(user: User, storageReference: StorageReference, databaseReference: DatabaseReference) {
            userNameTextView.text = user.userName
            // get all users in a list
            val usersReference = databaseReference.child("Users")
            usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Iterate through the list of users
                    for (userSnapshot in dataSnapshot.children) {
                        val userNameFromDatabase = userSnapshot.child("name").getValue(String::class.java)
                        val uidSnapshot = userSnapshot.child("uid")
                        val userUID = uidSnapshot.value.toString()

                        Log.d("Firebase", "UserName: $userNameFromDatabase UserUID: $userUID");

                        // Check if the current user's name matches the desired user's name
                        if (userNameFromDatabase == user.userName) {
                            // Use the uidSnapshot to construct the storage reference for the profile picture
                            storageReference.child("profilePictures").child(userUID).downloadUrl.addOnSuccessListener { uri ->
                                // Load the image using Glide
                                Glide.with(itemView.context).load(uri).into(userIconImageView)
                            }.addOnFailureListener { exception ->
                                // Handle any errors that may occur while fetching the image URL
                                // Log.e("UserViewHolder", "Error fetching user profile image URL", exception)
                            }
                            // Break out of the loop since the user is found
                            break
                        }
                        userIconImageView.setOnClickListener {
                            Log.d("FireBase","You clicked on ${userNameTextView.text} with ID: $userUID")
                            // Handle the click event here or navigate to the user profile
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors if needed
                }
            })
        }
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val recipeIconImageView: ImageView = itemView.findViewById(R.id.recipeIconImageView)
        //private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        fun bind(recipe: RecipeInSearch, storageReference: StorageReference) {
            recipeNameTextView.text = recipe.recipeName

            val formattedRecipeName = recipe.recipeName.replace(" ", "")

            storageReference.child("recipes/").child(formattedRecipeName).downloadUrl.addOnSuccessListener { uri ->
                // Load the image using Glide
                Glide.with(itemView.context).load(uri).into(recipeIconImageView)
            }

            recipeIconImageView.setOnClickListener {
                // Deactivate UI elements before handling the click action
                deactivateUIElements()

                // Handle the click event here or navigate to the recipe details
                val bundle = Bundle()
                var recipeObject = Recipe()

                val database = databaseReference.child("Recipes")
                val recipeName = recipeNameTextView.text.toString()

                val recipeQuery = database.orderByChild("name").equalTo(recipeName)

                recipeQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (items in dataSnapshot.children) {
                            recipeObject.name = items.child("name").value.toString()
                            recipeObject.imageUrl = items.child("imageUrl").value.toString()
                            recipeObject.description = items.child("description").value.toString()
                            recipeObject.ingredients = items.child("ingredients").value as MutableMap<String, Any>
                            recipeObject.instructions = items.child("instructions").value.toString()
                            recipeObject.details = items.child("details").value.toString()

                            // Assuming tags are stored as a JSON array in Firebase
                            val tagsList = mutableListOf<String>()
                            for (tagSnapshot in items.child("tags").children) {
                                val tag = tagSnapshot.value.toString()
                                tagsList.add(tag)
                            }
                            recipeObject.tags = tagsList

                            bundle.putSerializable("RECIPE_DATA", recipeObject)
                            val recipeDetailsFragment = RecipeDetailsFragment()
                            recipeDetailsFragment.arguments = bundle

                            // Access the activity through the context and deactivate UI elements
                            val activity = itemView.context as AppCompatActivity
                            deactivateUIElements()
                            //activity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, recipeDetailsFragment).commit()
                            recipeDetailsFragment.show(activity.supportFragmentManager, recipeDetailsFragment.tag)
                            activity.supportFragmentManager.executePendingTransactions()
                            recipeDetailsFragment.view?.requestFocus()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors if needed
                    }
                })
            }
        }
    }

    override fun deactivateUIElements() {
        (activity as? AdapterCallback)?.deactivateUIElements()
    }

}
