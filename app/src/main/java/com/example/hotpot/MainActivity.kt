package com.example.hotpot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.hotpot.model.Recipe
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var recipes: List<Recipe>
    private var selectedRecipe: Recipe? = null

    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)
        FirebaseApp.initializeApp(this)

        loadRecipesFromJson()

        findViewById<Button>(R.id.random_meal_btn).setOnClickListener {
            showRandomMeal()
        }

        findViewById<Button>(R.id.show_recipe_btn).setOnClickListener {
            selectedRecipe?.let { recipe ->
                openRecipeDetailsActivity(recipe)
            }
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
                    // TODO: Switch to the dashboard fragment/activity
                    true
                }
                R.id.navigation_list -> {
                    // TODO: Switch to the notifications fragment/activity
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

    private fun openRecipeDetailsActivity(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailsActivity::class.java)
        intent.putExtra("RECIPE_DATA", recipe)
        startActivity(intent)
    }

    private fun loadRecipesFromJson() {
        val jsonFileString = getJsonDataFromAsset(applicationContext, "recipes.json")
        val gson = Gson()
        val listRecipeType = object : TypeToken<List<Recipe>>() {}.type
        recipes = gson.fromJson(jsonFileString, listRecipeType)
    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }

    private fun showRandomMeal() {
        if (recipes.isNotEmpty()) {
            selectedRecipe = recipes.random()
            findViewById<TextView>(R.id.recipe_name).text = selectedRecipe?.name ?: "No Name"
            findViewById<TextView>(R.id.recipe_info).text = selectedRecipe?.details ?: "No Details"
        }
    }
    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.navigation_home // Replace 'navigation_home' with the actual ID of the home item
    }

}
