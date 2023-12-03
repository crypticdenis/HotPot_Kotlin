package com.example.hotpot

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        val signInTab = view.findViewById<Button>(R.id.register_signIn_tab);

        val editName : EditText = view.findViewById(R.id.signUpName);
        val editEmail: EditText = view.findViewById(R.id.signUpEmail);
        val editPassword: EditText = view.findViewById(R.id.signUpPassword);

        // setting checkmarks invisible
        editName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        editEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        editPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);


        signInTab.setOnClickListener {
                    (activity as? LoginActivity)?.showSignInFragment()
        }

        var signUpBtn = view.findViewById<Button>(R.id.signUp_signUp_btn);
        signUpBtn.setOnClickListener {
            if (correctName && correctEmail && correctPassword) {
                // Assuming you have an action defined in nav_graph.xml as action_signUp_to_dietFilters
                findNavController().navigate(R.id.action_signUp_to_dietFilters)
            } else {
                // Existing code for showing toast message
                Toast.makeText(requireContext(), "Invalid input. Try again", Toast.LENGTH_SHORT).show()
            }
        }


        /**
         * after text is changed, checks if it's empty
         */

        editName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that the characters within `s` are about to be replaced with new text with a length of `count`.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within `s`, the characters within `start` and `start + before` have been replaced with new text with a length of `count`.
            }

            override fun afterTextChanged(s: Editable?) {
                // Überprüfe, ob Text vorhanden und editName nicht ausgewählt ist
                if (!editName.isFocused && !s.isNullOrBlank()) {
                    editName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.checkmark, 0
                    )
                } else {
                    // Falls der Text leer ist oder editName ausgewählt ist, setze das Drawable auf null
                    editName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        })

        editEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that the characters within `s` are about to be replaced with new text with a length of `count`.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within `s`, the characters within `start` and `start + before` have been replaced with new text with a length of `count`.
            }

            override fun afterTextChanged(s: Editable?) {
                // Überprüfe, ob Text vorhanden und editEmail nicht ausgewählt ist
                if (!editEmail.isFocused && !s.isNullOrBlank()) {
                    editEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.checkmark, 0
                    )
                } else {
                    // Falls der Text leer ist oder editEmail ausgewählt ist, setze das Drawable auf null
                    editEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        })

        editPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that the characters within `s` are about to be replaced with new text with a length of `count`.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within `s`, the characters within `start` and `start + before` have been replaced with new text with a length of `count`.
            }

            override fun afterTextChanged(s: Editable?) {
                // Überprüfe, ob Text vorhanden und editPassword nicht ausgewählt ist
                if (!editPassword.isFocused && !s.isNullOrBlank()) {
                    editPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.checkmark, 0
                    )
                } else {
                    // Falls der Text leer ist oder editPassword ausgewählt ist, setze das Drawable auf null
                    editPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        })

        /**
         * setFocusChangeListener
         * sets the checkmark visible
         */
        editName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Wenn editName nicht mehr ausgewählt ist, überprüfe den Text und zeige das Checkmark an, wenn nötig
                val drawableEndVisibility = if (editName.text.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
                correctName = editName.text.isNotEmpty()
                editName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, if (drawableEndVisibility == View.VISIBLE) R.drawable.checkmark else 0, 0
                )
            }
        }
        editEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Wenn editName nicht mehr ausgewählt ist, überprüfe den Text und zeige das Checkmark an, wenn nötig
                val drawableEndVisibility = if (editEmail.text.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
                correctEmail = editEmail.text.isNotEmpty()
                editEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, if (drawableEndVisibility == View.VISIBLE) R.drawable.checkmark else 0, 0
                )
            }
        }
        editPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Wenn editName nicht mehr ausgewählt ist, überprüfe den Text und zeige das Checkmark an, wenn nötig
                val drawableEndVisibility = if (editPassword.text.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
                correctPassword = editPassword.text.isNotEmpty()
                editPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, if (drawableEndVisibility == View.VISIBLE) R.drawable.checkmark else 0, 0
                )
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
                if (editName.isFocused || editPassword.isFocused || editEmail.isFocused) {
                    val outRect = android.graphics.Rect()
                    editName.getGlobalVisibleRect(outRect)
                    editEmail.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        // Der Klick war außerhalb der EditTexts, entferne den Fokus
                        editName.clearFocus()
                        editEmail.clearFocus()
                        editPassword.clearFocus()
                    }
                }
            }
            false
        }
        return view
    }
}