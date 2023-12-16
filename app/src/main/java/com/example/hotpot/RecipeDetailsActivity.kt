package com.example.hotpot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val ingredientsSpinner: Spinner = findViewById(R.id.ingredients_spinner)

        // Retrieve the Recipe object passed through the intent
        val recipe = intent.getSerializableExtra("RECIPE_DATA") as? Recipe

        recipe?.let {
            // Now you can use the ingredients from the recipe to populate the spinner
            val adapter = ArrayAdapter(
                this,
                android.R.layout.spinner_item,
                it.ingredients
            )
            adapter.setDropDownViewResource(android.R.layout.spinner_item)
            ingredientsSpinner.adapter = adapter
        }

        // Set up a listener for the spinner
        ingredientsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // An item was selected. You can handle the selection here if needed.
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Interface callback for no selection
            }
        }

        val returnToHomeButton = findViewById<ImageView>(R.id.returnToHomeButton)
        returnToHomeButton.setOnClickListener {
            // Create an Intent to go back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the RecipeDetailsActivity
        }
    }
}


