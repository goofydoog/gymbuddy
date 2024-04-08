package com.example.gymbuddy

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        val arrowImageView = view.findViewById<ImageView>(R.id.imageView5)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)
        val emailEditText = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameEditText)

        val regButton = view.findViewById<Button>(R.id.registerButtonRegFrag)
        regButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dull_color))
        regButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.faded_text_color))

        regButton.setOnClickListener {
            val email: String = emailEditText.text.toString().trim()
            val password: String = passwordEditText.text.toString()
            val username: String = usernameEditText.text.toString()

            val isLengthMet = password.length >= 8
            val isLowercaseMet = password.any { it.isLowerCase() }
            val isUppercaseMet = password.any { it.isUpperCase() }
            val isNumberMet = password.any { it.isDigit() }
            val isSpecialCharMet = password.any { !it.isLetterOrDigit() }

            val areAllConditionsMet = isLengthMet && isLowercaseMet && isUppercaseMet && isNumberMet && isSpecialCharMet


            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && areAllConditionsMet) {
                // Only proceed with Firebase if both fields are filled
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Account created.", Toast.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                (activity as? MainActivity)?.navigateToLogin()
                            }, 1000)
                        } else {
                            Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
            }
        }


        val conditionLength = view.findViewById<TextView>(R.id.conditionLength)
        val conditionLowercase = view.findViewById<TextView>(R.id.conditionLowercase)
        val conditionUppercase = view.findViewById<TextView>(R.id.conditionUppercase)
        val conditionNumber = view.findViewById<TextView>(R.id.conditionNumber)
        val conditionSpecialChar = view.findViewById<TextView>(R.id.conditionSpecialChar)
        val shouldContainTextView = view.findViewById<TextView>(R.id.shouldcontain)


        arrowImageView.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = passwordEditText.text.toString()
                val email = emailEditText.text.toString()
                val username = usernameEditText.text.toString()

                val isLengthMet = password.length >= 8
                val isLowercaseMet = password.any { it.isLowerCase() }
                val isUppercaseMet = password.any { it.isUpperCase() }
                val isNumberMet = password.any { it.isDigit() }
                val isSpecialCharMet = password.any { !it.isLetterOrDigit() }

                val areAllConditionsMet = isLengthMet && isLowercaseMet && isUppercaseMet && isNumberMet && isSpecialCharMet
                shouldContainTextView.setTextColor(getColorBasedOnCondition(areAllConditionsMet))
                conditionLength.setTextColor(getColorBasedOnCondition(isLengthMet))
                conditionLowercase.setTextColor(getColorBasedOnCondition(isLowercaseMet))
                conditionUppercase.setTextColor(getColorBasedOnCondition(isUppercaseMet))
                conditionNumber.setTextColor(getColorBasedOnCondition(isNumberMet))
                conditionSpecialChar.setTextColor(getColorBasedOnCondition(isSpecialCharMet))

                val isFormComplete = email.isNotEmpty() && username.isNotEmpty() && areAllConditionsMet
                updateNextButtonState(isFormComplete, regButton)
            }
        }

        passwordEditText.addTextChangedListener(textWatcher)
        emailEditText.addTextChangedListener(textWatcher)
        usernameEditText.addTextChangedListener(textWatcher)
    }

    private fun updateNextButtonState(isFormComplete: Boolean, button: Button) {
        if (isFormComplete) {
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.original_color))
            button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        } else {
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dull_color))
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.faded_text_color))
        }
    }

    private fun getColorBasedOnCondition(condition: Boolean): Int {
        return if (condition) {
            ContextCompat.getColor(requireContext(), R.color.line_color_met)
        } else {
            ContextCompat.getColor(requireContext(), R.color.line_color_default)
        }
    }

}
