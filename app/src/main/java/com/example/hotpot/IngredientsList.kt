package com.example.hotpot

import FridgeFragment
import android.content.Intent
import com.example.hotpot.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView


class IngredientsList : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var meatBox : CardView
    private lateinit var vegeBox: CardView
    private lateinit var nutsBox: CardView
    private lateinit var herbsBox: CardView
    private lateinit var milkBox: CardView
    private lateinit var riceBox: CardView
    private lateinit var fruitsBox: CardView
    private lateinit var othersBox: CardView

    private lateinit var fridgeBtn : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ingredients_list)

        // Assuming you have a SearchView with the id "searchView" in your layout
        val searchView: SearchView = findViewById(R.id.searchView)
        val homeButton : ImageButton = findViewById(R.id.ingredientsList_HomeButton)

        meatBox = findViewById(R.id.meat_box);
        vegeBox = findViewById(R.id.vege_box)
        nutsBox = findViewById(R.id.nuts_box)
        herbsBox = findViewById(R.id.herbs_box)
        milkBox = findViewById(R.id.milk_box)
        riceBox = findViewById(R.id.rice_box)
        fruitsBox = findViewById(R.id.fruits_box)
        othersBox = findViewById(R.id.others_box)

        homeButton.setOnClickListener {
            finish();
        }

        fridgeBtn = findViewById(R.id.fridgeButton)
        fridgeBtn.setOnClickListener {
            val ingredientListLayout = findViewById<LinearLayout>(R.id.ingredientListLayout)
            ingredientListLayout.visibility = View.GONE

            // Öffne FridgeFragment und übergebe die ausgewählte Kategorie
            val fridgeFragment = FridgeFragment()
            val args = Bundle()
            args.putString("selectedCategory", "Meat")
            fridgeFragment.arguments = args

            // Füge das Fragment dem Fragmentmanager hinzu
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fridgeFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        meatBox.setOnClickListener {
            val ingredientListLayout = findViewById<LinearLayout>(R.id.ingredientListLayout)
            ingredientListLayout.visibility = View.GONE

            val fridgeContentFragment = FridgeContentFragment.newInstance("Meat")
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fridgeContentFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        vegeBox.setOnClickListener {
            val ingredientListLayout = findViewById<LinearLayout>(R.id.ingredientListLayout)
            ingredientListLayout.visibility = View.GONE

            val fridgeContentFragment = FridgeContentFragment.newInstance("Vegetables")
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fridgeContentFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        customizeSearchView(searchView)

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
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.navigation_list -> {
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

    private fun customizeSearchView(searchView: SearchView) {
        // Set color for search text
        val searchEditText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.hotpot_light_green))

        // Set hint text color
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.hint_search_color))

        // Set magnifying glass icon color
        val searchIcon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.hotpot_light_green))
    }
}
