package com.example.hotpot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hotpot.R
import android.widget.ImageView

class DietFilters : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diet_filter, container, false)

        val returnToHomeButton = view.findViewById<ImageView>(R.id.returnToHomeButton)
        returnToHomeButton.setOnClickListener {
            // Navigate back to MainActivity
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
