package com.example.hotpot

import FriendStoriesFragment
import RecipeDetailsFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import com.bumptech.glide.Glide
import kotlin.math.log

// TODO: Change colours to proper green hotpot colours
@Suppress("DEPRECATION")
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

        if (savedInstanceState == null) {
            // Load your fragment_friend_stories into the FriendsFrameLayout
            loadFragment(FriendStoriesFragment())
            // set background of fragment to transparent
            findViewById<FrameLayout>(R.id.friendsFrameLayout).setBackgroundColor(0x00000000)

        }

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
            selectedRecipe?.let { recipe ->
                showAddToFavoritesDialog(recipe)
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_home -> {
                    true
                }

                R.id.navigation_list -> {
                    val intent = Intent(this, ShoppingListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_search -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the contents of FriendsFrameLayout with the provided fragment
        fragmentTransaction.replace(R.id.friendsFrameLayout, fragment)

        // Add the transaction to the back stack (optional)
        fragmentTransaction.addToBackStack(null)

        // Commit the transaction
        fragmentTransaction.commit()
    }

    private fun openRecipeDetailsFragment(recipe: Recipe) {
        val recipeDetailsFragment = RecipeDetailsFragment()
        val bundle = Bundle()
        bundle.putSerializable("RECIPE_DATA", recipe)
        recipeDetailsFragment.arguments = bundle
        recipeDetailsFragment.show(supportFragmentManager, recipeDetailsFragment.tag)
    }

    private fun showAddToFavoritesDialog(recipe: Recipe) {
        val options = arrayOf("Als aktuelle UserStory einstellen", "Füge zu Favoriten hinzu")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Optionen auswählen")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Als aktuelle UserStory einstellen ausgewählt
                        setAsCurrentUserStory(recipe)
                    }
                    1 -> {
                        // Füge zu Favoriten hinzu ausgewählt
                        addToFavorites(recipe)
                        Toast.makeText(this@MainActivity, "Rezept zu Favoriten hinzugefügt!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setAsCurrentUserStory(recipe: Recipe) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val userStoryReference = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(it)
                .child("UserStory")
                .child("0") // Change here to save the recipe under a node with ID 0

            // Delete existing data under "UserStory/0"
            userStoryReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save the recipe under "UserStory/0"
                    userStoryReference.setValue(recipe).addOnSuccessListener {
                        Toast.makeText(this@MainActivity, "Recipe set as current UserStory!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@MainActivity, "Error saving UserStory: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error deleting existing data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(this@MainActivity, "Error retrieving user ID.", Toast.LENGTH_SHORT).show()
        }
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

        // Liste zum Sammeln der Rezepte
        val recipes: MutableList<Recipe> = mutableListOf()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (recipeSnapshot in dataSnapshot.children) {
                    val name = recipeSnapshot.child("name").getValue(String::class.java)
                    val description = recipeSnapshot.child("description").getValue(String::class.java)
                    val instructions = recipeSnapshot.child("instructions").getValue(String::class.java)
                    val details = recipeSnapshot.child("details").getValue(String::class.java)
                    val tags = recipeSnapshot.child("tags").getValue<List<String>>()
                    val creditUserID = recipeSnapshot.child("credit").getValue(String::class.java).toString()

                    Log.d("firebase", "creditsUserID: $creditUserID")

                    val ingredientsSnapshot = recipeSnapshot.child("ingredients")
                    val ingredientsMap = mutableMapOf<String, Any>()

                    for (ingredientSnapshot in ingredientsSnapshot.children) {
                        val ingredientName = ingredientSnapshot.key
                        val ingredientDetails = ingredientSnapshot.getValue<Map<String, Any>>()
                        ingredientsMap[ingredientName!!] = ingredientDetails!!
                    }

                    // Füge nur nicht-null Daten zur Liste hinzu
                    if (name != null && description != null && instructions != null && details != null && tags != null) {
                        val recipe = Recipe(name, "", description, ingredientsMap, instructions, details, tags, creditUserID)
                        recipes.add(recipe)
                    }
                }
                // Wenn Rezepte vorhanden sind
                if (recipes.isNotEmpty()) {
                    // Wähle ein zufälliges Rezept aus
                    selectedRecipe = recipes.random()

                    // Suche nach dem Credit für das ausgewählte Rezept
                    searchCreditForSelectedRecipe(selectedRecipe)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Fehler beim Lesen der Datenbank: ${error.message}")
            }
        })
    }

    private fun searchCreditForSelectedRecipe(selectedRecipe: Recipe?) {
        selectedRecipe?.let {
            val creditUserID = it.credits

            Log.d("searchCredit", "CreditUsERID: $creditUserID")

            Log.d("Firebase", "Credits: $creditUserID")

            val usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(
                it.credits.toString()
            ).child("name")

            usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userNameSnapshot: DataSnapshot) {
                    val userName = userNameSnapshot.getValue(String::class.java) ?: "Unknown User"

                    Log.d("Firebase", "Username: $userName")

                    // Setze die Daten für das ausgewählte Rezept
                    findViewById<TextView>(R.id.recipe_name).text = it.name
                    findViewById<TextView>(R.id.recipe_info).text = it.details
                    findViewById<TextView>(R.id.recipe_credit).text = ("by $userName")

                    // Lade das Bild nur für das ausgewählte Rezept
                    val foodPictureImageView: ImageView = findViewById(R.id.food_picture)
                    loadImageFromFirebaseStorage(it.name.toString(), foodPictureImageView)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Fehler beim Lesen der Benutzerdaten: ${error.message}")
                }
            })
        }
    }



    // Funktion zum Laden des Bildes von der Storage und Anzeigen im ImageView
    private fun loadImageFromFirebaseStorage(imageFileName: String?, imageView: ImageView) {
        if (imageFileName == null || imageFileName.isBlank()) {
            return
        }
        val storageReference = FirebaseStorage.getInstance().reference
        val sanitizedImageFileName = sanitizeRecipeNameForStorage(imageFileName)
        val recipePictureReference = storageReference.child("recipes").child(sanitizedImageFileName)

        Log.d("FirebaseStorage", "Versuche Bild zu laden: $sanitizedImageFileName")

        // Holen Sie sich die Download-URL für das Bild
        recipePictureReference.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            // Verwende Glide, um das Bild von der URL in das ImageView zu laden
            Glide.with(this@MainActivity)
                .load(imageUrl).override(375,250)
                .into(imageView)

            Log.d("FirebaseStorage", "Bild erfolgreich geladen: $imageUrl")
        }.addOnFailureListener { exception ->
            // Handle den Fehler beim Abrufen der Download-URL
            Log.e("FirebaseStorage", "Fehler beim Laden des Bildes: ${exception.message}")
        }
    }


    private fun sanitizeRecipeNameForStorage(recipeName: String): String {
        return recipeName.replace(" ", "")
    }




    override fun onResume() {
    super.onResume()
    bottomNavigationView.selectedItemId = R.id.navigation_home // Replace 'navigation_home' with the actual ID of the home item
    }

    /**
     * prevents returning to login/signUp screen
     */
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
    }

