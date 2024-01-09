import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.hotpot.R
import com.example.hotpot.Recipe
import androidx.appcompat.app.AlertDialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("DEPRECATION")
class RecipeDetailsFragment : BottomSheetDialogFragment() {

    private lateinit var selectedIngredients: BooleanArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_details, container, false)

        val recipeTitleTextView: TextView = view.findViewById(R.id.recipe_title)
        val recipeStepsTextView: TextView = view.findViewById(R.id.recipe_steps)
        val dietaryInfoTextView: TextView = view.findViewById(R.id.dietary_info)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionInfo)

        val recipe = arguments?.getSerializable("RECIPE_DATA") as? Recipe

        recipe?.let {
            recipeTitleTextView.text = it.name
            recipeStepsTextView.text = it.instructions
            descriptionTextView.text = it.description
            dietaryInfoTextView.text = it.tags.joinToString(", ")
            selectedIngredients = BooleanArray(it.ingredients.size)
        }

        val ingredientsButton: Button = view.findViewById(R.id.ingredients_button)
        ingredientsButton.setOnClickListener {
            recipe?.let {
                showIngredientsDialog(it.ingredients)
            }
        }

        val closeRecipeDetailsBtn: ImageButton = view.findViewById(R.id.closeRecipeDetailsBtn)
        closeRecipeDetailsBtn.setOnClickListener {
            Log.d("Fragment", "trying to close")
            dismiss()
        }

        return view
    }

    private fun showIngredientsDialog(ingredients: Map<String, Any>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val ingredientEntries = ingredients.entries.toTypedArray()

            AlertDialog.Builder(requireContext())
                .setTitle("Add to Shopping List")
                .setMultiChoiceItems(ingredientEntries.map { "${it.key}: ${it.value}" }
                    .toTypedArray(), selectedIngredients) { _, which, isChecked ->
                    selectedIngredients[which] = isChecked
                }
                .setPositiveButton("Save") { dialog, _ ->
                    saveIngredientsToShoppingList(userId, selectedIngredients, ingredientEntries)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Add All") { _, _ ->
                    selectAllIngredientsAndSave(userId, selectedIngredients, ingredientEntries)
                }
                .create()
                .show()
        }
    }

    private fun selectAllIngredients(size: Int) {
        for (i in 0 until size) {
            selectedIngredients[i] = true
        }
    }
    private fun selectAllIngredientsAndSave(userId: String, selectedIngredients: BooleanArray, ingredientEntries: Array<Map.Entry<String, Any>>) {
        selectAllIngredients(ingredientEntries.size)
        saveIngredientsToShoppingList(userId, selectedIngredients, ingredientEntries)
    }



    private fun saveIngredientsToShoppingList(userId: String, selectedIngredients: BooleanArray, ingredientEntries: Array<Map.Entry<String, Any>>) {
        val shoppingListRef = FirebaseDatabase.getInstance().getReference("Users/$userId/ShoppingList")

        for (i in selectedIngredients.indices) {
            if (selectedIngredients[i]) {
                val ingredientEntry = ingredientEntries[i]
                val ingredientName = ingredientEntry.key
                val ingredientValue = ingredientEntry.value as? Map<*, *> ?: continue

                // Überprüfung, ob das Ingredient bereits existiert
                shoppingListRef.child(ingredientName).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Ingredient existiert bereits, Mengen addieren oder aktualisieren
                            for ((unit, amount) in ingredientValue) {
                                val existingData = snapshot.child(unit.toString()).value as? Number ?: continue
                                val existingAmount = existingData.toDouble()
                                val newAmount = amount as? Number ?: continue
                                val totalAmount = existingAmount + newAmount.toDouble()

                                // Menge addieren oder aktualisieren
                                shoppingListRef.child(ingredientName).child(unit.toString()).setValue(totalAmount)
                            }
                        } else {
                            // Ingredient existiert nicht, einfach speichern
                            for ((unit, amount) in ingredientValue) {
                                shoppingListRef.child(ingredientName).child(unit.toString()).setValue(amount)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle onCancelled if needed
                    }
                })
            }
        }
        // Optional: You may want to show a confirmation message or handle the success in some way.
        Toast.makeText(requireContext(), "Ingredients added to ShoppingList", Toast.LENGTH_SHORT).show()
    }
}

