package com.example.gymbuddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class StartingScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_starting_screen, container, false)

        view.findViewById<Button>(R.id.logInButton).setOnClickListener {
            (activity as? MainActivity)?.navigateToLogin()
        }

        view.findViewById<Button>(R.id.registerButton).setOnClickListener {
            (activity as? MainActivity)?.navigateToRegister()
        }

        return view
    }


}
