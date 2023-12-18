package com.example.hotpot

import android.R
import androidx.appcompat.widget.SearchView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class IngredientsList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_layout)

        // Assuming you have a SearchView with the id "searchView" in your layout
        val searchView: SearchView = findViewById(R.id.searchView)

        customizeSearchView(searchView)
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
