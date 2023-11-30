package com.example.hotpot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    //val horizontalLayout = findViewById<LinearLayout>(R.id.userStoriesContainer)

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

        //addUserStory();
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