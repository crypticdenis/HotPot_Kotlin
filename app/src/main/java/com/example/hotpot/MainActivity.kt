package com.example.hotpot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class MainActivity : AppCompatActivity() {
    //val horizontalLayout = findViewById<LinearLayout>(R.id.userStoriesContainer)
    private lateinit var recipes: List<Recipe>  // Declare at class level
    private var selectedRecipe: Recipe? = null


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navBar: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)
        FirebaseApp.initializeApp(this)

        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        loadRecipesFromJson()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setupDrawerAndNavigation()

        val rndMealBtn = findViewById<Button>(R.id.random_meal_btn)
        rndMealBtn.setOnClickListener {
            showRandomMeal()
        }

        // Setup click listener for the Show Recipe button
        val showRecipeButton = findViewById<Button>(R.id.show_recipe_btn)
        showRecipeButton.setOnClickListener {
            selectedRecipe?.let { recipe ->
                openRecipeDetailsActivity(recipe)
            }
        }
    }

    private fun setupDrawerAndNavigation() {
        drawerLayout = findViewById(R.id.drawer_Layout)
        navBar = findViewById(R.id.nav_view)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navBar.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            // Your navigation item selection handling
            false
        })

        setupDrawerMenu()
    }

    private fun openRecipeDetailsActivity(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailsActivity::class.java)
        intent.putExtra("RECIPE_DATA", recipe) // Assuming Recipe implements Serializable
        startActivity(intent)
    }

        private fun loadRecipesFromJson() {
            val jsonFileString = getJsonDataFromAsset(applicationContext, "recipes.json")
            val gson = Gson()
            val listRecipeType = object : TypeToken<List<Recipe>>() {}.type
            recipes = gson.fromJson(jsonFileString, listRecipeType)
        }

        private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

    private fun showRandomMeal() {
        if (recipes.isNotEmpty()) {
            selectedRecipe = recipes.random()

            // Assuming recipe_name and recipe_info are TextView IDs in your layout
            findViewById<TextView>(R.id.recipe_name).text = selectedRecipe?.name ?: "No Name"
            findViewById<TextView>(R.id.recipe_info).text = selectedRecipe?.details ?: "No Details"
        }
    }

    private fun setupDrawerMenu() {
        val menu = navBar.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val spannableString = SpannableString(menuItem.title)
            spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.hotpot_light_green)), 0, spannableString.length, 0)
            spannableString.setSpan(AbsoluteSizeSpan(25, true), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            spannableString.setSpan(TypefaceSpan("abeezee"), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            menuItem.title = spannableString
        }
    }

}