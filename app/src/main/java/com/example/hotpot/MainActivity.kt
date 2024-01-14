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
    private var currentUserTags = mutableListOf<String>()


    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)
        FirebaseApp.initializeApp(this)

        // Fetch current user's tags
        fetchCurrentUserTags()

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

    private fun fetchCurrentUserTags() {
        // Fetch current user's tags
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val currentUserTagsReference = FirebaseDatabase.getInstance().reference.child("Users").child(it).child("Tags")

            currentUserTagsReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Clear the existing tags
                    currentUserTags.clear()

                    // Iterate through the children and add each tag to the list
                    for (tagSnapshot in dataSnapshot.children) {
                        val tagValue = tagSnapshot.getValue(String::class.java)
                        tagValue?.let {
                            currentUserTags.add(it)
                        }
                    }

                    // Now, currentUserTags list contains all the tag values for the current user
                    // You can use this list as needed in your application
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.e("Firebase", "Error fetching user tags: ${error.message}")
                }
            })
        } ?: run {
            // Handle case where userId is null
            Log.e("Firebase", "Error retrieving user ID.")
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
                        addToFavorites(recipe, this@MainActivity)
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

                        // check if the recipe has a tag that the currentUser is allergic to etc.
                        if (recipe.tags.intersect(currentUserTags).isEmpty()) {
                            recipes.add(recipe)
                        }
                    }
                }
                // Wenn Rezepte vorhanden sind
                if (recipes.isNotEmpty()) {
                    // Wähle ein zufälliges Rezept aus
                    selectedRecipe = recipes.random()

                    // continue randomizing till a recipe with no common tags come up and not the same recipe
                    if (selectedRecipe?.tags?.intersect(currentUserTags)?.isEmpty() == false ||
                        selectedRecipe?.name == findViewById<TextView>(R.id.recipe_name).text.toString()
                    ) {
                        showRandomMeal()
                    } else {
                        // Suche nach dem Credit für das ausgewählte Rezept
                        searchCreditForSelectedRecipe(selectedRecipe)
                    }
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
        if (imageFileName.isNullOrBlank()) {
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
private fun addToFavorites(selectedRecipe: Recipe, context: Context) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    userId?.let {
        val favoritesReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(it).child("Favorites")

        // check if recipe is already in Favorites
        favoritesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipeExists = dataSnapshot.children.any { favoriteSnapshot ->
                    val name = favoriteSnapshot.child("name").getValue(String::class.java)
                    name != null && name == selectedRecipe.name
                }

                if (recipeExists) {
                    // Das Rezept existiert bereits in den Favoriten
                    Log.d("Firebase", "Rezept existiert bereits in den Favoriten.")
                    showToast("Rezept ist schon favorisiert", context)
                } else {
                    // Das Rezept existiert noch nicht in den Favoriten, füge es hinzu
                    val randomKey = favoritesReference.push().key

                    if (randomKey != null) {
                        favoritesReference.child(randomKey).setValue(selectedRecipe)
                        // Hier kannst du weitere Eigenschaften des Rezepts hinzufügen
                        Log.d("Firebase", "Rezept erfolgreich zu Favoriten hinzugefügt.")
                        showToast("Rezept zu Favoriten hinzugefügt!", context)
                    } else {
                        // Falls randomKey null ist
                        Log.e(
                            "Firebase",
                            "Fehler beim Erstellen eines random Keys für das Rezept."
                        )
                        showToast("Fehler beim Hinzufügen zu Favoriten", context)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Fehler beim Lesen der Datenbank: ${error.message}")
                showToast("Fehler beim Hinzufügen zu Favoriten", context)
            }
        })
    } ?: run {
        // Falls userId null ist
        Log.e("Firebase", "Fehler beim Abrufen der Benutzer-ID.")
        showToast("Fehler beim Hinzufügen zu Favoriten", context)
    }
}
    private fun showToast(message: String, context: Context) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }


