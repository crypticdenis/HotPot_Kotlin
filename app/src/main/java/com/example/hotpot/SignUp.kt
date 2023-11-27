package com.example.hotpot

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUp : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var correctName: Boolean = false
    var correctEmail: Boolean = false
    var correctPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // resetting if Fragment gets called again
        correctName = false
        correctEmail = false
        correctPassword = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        val signInTab = view.findViewById<Button>(R.id.register_signIn_tab);



        val editName : EditText = view.findViewById(R.id.editTextTextName);
        val editEmail: EditText = view.findViewById(R.id.editTextTextEmailAddress);
        val editPassword: EditText = view.findViewById(R.id.editTextTextPassword);

        // setting checkmarks invisible
        editName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        editEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        editPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);


        signInTab.setOnClickListener {
            checkEditTexts(editName, editEmail, editPassword)
                    (activity as? LoginActivity)?.showSignInFragment()
        }

        var signUpBtn = view.findViewById<Button>(R.id.signUp_signUp_btn);
        signUpBtn.setOnClickListener() {
            // check if everything is filled and maybe valid
            checkEditTexts(editName, editEmail, editPassword)
            // either switch to sign in or error message
                //(activity as? LoginActivity)?.showSignInFragment()
                Toast.makeText(requireContext(), "Invalid input. Try again", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private fun checkEditTexts(editName : EditText, editMail : EditText, editPassword : EditText ) {
        if(editName.text != null)
            correctName = true
        if(editMail.text != null)
            correctEmail = true;
        if(editPassword.text != null)
            correctPassword = true;
    }
}