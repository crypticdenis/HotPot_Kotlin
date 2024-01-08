import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.hotpot.R
import com.example.hotpot.Recipe
import androidx.appcompat.app.AlertDialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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
        val descriptionTextView : TextView = view.findViewById(R.id.descriptionInfo)

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
        val ingredientEntries = ingredients.entries.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Add to Shopping List")
            .setMultiChoiceItems(ingredientEntries.map { "${it.key}: ${it.value}" }.toTypedArray(), selectedIngredients) { _, which, isChecked ->
                selectedIngredients[which] = isChecked
            }
            .setPositiveButton("Save") { dialog, _ ->
                dialog.dismiss()
                // TODO: save ingredient list to shopping list
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
