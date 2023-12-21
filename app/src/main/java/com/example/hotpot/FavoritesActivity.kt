package com.example.hotpot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotpot.adapter.FavoritesAdapter
import com.example.hotpot.model.Recipe
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar



class FavoritesActivity : AppCompatActivity() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private var favoriteRecipes: List<Recipe> = listOf() // This will hold our dummy data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_favorites)
        setSupportActionBar(toolbar)

        val returnButton = findViewById<ImageButton>(R.id.toolbar_return_button)
        returnButton.setOnClickListener {
            // Handle the return button click
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
        )

        // Update the adapter with the new data
        favoritesAdapter.updateData(favoriteRecipes)
    }
}
