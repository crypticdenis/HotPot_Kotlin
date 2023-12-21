package com.example.hotpot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.hotpot.model.Recipe
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var selectedIngredients: BooleanArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val recipeTitleTextView: TextView = findViewById(R.id.recipe_title)
        val recipeStepsTextView: TextView = findViewById(R.id.recipe_steps)
        val dietaryInfoTextView: TextView = findViewById(R.id.dietary_info) // TextView for dietary info

        // Retrieve the Recipe object passed through the intent
        val recipe = intent.getSerializableExtra("RECIPE_DATA") as? Recipe

        recipe?.let {
            recipeTitleTextView.text = it.name
            recipeStepsTextView.text = it.instructions
            dietaryInfoTextView.text = it.tags.joinToString(", ") // Join tags with comma
            selectedIngredients = BooleanArray(it.ingredients.size)
        }

        val ingredientsButton: Button = findViewById(R.id.ingredients_button)
        ingredientsButton.setOnClickListener {
            recipe?.let {
                showIngredientsDialog(it.ingredients)
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

    private fun showIngredientsDialog(ingredients: List<String>) {
        // Convert the ingredients list to an array for the AlertDialog
        val ingredientsArray = ingredients.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Add to Shopping List") // Updated title text
            .setMultiChoiceItems(ingredientsArray, selectedIngredients) { _, which, isChecked ->
                // Update the selected items
                selectedIngredients[which] = isChecked
            }
            .setPositiveButton("Save") { dialog, _ -> // Updated button text
                dialog.dismiss()
                // Implement your save logic here
            }
            .setNegativeButton("Cancel") { dialog, _ -> // Button text is already 'Cancel'
                dialog.dismiss()
            }
            .create()
            .show()
    }
}



