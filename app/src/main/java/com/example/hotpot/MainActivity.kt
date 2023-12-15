package com.example.hotpot

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

    /**
     * navigation bar
     * toolbar for navigation bar
     */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navBar: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    /**
     * reference to the horizontalLayout that contains the userStories
     * having an array to add new stories
     * update the layout with array contents
     */

    /*
    val userStories = arrayOf(
        // add here fragment
        // create fragments that are built like in Figma

    )
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)
        FirebaseApp.initializeApp(this)
        loadRecipesFromJson()

        val rndMealBtn = findViewById<Button>(R.id.random_meal_btn)
        rndMealBtn.setOnClickListener {
            showRandomMeal()


            //addUserStory();

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            drawerLayout = findViewById(R.id.drawer_Layout)
            navBar = findViewById(R.id.nav_view)
            drawerToggle =
                ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(drawerToggle)
            drawerToggle.syncState()
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

            navBar.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
                if (item.itemId == R.id.shoppingList_btn)
                    Toast.makeText(
                        this@MainActivity, "Home Selected", Toast.LENGTH_SHORT
                    ).show()
                if (item.itemId == R.id.savedRecipes_btn)
                    Toast.makeText(
                        this@MainActivity, "Contact Selected", Toast.LENGTH_SHORT
                    ).show()
                if (item.itemId == R.id.addFriends_btn)
                    Toast.makeText(
                        this@MainActivity, "Gallery Selected", Toast.LENGTH_SHORT
                    ).show()
                if (item.itemId == R.id.settings_btn)
                    Toast.makeText(
                        this@MainActivity, "About Selected", Toast.LENGTH_SHORT
                    ).show()
                false
            })
        }
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
            val randomRecipe = recipes.random()

            // Find the TextViews by their IDs
            val recipeNameTextView = findViewById<TextView>(R.id.recipe_name)
            val recipeInfoTextView = findViewById<TextView>(R.id.recipe_info) // Add this line

            // Set the text of the TextViews
            recipeNameTextView.text = randomRecipe.name
            recipeInfoTextView.text = randomRecipe.details // Update the recipe_info TextView

            // Optional: Display a toast or perform other actions
            // Toast.makeText(this, "Recipe: ${randomRecipe.name}", Toast.LENGTH_SHORT).show()
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

    private fun addUserStory() {
        /*
        for (resourceId in userStories) {

            val imageFragment = ImageFragment.newInstance(resourceId)
            supportFragmentManager.beginTransaction()
                .add(horizontalLayout.id, imageFragment)
                .commit()
             */
        }
}