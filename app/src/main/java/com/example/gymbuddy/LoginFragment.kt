package com.example.gymbuddy

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        val arrowImageView = view.findViewById<ImageView>(R.id.imageView5)
        val logInButton = view.findViewById<Button>(R.id.loginButton)

        val forgotPasswordTextView = view.findViewById<TextView>(R.id.forgotPassword)
        forgotPasswordTextView.setOnClickListener {
            showResetPasswordDialog()
        }
        val emailEditText = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)


        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateButtonState(emailEditText, passwordEditText, logInButton)
            }
        })

        // Initial button state update
        updateButtonState(emailEditText, passwordEditText, logInButton)

        arrowImageView.setOnClickListener {
            // Replace the current fragment with a new instance of StartingScreenFragment
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainer, StartingScreenFragment())
                ?.commit()
        }



        logInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Login successful.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, HomeMain::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(requireContext(), "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showResetPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val emailEditText = view.findViewById<EditText>(R.id.emailBox)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnReset = view.findViewById<Button>(R.id.btnReset)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        btnReset.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            sendPasswordResetEmail(email)
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Reset link sent to your email.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to send reset email.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateButtonState(emailEditText: TextInputEditText, passwordEditText: TextInputEditText, logInButton: Button) {
        val isEmailEmpty = emailEditText.text.isNullOrEmpty()
        val isPasswordEmpty = passwordEditText.text.isNullOrEmpty()
        val isPasswordValid = (passwordEditText.text?.length ?: 0) >= 8

        if (!isEmailEmpty && !isPasswordEmpty && isPasswordValid) {
            logInButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.original_color))
            logInButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            logInButton.isEnabled = true
        } else {
            logInButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dull_color))
            logInButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.faded_text_color))
            logInButton.isEnabled = false
        }
    }

}

