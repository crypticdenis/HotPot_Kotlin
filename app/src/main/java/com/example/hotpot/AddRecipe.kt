package com.example.hotpot

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Double.max
import kotlin.math.max

class AddRecipe : AppCompatActivity() {
    private lateinit var addRecipeStepsBtn: Button
    private lateinit var recipeStepEditText: EditText
    private lateinit var scrollViewParent: NestedScrollView
    private lateinit var dietaryFilterContainer: LinearLayout
    val dietaryFilterOptions = arrayOf("vegan", "gluten", "halal", "keto",
        "kosher", "lactose", "paleo", "peanut", "pescatarian", "shellfish", "soy", "vegetarian")
    private var selectedFilters = mutableListOf<String>()
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        addRecipeStepsBtn = findViewById(R.id.addRecipeSteps_Btn)
        recipeStepEditText = findViewById(R.id.stepsEditText)
        scrollViewParent = findViewById(R.id.nestedScrollView)

        val recipeNameEditText: EditText = findViewById(R.id.AddRecipe_recipeName)
        val recipeStepEditText: EditText = findViewById(R.id.stepsEditText)

        recipeNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Den vorhandenen Text in der EditText löschen
                recipeNameEditText.setText("")
            }
        }

        val closeBtn = findViewById<ImageButton>(R.id.addRecipe_CloseBtn)
        closeBtn.setOnClickListener {
            finish()
        }

        val cancelBtn = findViewById<Button>(R.id.AddRecipe_CancelBtn)
        cancelBtn.setOnClickListener {
            finish()
        }

        val saveRecipeBtn = findViewById<Button>(R.id.AddRecipe_SaveBtn)
        saveRecipeBtn.setOnClickListener {
            uploadNewRecipe();
            finish()
        }

        addRecipeStepsBtn.setOnClickListener {
            // Hier wird der Button in ein EditText umgewandelt
            addRecipeStepsBtn.visibility = View.GONE
            recipeStepEditText.visibility = View.VISIBLE
        }

        // Hier kannst du den Code für die dynamischen CheckBoxes und Dietary Filter hinzufügen
        dietaryFilterContainer = findViewById(R.id.dietaryFilterContainer)

        val addDietaryFilterBtn: Button = findViewById(R.id.dietaryFilter_Btn)
        addDietaryFilterBtn.setOnClickListener {
                    showDietaryFilters();
                }

        scrollViewParent.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Überprüfe, ob der Klick außerhalb der EditTexts war
                if (recipeNameEditText.isFocused || recipeStepEditText.isFocused) {
                    val outRectName = android.graphics.Rect()
                    val outRectStep = android.graphics.Rect()

                    recipeNameEditText.getGlobalVisibleRect(outRectName)
                    recipeStepEditText.getGlobalVisibleRect(outRectStep)

                    // Reduziere die Größe des Rechtecks
                    val padding = 16 // Hier kannst du die Padding-Größe anpassen

                    outRectName.set(outRectName.left + padding, outRectName.top + padding, outRectName.right - padding, outRectName.bottom - padding)
                    outRectStep.set(outRectStep.left + padding, outRectStep.top + padding, outRectStep.right - padding, outRectStep.bottom - padding)

                    if (!outRectName.contains(event.rawX.toInt(), event.rawY.toInt()) &&
                        !outRectStep.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        // Der Klick war außerhalb der EditTexts, entferne den Fokus
                        recipeNameEditText.clearFocus()
                        recipeStepEditText.clearFocus()

                        // Verberge die Tastatur
                        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(scrollViewParent.windowToken, 0)
                    }
                }
            }
            false
        }
    }
    private fun showDietaryFilters() {
        val dietaryFilterArray = dietaryFilterOptions

        AlertDialog.Builder(this@AddRecipe)
            .setTitle("Recipe contains")
            .setMultiChoiceItems(dietaryFilterArray, null) { _, which, isChecked ->
                if (isChecked) {
                    selectedFilters.add(dietaryFilterOptions[which])
                } else {
                    selectedFilters.remove(dietaryFilterOptions[which])
                }
            }
            .setPositiveButton("Save") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    /**
     * add recipe button uploads recipe up to the database
     */
    private fun uploadNewRecipe() {
        // Namen aus dem EditText extrahieren
        val recipeNameEditText: EditText = findViewById(R.id.AddRecipe_recipeName)
        val recipeName = recipeNameEditText.text.toString()

        // Diätetische Filter aus den ausgewählten Filters extrahieren
        val selectedFilters = selectedFilters;

        // Schritte aus dem EditText extrahieren
        val stepsEditText: EditText = findViewById(R.id.stepsEditText)
        val steps = stepsEditText.text.toString()

        // Weitere Logik für die Zutaten hinzufügen, wenn du die Ingredient List später hinzufügen möchtest

        // Firebase-Referenz
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Recipes")

        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newRecipeId = 0

                if (snapshot.exists()) {
                    val lastRecipe = snapshot.children.first()
                    newRecipeId = lastRecipe.key?.toInt() ?: 0
                    newRecipeId++
                }

                // Neues Rezept erstellen
                val newRecipeReference = databaseReference.child(newRecipeId.toString())
                newRecipeReference.child("name").setValue(recipeName)
                newRecipeReference.child("instructions").setValue(steps)

                // Für jedes Tag in selectedFilters ein Verzeichnis erstellen
                for ((index, tag) in selectedFilters.withIndex()) {
                    newRecipeReference.child("tags").child(index.toString()).setValue(tag)
                }

                Log.d("Firebase", "Neues Rezept hochgeladen mit ID: $newRecipeId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Fehler beim Hochladen des Rezepts: ${error.message}")
            }
        })
    }
}

