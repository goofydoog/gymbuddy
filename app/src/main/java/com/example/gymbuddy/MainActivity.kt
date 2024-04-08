package com.example.gymbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemUI()

        FirebaseApp.initializeApp(this)

        if (FirebaseAuth.getInstance().currentUser == null) {
            navigateToStartingScreen()
        } else {
            navigateToHomeMain()
        }

        val contentView = findViewById<View>(R.id.fragmentContainer)
        contentView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

    }



    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun navigateToStartingScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, StartingScreenFragment())
            .commit()
    }

    private fun navigateToHomeMain() {
        val intent = Intent(this, HomeMain::class.java)
        startActivity(intent)
        finish()
    }

    fun navigateToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }

    fun navigateToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

}
