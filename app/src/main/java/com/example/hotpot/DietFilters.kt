package com.example.hotpot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class DietFilters : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diet_filter, container, false)

        // Setup the return to home button
        val returnToHomeButton = view.findViewById<ImageView>(R.id.returnToHomeButton)
        returnToHomeButton.setOnClickListener {
            // Navigate back to MainActivity
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        // Setup the save button
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            Toast.makeText(requireActivity(), "Changes saved successfully", Toast.LENGTH_SHORT).show()
            // Additional actions can be added here
        }

        return view
    }
}
