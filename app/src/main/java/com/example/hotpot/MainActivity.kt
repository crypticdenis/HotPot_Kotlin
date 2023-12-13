package com.example.hotpot

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    //val horizontalLayout = findViewById<LinearLayout>(R.id.userStoriesContainer)

    /**
     * navigation bar
     * toolbar for navigation bar
     */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navBar: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    /**
     * reference to the horizontalLayout that contains the userStories
     * having an array to add new stories
     * update the layout with array contents
     */

    /*
    val userStories = arrayOf(
        // add here fragment
        // create fragments that are built like in Figma

    )
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)
        FirebaseApp.initializeApp(this)


        //addUserStory();

        val toolbar: Toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_Layout)
        navBar = findViewById(R.id.nav_View)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navBar.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.shoppingList_btn)
                Toast.makeText(
                this@MainActivity, "Home Selected", Toast.LENGTH_SHORT).show()
            if (item.itemId == R.id.savedRecipes_btn)
                Toast.makeText(
                this@MainActivity, "Contact Selected", Toast.LENGTH_SHORT).show()
            if (item.itemId == R.id.addFriends_btn)
                Toast.makeText(
                    this@MainActivity, "Gallery Selected", Toast.LENGTH_SHORT).show()
            if (item.itemId == R.id.settings_btn)
                Toast.makeText(
                    this@MainActivity, "About Selected", Toast.LENGTH_SHORT).show()
            false
        })
    }

    private fun setupDrawerMenu() {
        val menu = navBar.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val spannableString = SpannableString(menuItem.title)
            spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.hotpot_light_green)), 0, spannableString.length, 0)
            spannableString.setSpan(AbsoluteSizeSpan(25, true), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            spannableString.setSpan(TypefaceSpan("abeezee"), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            menuItem.title = spannableString
        }
    }

    private fun addUserStory() {
        /*
        for (resourceId in userStories) {

            val imageFragment = ImageFragment.newInstance(resourceId)
            supportFragmentManager.beginTransaction()
                .add(horizontalLayout.id, imageFragment)
                .commit()
             */
        }

}