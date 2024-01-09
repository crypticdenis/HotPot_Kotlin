package com.example.hotpot

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//TODO: Show picture of user next to them in search results/show recipe picture in search results (not yet implemented in DB)
//TODO: make search results clickable to go to user profile/recipe page
//TODO: don't show search results until user has typed something
//TODO: create user profiles
//TODO: fix issue where not putting a filter and then searching causes the app to crash (-> use both filters by default?)
class SearchActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var filterUserButton: Button
    private lateinit var filterRecipeButton: Button
    private var currentFilter: String = ""

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.searchItemRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize SearchAdapter with an empty list
        searchAdapter = SearchAdapter(emptyList())
        recyclerView.adapter = searchAdapter

        // Initialize SearchView
        searchView = findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query changes
                // You may want to filter your data based on the newText and update the RecyclerView
                updateSearchResults(newText?.toLowerCase().orEmpty(), currentFilter)
                return true
            }
        })


        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_search
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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

        // Initialize buttons
        filterUserButton = findViewById(R.id.searchFilterUser)
        filterRecipeButton = findViewById(R.id.searchFilterRecipe)

        // Set initial styles for buttons
        setButtonStyle(filterUserButton, false)
        setButtonStyle(filterRecipeButton, false)

        filterUserButton.setOnClickListener {
            setButtonStyle(filterUserButton, true)
            setButtonStyle(filterRecipeButton, false)
            // Handle user filter logic here
            currentFilter = "Users"
            updateSearchResults(searchView.query.toString(), currentFilter)
        }

        filterRecipeButton.setOnClickListener {
            setButtonStyle(filterUserButton, false)
            setButtonStyle(filterRecipeButton, true)
            // Handle recipe filter logic here
            currentFilter = "Recipes"
            updateSearchResults(searchView.query.toString(), currentFilter)
        }


    }

    private fun updateSearchResults(query: String, filter: String) {
        // Perform the search and update the adapter with the filtered results
        val filteredResults = performSearch(query, filter)
        searchAdapter.updateData(filteredResults)
    }


    private fun performSearch(query: String, filter: String): List<Any> {
        val results = mutableListOf<Any>()

        val lowercaseQuery = query.toLowerCase()

        val database = FirebaseDatabase.getInstance()
        val reference = when (filter) {
            "Users" -> database.getReference("Users")
            "Recipes" -> database.getReference("Recipes")
            else -> throw IllegalArgumentException("Invalid filter: $filter")
        }

        // Query Firebase based on the search query
        reference.orderByChild("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    // Parse the data
                    val id = snapshot.key ?: ""
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""

                    // Convert both the query and database data to lowercase for case-insensitive comparison
                    val lowercaseName = name.toLowerCase()

                    // Check if the lowercase name contains the lowercase query
                    if (lowercaseName.contains(lowercaseQuery)) {
                        when (filter) {
                            "Users" -> results.add(User(id, name))
                            "Recipes" -> results.add(RecipeInSearch("recipe_image_url", name))
                        }
                    }
                }
                // Update the adapter with the filtered results
                searchAdapter.updateData(results)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if needed
            }
        })

        return results
    }




    @Suppress ("DEPRECATION")
    private fun setButtonStyle(button: Button, isSelected: Boolean) {
        val layoutParams = button.layoutParams
        layoutParams.height = resources.getDimensionPixelSize(if (isSelected) R.dimen.button_height_selected else R.dimen.button_height_unselected)
        button.layoutParams = layoutParams

        button.setBackgroundResource(if (isSelected) R.drawable.circular_green_background else R.drawable.circular_outline_background)

        // Set background tint color directly

        button.backgroundTintList = ColorStateList.valueOf(resources.getColor(if (isSelected) R.color.hotpot_green else R.color.hotpot_dark_green))


        // Set text color to white for the selected button and black for unselected buttons
        button.setTextColor(resources.getColor(if (isSelected) R.color.white else R.color.hotpot_dark_green))
    }


}
