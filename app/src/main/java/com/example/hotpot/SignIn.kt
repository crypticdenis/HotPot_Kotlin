package com.example.hotpot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignIn.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignIn : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        val signUpTab = view.findViewById<Button>(R.id.login_signUp_tab)

        val editEmail = view.findViewById<EditText>(R.id.signInEmailAddress)
        val editPassword = view.findViewById<EditText>(R.id.signInPassword);

        var correctEmail : Boolean = false;
        var correctPassword : Boolean = false;

        signUpTab.setOnClickListener {
            // Navigate to the SignUp fragment
            (activity as? LoginActivity)?.showSignUpFragment()
        }

        val signInBtn = view.findViewById<Button>(R.id.signIn_signIn_Btn);

        signInBtn.setOnClickListener {
            if(correctEmail && correctPassword) {
                // change activity to main screen
                val intent = Intent(requireContext(), MainActivity::class.java);
                startActivity(intent);
            } else {
                val toast = Toast.makeText(requireContext(), "Invalid input. Try again", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        editEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Wenn editName nicht mehr ausgewählt ist, überprüfe den Text und zeige das Checkmark an, wenn nötig
                correctEmail = editEmail.text.isNotEmpty()
            }
        }
        editPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Wenn editName nicht mehr ausgewählt ist, überprüfe den Text und zeige das Checkmark an, wenn nötig
                correctPassword = editPassword.text.isNotEmpty()
            }
        }


        /**
         * if touched outside of the editTexts
         * set the editTexts not being focused anymore
         * this triggers onFocusChange
         */
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Überprüfe, ob der Klick außerhalb der EditTexts war
                if (editEmail.isFocused || editPassword.isFocused) {
                    val outRect = android.graphics.Rect()
                    editEmail.getGlobalVisibleRect(outRect)
                    editPassword.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        // Der Klick war außerhalb der EditTexts, entferne den Fokus
                        editEmail.clearFocus()
                        editPassword.clearFocus()
                    }
                }
            }
            false
        }
        return view;
    }
}