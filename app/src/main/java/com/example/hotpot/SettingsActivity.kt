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
            SettingItem(R.drawable.hotpot_icon, "Diet Filter"),
            SettingItem(R.drawable.shutdown, "Logout")
        ), this)
    }

    override fun onItemClick(item: SettingItem) {
        when (item.title) {
            "Logout" -> startLogoutActivity()
            "Diet Filter" -> openDietFiltersFragment()
            // Handle other items if needed
        }
    }

    private fun openDietFiltersFragment() {
        val fragment = DietFilters() // Replace with your actual fragment initialization
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        recyclerView.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
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

