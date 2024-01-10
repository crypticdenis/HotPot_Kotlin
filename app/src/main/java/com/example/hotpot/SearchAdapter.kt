package com.example.hotpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class SearchAdapter(private var dataset: List<Any>, private val storageReference: StorageReference, private val databaseReference: DatabaseReference) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                        val userNameFromDatabase = userSnapshot.child("Name").getValue(String::class.java)
                        val uidSnapshot = userSnapshot.child("uid")
                        val userUID = uidSnapshot.value.toString()

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
                            Toast.makeText(itemView.context, "You clicked on ${userNameTextView.text} with ID: $userUID", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors if needed
                }
            })
        }
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val recipeIconImageView: ImageView = itemView.findViewById(R.id.recipeIconImageView)

        fun bind(recipe: RecipeInSearch, storageReference: StorageReference) {
            recipeNameTextView.text = recipe.recipeName

            val formattedRecipeName = recipe.recipeName.replace(" ", "")

            storageReference.child("recipes/").child(formattedRecipeName).downloadUrl.addOnSuccessListener { uri ->
                // Load the image using Glide
                Glide.with(itemView.context).load(uri).into(recipeIconImageView)
            }.addOnFailureListener { exception ->
                // Handle any errors that may occur while fetching the image URL
                // Log.e("RecipeViewHolder", "Error fetching recipe image URL", exception)
            }
            // For example:
            Glide.with(itemView.context).load(storageReference.child(recipe.recipeImage)).into(recipeIconImageView)

            recipeIconImageView.setOnClickListener {
                Toast.makeText(itemView.context, "You clicked on $recipeNameTextView", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
