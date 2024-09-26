package com.logbook.unitconverter

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.logbook.unitconverter.databinding.ActivityMainBinding
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigationrail.NavigationRailView
import com.logbook.unitconverter.ui.FontSizeAware

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fontSizes: Array<String> by lazy {
        resources.getStringArray(R.array.font_sizes)
    }
    private var selectedFontSizeIndex = 1 // Default to Medium


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //showing dialog and then closing the application..
            showExitDialog()
        }
    }



    companion object {
        private const val PREFS_DARK_MODE = "DARK_MODE"
        private const val PREFS_FONT_SIZE_INDEX = "FONT_SIZE_INDEX"
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            applyFontSize()
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // adding onBackPressedCallback callback listener.
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        // Load the dark mode preference
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean(PREFS_DARK_MODE, false)
        setDarkMode(isDarkMode)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationRail: NavigationRailView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, _, _ ->
            applyFontSize()
        }


        // Setup NavigationRailView with item selection handling

        navigationRail.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_conversion -> {
                    navController.navigate(R.id.conversionFragment)
                    true
                }
                R.id.navigation_formula -> {
                    navController.navigate(R.id.formulaFragment)
                    true
                }
                R.id.navigation_about -> {
                    navController.navigate(R.id.aboutFragment)
                    true
                }
                else -> false
            }
        }

        // Load font size preference
        selectedFontSizeIndex = sharedPreferences.getInt(PREFS_FONT_SIZE_INDEX, 1) // Default to Medium
        applyFontSize()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        updateDarkModeMenuItem(menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_dark_mode -> {
                toggleDarkMode()
                true
            }
            R.id.action_font_size -> {
                showFontSizeDialog()
                true
            }

            R.id.action_exit -> {
                showExitDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showExitDialog() { // Create exit dialog that will be shown to user when user press back button or exit option in menu
        try {
            AlertDialog.Builder(this)
                .setTitle(R.string.confirm_exit_title)
                .setMessage(R.string.confirm_exit_message)
                .setPositiveButton(R.string.button_yes) { _, _ ->
                    finish() // Close the activity
                }
                .setNegativeButton(R.string.button_no) { dialog, _ ->
                    dialog.dismiss() // Dismiss the dialog
                }
                .create()
                .show()
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception
            Toast.makeText(this, getString(R.string.toast_error_showing_dialog), Toast.LENGTH_SHORT).show()
        }
    }


    private fun setDarkMode(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        })
    }

    private fun toggleDarkMode() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeEnabled = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        setDarkMode(!isDarkModeEnabled)
        saveThemePreference(!isDarkModeEnabled)
        recreate() // Recreate the activity to apply changes

        val message = if (isDarkModeEnabled) {
            getString(R.string.toast_light_mode)
        } else {
            getString(R.string.toast_dark_mode)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()) {
            putBoolean(PREFS_DARK_MODE, isDarkMode)
            apply()
        }
    }

    private fun showFontSizeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_select_font_size)
            .setSingleChoiceItems(fontSizes, selectedFontSizeIndex) { _, which ->
                selectedFontSizeIndex = which
            }
            .setPositiveButton(R.string.button_ok) { dialog, _ ->
                saveFontSizePreference(selectedFontSizeIndex)
                applyFontSize()
                dialog.dismiss()

                // Show toast message for font size change
                val fontSizeMessage = getString(R.string.toast_font_size_change, fontSizes[selectedFontSizeIndex])
                Toast.makeText(this, fontSizeMessage, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.button_cancel) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun saveFontSizePreference(index: Int) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()) {
            putInt(PREFS_FONT_SIZE_INDEX, index)
            apply()
        }
    }

    private fun applyFontSize() {
        val fontSize = when (selectedFontSizeIndex) { // Set font size based on the selected index
                0 -> resources.getDimension(R.dimen.font_size_small) // Small
                1 -> resources.getDimension(R.dimen.font_size_medium) // Medium
                2 -> resources.getDimension(R.dimen.font_size_large) // Large
                else -> resources.getDimension(R.dimen.font_size_medium) // Default
            }


        // Get the NavHostFragment and its child fragments
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val fragmentManager = navHostFragment.childFragmentManager

        // Iterate over the child fragments
        for (fragment in fragmentManager.fragments) {
            if (fragment is FontSizeAware) {
                fragment.setFontSize(fontSize)
            }
        }
    }


    private fun updateDarkModeMenuItem(menu: android.view.Menu?) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean(PREFS_DARK_MODE, false)
        menu?.findItem(R.id.action_dark_mode)?.setTitle(if (isDarkMode) {
            getString(R.string.menu_switch_to_light_mode)
        } else {
            getString(R.string.menu_switch_to_dark_mode)
        })
    }
}
