package com.example.hotpot

import RecipeDetailsFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var recipes: List<Recipe>
    private var selectedRecipe: Recipe? = null

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)
        FirebaseApp.initializeApp(this)

        // First recipe is null, that's why no recipe details open up
        showRandomMeal();

        findViewById<Button>(R.id.random_meal_btn).setOnClickListener {
            showRandomMeal()
        }

        findViewById<Button>(R.id.show_recipe_btn).setOnClickListener {
            selectedRecipe?.let { recipe ->
                openRecipeDetailsFragment(recipe)
            }
        }

        findViewById<ImageButton>(R.id.addRecipeOverlayBtn).setOnClickListener {
            showAddRecipePopupMenu()
        }

        findViewById<ImageButton>(R.id.addToFavoritesBtn).setOnClickListener {
            selectedRecipe?.let { it1 -> addToFavorites(it1) };
            Toast.makeText(this@MainActivity, "Rezept zu Favoriten hinzugefügt!", Toast.LENGTH_SHORT).show();
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
                    val intent = Intent(this, IngredientsList::class.java)
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

    private fun openRecipeDetailsFragment(recipe: Recipe) {
        val recipeDetailsFragment = RecipeDetailsFragment()
        val bundle = Bundle()
        bundle.putSerializable("RECIPE_DATA", recipe)
        recipeDetailsFragment.arguments = bundle
        recipeDetailsFragment.show(supportFragmentManager, recipeDetailsFragment.tag)
    }


    private fun loadRecipesFromJson() {
        val jsonFileString = getJsonDataFromAsset(applicationContext, "recipes.json")
        val gson = Gson()
        val listRecipeType = object : TypeToken<List<Recipe>>() {}.type
        recipes = gson.fromJson(jsonFileString, listRecipeType);
        selectedRecipe = recipes[0];
        uploadRecipes(recipes);

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
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Recipes")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipes: MutableList<Recipe> = mutableListOf()

                for (recipeSnapshot in dataSnapshot.children) {
                    // Überprüfe, ob die Daten nicht null sind
                    val name = recipeSnapshot.child("name").getValue(String::class.java)
                    val description = recipeSnapshot.child("description").getValue(String::class.java)
                    val ingredients = recipeSnapshot.child("ingredients").getValue<List<String>>()
                    val instructions = recipeSnapshot.child("instructions").getValue(String::class.java)
                    val details = recipeSnapshot.child("details").getValue(String::class.java)
                    val tags = recipeSnapshot.child("tags").getValue<List<String>>()

                    // Füge nur nicht-null Daten zur Liste hinzu
                    if (name != null && description != null && ingredients != null && instructions != null && details != null && tags != null) {
                        val recipe = Recipe(name, description, ingredients, instructions, details, tags)
                        recipes.add(recipe)
                    }
                }

                if (recipes.isNotEmpty()) {
                    selectedRecipe = recipes.random()

                    findViewById<TextView>(R.id.recipe_name).text = selectedRecipe?.name ?: "No Name"
                    findViewById<TextView>(R.id.recipe_info).text = selectedRecipe?.details ?: "No Details"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Fehler beim Lesen der Datenbank: ${error.message}")
            }
        })
    }

    override fun onResume() {
    super.onResume()
    bottomNavigationView.selectedItemId = R.id.navigation_home // Replace 'navigation_home' with the actual ID of the home item
    }

    /**
     * prevents returning to login/signUp screen
     */
    override fun onBackPressed() {
        // Hier kannst du spezielle Logik für den Back-Button hinzufügen,
        // oder einfach nichts tun, um das Standardverhalten zu behalten.

        // Beispiel: Keine Aktion durchführen
        super.onBackPressed()
    }

    private fun showAddRecipePopupMenu() {
        val anchorView = findViewById<ImageButton>(R.id.addRecipeOverlayBtn)

        // Erstelle die PopupWindow-Instanz
        val popupWindow = PopupWindow(
            layoutInflater.inflate(R.layout.fragment_add_recipe, null),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Finde die Buttons im Popup-Layout
        val addRecipe = popupWindow.contentView.findViewById<Button>(R.id.addRecipeBtn)
        val closeAddRecipeOverlay = popupWindow.contentView.findViewById<Button>(R.id.closeAddRecipeBtn)

        // Setze die Klick-Listener für die Buttons im Popup
        addRecipe.setOnClickListener {
            Log.d("PopupMenu", "Menu Item 1 clicked")
            // Hier kannst du die Aktion für "Menu Item 1" ausführen
            popupWindow.dismiss()

            // Navigate to AddRecipe
            val intent = Intent(this, AddRecipe::class.java)
            startActivity(intent)
        }

        closeAddRecipeOverlay.setOnClickListener {
            Log.d("PopupMenu", "Menu Item 2 clicked")
            // Hier kannst du die Aktion für "Menu Item 2" ausführen
            popupWindow.dismiss()
        }

        // Öffne das Popup-Menü an der Position des Anker-Views
        popupWindow.showAsDropDown(anchorView)
    }
}

    private fun addToFavorites(selectedRecipe: Recipe) {
        // Überprüfe, ob ein ausgewähltes Rezept vorhanden ist
        if (selectedRecipe != null) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            userId?.let {
                val favoritesReference = FirebaseDatabase.getInstance().reference.child("Users").child(it).child("Favorites")

                // Erstelle eine eindeutige ID für das favorisierte Rezept
                val favoriteRecipeId = favoritesReference.push().key

                if (favoriteRecipeId != null) {
                    favoritesReference.child(favoriteRecipeId).setValue(selectedRecipe)
                        .addOnSuccessListener {
                            // Erfolgreich hinzugefügt
                            Log.d("Firebase", "Rezept erfolgreich zu Favoriten hinzugefügt.")
                        }
                        .addOnFailureListener {
                            // Fehler beim Hinzufügen
                            Log.e("Firebase", "Fehler beim Hinzufügen des Rezepts zu Favoriten: ${it.message}")
                        }
                } else {
                    // Falls favoriteRecipeId null ist
                    Log.e("Firebase", "Fehler beim Erstellen einer eindeutigen ID für das Rezept.")
                }
            } ?: run {
                // Falls userId null ist
                Log.e("Firebase", "Fehler beim Abrufen der Benutzer-ID.")
            }

        } else {
            // Ausgewähltes Rezept ist null
            Log.w("Firebase", "Ausgewähltes Rezept ist null. Nichts zu favorisieren.")
        }
    }

