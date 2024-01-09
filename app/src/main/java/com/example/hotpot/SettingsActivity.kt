package com.example.hotpot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



interface OnSettingsItemClickListener {
    fun onItemClick(item: SettingItem)
}

interface OnFragmentInteractionListener {
    fun onCloseFragment()
}
class SettingsActivity : AppCompatActivity(), OnSettingsItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fragmentContainer: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val customToolbar = findViewById<Toolbar>(R.id.toolbar_custom)
        setSupportActionBar(customToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        customToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.settings_recycler_view)
        fragmentContainer = findViewById(R.id.fragment_container)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SettingsAdapter(listOf(
            SettingItem(R.drawable.me, "Account"),
            SettingItem(R.drawable.fridge_button_icon, "What's in my fridge?"),
            SettingItem(R.drawable.hotpot_icon, "Diet Filter"),
            SettingItem(R.drawable.shutdown, "Logout")
        ), this)
    }

    override fun onItemClick(item: SettingItem) {
        when (item.title) {
            "Logout" -> startLogoutActivity()
            "Diet Filter" -> openDietFiltersFragment()
            "What's in my fridge?" ->startFridgeActivity();
            // Handle other items if needed

        }
    }

    fun onCloseFragment() {
        finish();
    }

    private fun startFridgeActivity() {
        val intent = Intent(this, IngredientsList::class.java)
        startActivity(intent)
    }

    private fun openDietFiltersFragment() {
        val fragment = DietFilters() // Replace with your actual fragment initialization
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        fragmentContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun startLogoutActivity() {
        AlertDialog.Builder(this)
            .setTitle("Log Out") // Set the title for the dialog
            .setMessage("Are you sure you want to log out?") // Set the message to show in the dialog
            .setPositiveButton("Yes") { dialog, which ->
                // This block will be executed if the user clicks "Yes"
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, which ->
                // This block will be executed if the user clicks "No"
                dialog.dismiss() // Close the dialog
            }
            .show() // Display the dialog
    }
    override fun onBackPressed() {
        if (fragmentContainer.visibility == View.VISIBLE) {
            super.onBackPressed()
            recyclerView.visibility = View.VISIBLE
            fragmentContainer.visibility = View.GONE
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()  // Ensure this activity is removed from the back stack
        }
    }


}

