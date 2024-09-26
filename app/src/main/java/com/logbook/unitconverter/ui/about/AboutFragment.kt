package com.logbook.unitconverter.ui.about

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.logbook.unitconverter.R
import com.logbook.unitconverter.databinding.FragmentAboutBinding
import com.logbook.unitconverter.ui.FontSizeAware

class AboutFragment : Fragment(), FontSizeAware {

    private var fontSize: Float = 16f
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Attempt to retrieve the font size from SharedPreferences
        try {
            val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val selectedFontSizeIndex = sharedPreferences.getInt("FONT_SIZE_INDEX", 1)

            // Set font size based on the retrieved index
            fontSize = when (selectedFontSizeIndex) {
                0 -> resources.getDimension(R.dimen.font_size_small)
                1 -> resources.getDimension(R.dimen.font_size_medium)
                2 -> resources.getDimension(R.dimen.font_size_large)
                else -> resources.getDimension(R.dimen.font_size_medium)
            }
        } catch (e: Exception) {
            // Log the exception and use a default font size
            e.printStackTrace()
            fontSize = resources.getDimension(R.dimen.font_size_medium)
        }

        // Inflate the layout and handle any potential exceptions
        return try {
            _binding = FragmentAboutBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            // Log the exception and return an empty view to prevent crashes
            e.printStackTrace()
            View(requireContext()) // Return an empty view
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the font size for the UI elements
        setFontSize(fontSize)

        // Attempt to set the text and images for the UI elements
        try {
            binding.appNameTextView.text = getString(R.string.app_name)
            binding.appDescriptionTextView.text = getString(R.string.app_description)
            binding.developerInfoTextView.text = getString(R.string.developer_info)
            binding.versionTextView.text = getString(R.string.app_version)
            binding.applicationIconView.setImageResource(R.mipmap.ic_launcher)
        } catch (e: Exception) {
            // Handle any exceptions when setting text or images
            e.printStackTrace()
        }
    }

    override fun setFontSize(size: Float) {
        fontSize = size // Update the font size and apply it
        applyFontSize()
    }

    private fun applyFontSize() {
        // Attempt to apply the font size to the UI elements
        try {
            binding.appNameTextView.textSize = fontSize
            binding.appDescriptionTextView.textSize = fontSize
            binding.developerInfoTextView.textSize = fontSize
            binding.versionTextView.textSize = fontSize
        } catch (e: Exception) {
            // Handle any exceptions when applying font sizes
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to prevent memory leaks
    }
}
