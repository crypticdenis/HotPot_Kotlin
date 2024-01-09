package com.example.hotpot

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ShoppingListActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var linearLayout: LinearLayout

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        // Setup references to Firebase
        databaseReference = FirebaseDatabase.getInstance().reference
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            // Assuming you have a reference to the linear layout inside your ScrollView
            linearLayout = findViewById(R.id.shoppingListLayout)

            // Reference to the ShoppingList node for the current user
            val shoppingListReference = databaseReference.child("Users").child(userId).child("ShoppingList")

            // Retrieve selected ingredients from Firebase
            shoppingListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ingredientSnapshot in snapshot.children) {
                        val ingredientName = ingredientSnapshot.key.toString()

                        // Create UI elements and add them to ScrollView
                        for (unitSnapshot in ingredientSnapshot.children) {
                            val unitName = unitSnapshot.key.toString()
                            val amountValue = unitSnapshot.value.toString()
                            createUIElement(ingredientName, unitName, amountValue)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_list
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_home -> {
                    // switch to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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

    private fun createUIElement(name: String, unit: String, amount: String) {
        val linearLayoutHorizontal = LinearLayout(this)


        linearLayoutHorizontal.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL

        // Create item text (left-aligned)
        val textView = TextView(this)
        val textParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f // weight for left-aligned TextView
        )
        textView.layoutParams = textParams
        textView.text = name
        textView.setTextSize(25F)
        textView.typeface = ResourcesCompat.getFont(this, R.font.abeezee)
        textView.setPadding(0, 13, 0, 13)
        linearLayoutHorizontal.addView(textView)

        // Create amount text (center-aligned)
        val amountView = TextView(this)
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
            "Count" -> amountView.text = "${amountView.text}x"
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
        amountView.typeface = ResourcesCompat.getFont(this, R.font.abeezee)
        amountView.setPadding(0, 13, 0, 13)
        linearLayoutHorizontal.addView(amountView)

        // Create edit- and trash-button (right-aligned)
        val editButton = ImageView(this)
        val trashButton = ImageView(this)

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        editButton.layoutParams = buttonParams
        editButton.setImageResource(R.drawable.pen_button)
        editButton.setPadding(12, 12, 12, 12)
        linearLayoutHorizontal.addView(editButton)

        trashButton.layoutParams = buttonParams
        trashButton.setImageResource(R.drawable.trash_button)
        trashButton.setPadding(12, 12, 12, 12)
        linearLayoutHorizontal.addView(trashButton)

        editButton.setOnClickListener {
            Log.d("Buttons", "$name editButton clicked")

            // Create an AlertDialog
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Edit Quantity")

            // Create a LinearLayout to hold both EditText and Spinner
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            // Create EditText
            val editText = EditText(this)
            editText.gravity = Gravity.CENTER
            editText.hint = "Enter new quantity"
            linearLayout.addView(editText)

            // Create Spinner
            val unitSpinner = Spinner(this)
            unitSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getUnitOptions())

            // Set the default selection to the current unit
            val unitPosition = getUnitOptions().indexOf(unit)
            unitSpinner.setSelection(if (unitPosition != -1) unitPosition else 0)

            linearLayout.addView(unitSpinner)

            // Set the custom layout to the AlertDialog
            alertDialogBuilder.setView(linearLayout)

            alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                // User clicked Update
                val newQuantity = editText.text.toString()
                val selectedUnit = unitSpinner.selectedItem.toString()

                // Validate newQuantity (you might want to add additional validation)
                if (newQuantity.isNotBlank()) {
                    // Update the quantity in the Firebase database
                    updateQuantityInShoppingList(name, newQuantity, selectedUnit)
                } else {
                    Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                }
            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                // User clicked Cancel
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        trashButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("$name löschen")
            alertDialogBuilder.setMessage("Soll $name wirklich gelöscht werden?")

            alertDialogBuilder.setPositiveButton("Löschen") { _, _ ->
                // Benutzer hat "Löschen" ausgewählt
                Log.d("Buttons", "$name - Löschen bestätigt")

                deleteItemFromShoppingList(name)
            }

            alertDialogBuilder.setNegativeButton("Abbrechen") { dialog, _ ->
                // Benutzer hat "Abbrechen" ausgewählt
                Log.d("Buttons", "$name - Löschen abgebrochen")
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
        linearLayout?.addView(linearLayoutHorizontal)
    }

    private fun deleteItemFromShoppingList(itemName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userUID = currentUser.uid
            val shoppingListReference = databaseReference.child("Users").child(userUID).child("ShoppingList")

            shoppingListReference.child(itemName).removeValue()
                .addOnSuccessListener {
                    Log.d("Firebase", "Item $itemName deleted from ShoppingList")
                    removeUIElement(itemName)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error deleting item from ShoppingList: ${e.message}")
                }
        }
    }
    private fun removeUIElement(itemName: String) {
        // Find and remove the UI element from fridgeContentLayout
        val childCount = linearLayout.childCount
        for (i in 0 until childCount) {
            val child = linearLayout.getChildAt(i)
            if (child is LinearLayout) {
                val textView = child.getChildAt(0) as? TextView
                if (textView?.text == itemName) {
                    // Remove the UI element
                    linearLayout.removeView(child)
                    return
                }
            }
        }
    }

    private fun updateQuantityInShoppingList(itemName: String, newQuantity: String, selectedUnit: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userUID = currentUser.uid
            val shoppingListReference = databaseReference.child("Users").child(userUID).child("ShoppingList")

            val itemReference = shoppingListReference.child(itemName)

            // Überprüfe, ob das Ingredient in Firebase gefunden wurde
            itemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Lösche alle bestehenden Einheitsmengen
                        itemReference.removeValue().addOnCompleteListener { removeTask ->
                            if (removeTask.isSuccessful) {
                                // Füge die aktualisierte Einheitsmenge hinzu
                                itemReference.child(selectedUnit).setValue(newQuantity)
                                    .addOnCompleteListener { addTask ->
                                        if (addTask.isSuccessful) {
                                            Log.d("Firebase", "Quantity for $itemName updated to $newQuantity $selectedUnit")
                                            Toast.makeText(this@ShoppingListActivity, "Quantity updated", Toast.LENGTH_SHORT).show()

                                            // Update the UI with the new quantity and unit
                                            linearLayout.removeAllViews()
                                            // Re-create UI elements for the updated shopping list
                                            shoppingListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(newDataSnapshot: DataSnapshot) {
                                                    for (ingredientSnapshot in newDataSnapshot.children) {
                                                        val ingredientName = ingredientSnapshot.key.toString()

                                                        // Create UI elements and add them to ScrollView
                                                        for (unitSnapshot in ingredientSnapshot.children) {
                                                            val unitName = unitSnapshot.key.toString()
                                                            val amountValue = unitSnapshot.value.toString()
                                                            createUIElement(ingredientName, unitName, amountValue)
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    // Handle onCancelled if needed
                                                }
                                            })
                                        } else {
                                            Log.e("Firebase", "Error adding updated quantity: ${addTask.exception?.message}")
                                            Toast.makeText(this@ShoppingListActivity, "Error updating quantity", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Log.e("Firebase", "Error removing existing quantities: ${removeTask.exception?.message}")
                                Toast.makeText(this@ShoppingListActivity, "Error updating quantity", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.d("Firebase", "Item $itemName not found")
                        Toast.makeText(this@ShoppingListActivity, "Item not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled if needed
                    Log.e("Firebase", "Error retrieving item: ${error.message}")
                    Toast.makeText(this@ShoppingListActivity, "Error updating quantity", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }




    fun getUnitOptions(): List<String> {
        return listOf("Gram", "Milliliter", "Piece", "Count", "Teaspoon", "Tablespoon", "Cup", "Ounce", "Pound", "Liter", "Fluid Ounce", "Quart", "Gallon")
    }
}
