import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.hotpot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FridgeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hier kannst du auf die Ansichten in deinem Fragment zugreifen
        val linearLayout: LinearLayout = view.findViewById(R.id.fridgeLinearLayoutList)

        val databaseReference = FirebaseDatabase.getInstance().reference    // Get root reference

        // Assuming you have FirebaseAuth instance
        val auth = FirebaseAuth.getInstance()

        // Check if a user is currently signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // The UID of the currently logged-in user
            val userUID = currentUser.uid

            Log.i("Currently logged in as ", userUID)

            // Now you can use this userUID in your function
            createObjectsInFridge(databaseReference, userUID)
        }

        // Assuming you have a reference to the old activity layout
        val ingredientListLayout = activity?.findViewById<LinearLayout>(R.id.ingredientListLayout)



        val backButton = view.findViewById<ImageView>(R.id.backButtonFridge)
        backButton?.setOnClickListener {
            // Set the visibility of the old activity layout back to visible
            ingredientListLayout?.visibility = View.VISIBLE

            // Pop the fragment from the back stack
            requireActivity().supportFragmentManager.popBackStack()
        }
    }



    fun createObjectsInFridge(databaseReference: DatabaseReference, userUID: String) {
        val userFridgeReference = databaseReference.child("Users").child(userUID).child("Fridge")

        userFridgeReference.get().addOnSuccessListener { fridgeSnapshot ->
            val categoryContentLayout = view?.findViewById<LinearLayout>(R.id.fridgeContentLayout)
            view?.findViewById<SearchView>(R.id.searchView)?.visibility = View.VISIBLE

            for (categorySnapshot in fridgeSnapshot.children) {
                // Iterate through categories (Meat, Vegetables)
                for (itemSnapshot in categorySnapshot.children) {
                    // Iterate through items (Chicken, Beef, etc.)
                    val itemName = itemSnapshot.key.toString()

                    for (unitSnapshot in itemSnapshot.children) {
                        // Iterate through units (Gram)
                        val unitName = unitSnapshot.key.toString()

                        for (amountSnapshot in unitSnapshot.children) {
                            // Iterate through amounts (400, etc.)
                            val amountValue = amountSnapshot.key.toString()

                            // Now you have all the information to create your UI elements
                            createUIElement(itemName, unitName, amountValue)
                        }
                    }
                }
            }
        }
    }


    fun createUIElement(name: String, unit: String, amount: String) {
        val fridgeContentLayout = view?.findViewById<LinearLayout>(R.id.fridgeContentLayout)
        val linearLayoutHorizontal = LinearLayout(requireContext())
        linearLayoutHorizontal.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL

        // Create item text (left-aligned)
        val textView = TextView(requireContext())
        val textParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f // weight for left-aligned TextView
        )
        textView.layoutParams = textParams
        textView.text = name
        textView.setTextSize(25F)
        textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.abeezee)
        textView.setPadding(0, 13, 0, 13)
        linearLayoutHorizontal.addView(textView)

        // Create amount text (center-aligned)
        val amountView = TextView(requireContext())
        val amountParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0.5f
        )
        amountView.layoutParams = amountParams
        amountView.text = amount

        when (unit) {
            "Gram" -> amountView.text = "${amountView.text}g"
            "Milliliter" -> amountView.text = "${amountView.text}ml"
            "Piece" -> amountView.text = "${amountView.text}x"
            "Teaspoon" -> amountView.text = "${amountView.text}Tsp"
            "Tablespoon" -> amountView.text = "${amountView.text}tbsp"
            "Cup" -> amountView.text = "${amountView.text}c"
            "Ounce" -> amountView.text = "${amountView.text}oz"
            "Pound" -> amountView.text = "${amountView.text}lb"
            "Liter" -> amountView.text = "${amountView.text}L"
            "Fluid Ounce" -> amountView.text = "${amountView.text}fl oz"
            "Quart" -> amountView.text = "${amountView.text}qt"
            "Gallon" -> amountView.text = "${amountView.text}gal"
            // Add more units as needed
            else -> { /* Handle other cases if needed */ }
        }

        amountView.setTextSize(25F)
        amountView.typeface = ResourcesCompat.getFont(requireContext(), R.font.abeezee)
        amountView.setPadding(0, 13, 0, 13)
        linearLayoutHorizontal.addView(amountView)

        // Create add button (right-aligned)
        val addButton = ImageView(requireContext())
        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        addButton.layoutParams = buttonParams
        addButton.setImageResource(R.drawable.plus_button)
        addButton.setPadding(12, 12, 12, 12)
        linearLayoutHorizontal.addView(addButton)

        fridgeContentLayout?.addView(linearLayoutHorizontal)
    }


}
