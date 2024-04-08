package com.example.gymbuddy

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class HomeMain : AppCompatActivity(){

    private lateinit var fab: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_main)
        hideSystemUI()

        // Ustawienie nasłuchiwania zmiany widoczności UI systemu
        val contentView = findViewById<View>(R.id.drawer_layout)
        contentView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fab = findViewById(R.id.fab)
        drawerLayout = findViewById(R.id.drawer_layout)

        // Ustawienie paska narzędziowego
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Konfiguracja przełącznika ActionBarDrawer
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Ustawienie domyślnego fragmentu
        if(savedInstanceState == null) {
            replaceFragment(HomeFragment1())
        }

        // Obsługa zdarzeń kliknięcia w dolnej nawigacji
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment1())
                R.id.shorts -> replaceFragment(TikTokFragment())
                R.id.planworkout -> replaceFragment(PlanWorkOutFragment())
                R.id.library -> replaceFragment(ExerciseFragment())
            }
            true
        }

        // Obsługa kliknięcia przycisku FAB
        fab.setOnClickListener {
            showBottomDialog() // Wyświetlenie dolnego okna dialogowego
        }

        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Obsługa przycisku wstecz - zamknięcie menu bocznego jeśli jest otwarte
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        // Metoda do wymiany fragmentów w kontenerze
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun showBottomDialog() {
        // Tworzenie i konfiguracja dolnego okna dialogowego
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        // Inicjalizacja elementów UI w dialogu
        val videoLayout: LinearLayout = dialog.findViewById(R.id.layoutVideo)
        val shortsLayout: LinearLayout = dialog.findViewById(R.id.layoutShorts)
        val logoutLayout: LinearLayout = dialog.findViewById(R.id.layoutLogOut)
        val cancelButton: ImageView = dialog.findViewById(R.id.cancelButton)

        videoLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Upload a Video is clicked", Toast.LENGTH_SHORT).show()
        }

        shortsLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Create a TikTok is Clicked", Toast.LENGTH_SHORT).show()
        }

        logoutLayout.setOnClickListener {
            // Wylogowanie użytkownika i powrót do ekranu głównego
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            navigateBackToMain()
        }

        cancelButton.setOnClickListener { dialog.dismiss() } // Zamknięcie dialogu

        // Wyświetlenie dialogu
        dialog.show()
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI() // Ukrycie UI systemowego przy powrocie do aplikacji
    }

    private fun navigateBackToMain() {
        // Powrót do ekranu głównego po wylogowaniu
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
