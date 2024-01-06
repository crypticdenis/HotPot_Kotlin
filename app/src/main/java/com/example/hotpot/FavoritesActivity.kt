package com.example.hotpot

import RecipeDetailsFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.auth.FirebaseAuth


/*
class FavoritesActivity : AppCompatActivity() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private var favoriteRecipes: List<Recipe> = listOf() // This will hold our dummy data
    // Initialize an empty list for favorite recipes
    val favoriteRecipesList: MutableList<Recipe> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_favorites)
        setSupportActionBar(toolbar)

        val returnButton = findViewById<ImageButton>(R.id.toolbar_return_button)
        returnButton.setOnClickListener {
            // Handle the return button click
            finish();
            onBackPressed()
        }

        // Initialize RecyclerView and set its adapter and layout manager
        favoritesRecyclerView = findViewById(R.id.favorites_recycler_view)
        favoritesAdapter = FavoritesAdapter(favoriteRecipes)
        favoritesRecyclerView.adapter = favoritesAdapter
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Load dummy favorite recipes
        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        // Dummy data for favorite recipes
        favoriteRecipes = listOf(
            Recipe(
                name = "Spaghetti Carbonara",
                description = "A classic Italian pasta dish...",
                ingredients = listOf("Pasta", "Eggs", "Cheese"),
                instructions = "Cook pasta. Mix eggs and cheese. Combine.",
                details = "Difficulty: Easy | Time: 30min",
                tags = listOf("Italian", "Pasta")
            ),
            Recipe(
                name = "Chicken Curry",
                description = "A spicy and flavorful dish...",
                ingredients = listOf("Chicken", "Curry Powder", "Coconut Milk"),
                instructions = "Cook chicken. Add spices and milk.",
                details = "Difficulty: Medium | Time: 45min",
                tags = listOf("Indian", "Spicy")
            )
            // Add more recipes as needed

            // Load favorites from the database
            val databaseReference = FirebaseDatabase.getInstance().reference.child("Favorites")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (recipeSnapshot in dataSnapshot.children) {
                    // Parse the Recipe data from the snapshot
                    val name = recipeSnapshot.child("name").getValue(String::class.java)
                    val description = recipeSnapshot.child("description").getValue(String::class.java)
                    val ingredients = recipeSnapshot.child("ingredients").getValue<List<String>>()
                    val instructions = recipeSnapshot.child("instructions").getValue(String::class.java)
                    val details = recipeSnapshot.child("details").getValue(String::class.java)
                    val tags = recipeSnapshot.child("tags").getValue<List<String>>()

                    // Add the parsed recipe to the list if data is not null
                    if (name != null && description != null && ingredients != null && instructions != null && details != null && tags != null) {
                        val recipe = Recipe(name, description, ingredients, instructions, details, tags)
                        favoriteRecipesList.add(recipe)
                    }
                }
        )

        // Update the adapter with the new data
        favoritesAdapter.updateData(favoriteRecipes)
    }
}
                */

class FavoritesActivity : AppCompatActivity() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private var favoriteRecipes: List<Recipe> = listOf()
    private lateinit var userId: String
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        favoritesRecyclerView = findViewById(R.id.favorites_recycler_view)
        // Pass the click listener when creating the FavoritesAdapter
        favoritesAdapter = FavoritesAdapter(favoriteRecipes, object : OnRecipeClickListener {
            override fun onRecipeClick(position: Int) {
                val clickedRecipe = favoriteRecipes[position]
                // Handle the click event, for example, open a new activity or show details
                val detailsFragment = RecipeDetailsFragment()
                val bundle = Bundle()
                bundle.putSerializable("RECIPE_DATA", clickedRecipe)
                detailsFragment.arguments = bundle
                detailsFragment.show(supportFragmentManager, detailsFragment.tag)
            }
        })
        favoritesRecyclerView.adapter = favoritesAdapter
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)

        val returnButton = findViewById<ImageButton>(R.id.toolbar_return_button)
        returnButton.setOnClickListener {
            finish()
            onBackPressed()
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Favorites")

        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                favoriteRecipes = emptyList()

                for (recipeSnapshot in dataSnapshot.children) {
                    val recipe = recipeSnapshot.getValue(Recipe::class.java)
                    recipe?.let { favoriteRecipes = favoriteRecipes + it }
                }

                favoritesAdapter.updateData(favoriteRecipes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}




