package com.logbook.unitconverter.ui.conversion

import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.logbook.unitconverter.R
import com.logbook.unitconverter.databinding.FragmentConversionBinding
import com.logbook.unitconverter.ui.FontSizeAware

class ConversionFragment : Fragment(), FontSizeAware {

    private var _binding: FragmentConversionBinding? = null
    private val binding get() = _binding!!

    private var fontSize: Float = 16f // Default font size

    private val viewModel: ConversionViewModel by viewModels()
    private lateinit var conversionResultAdapter: ConversionResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Attempt to retrieve font size from SharedPreferences
        try {
            val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val selectedFontSizeIndex = sharedPreferences.getInt("FONT_SIZE_INDEX", 1)

            // Set font size based on the selected index
            fontSize = when (selectedFontSizeIndex) {
                0 -> resources.getDimension(R.dimen.font_size_small)
                1 -> resources.getDimension(R.dimen.font_size_medium)
                2 -> resources.getDimension(R.dimen.font_size_large)
                else -> resources.getDimension(R.dimen.font_size_medium)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fontSize = resources.getDimension(R.dimen.font_size_medium) // Fallback to default size on error
        }

        // Inflate the layout and return the root view
        return try {
            _binding = FragmentConversionBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            e.printStackTrace()
            View(requireContext()) // Return an empty view if inflation fails
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup UI components
        setupSpinners(fontSize)
        setupRecyclerView()
        observeConvertedValues()
        setupInputListeners()
    }

    private fun setupSpinners(fontSize: Float) {
        // Setup spinners with unit options
        try {
            val units = resources.getStringArray(R.array.unit_array)
            binding.fromUnitSpinner.adapter = UnitSelectionAdapter(requireContext(), units, fontSize)
            binding.toUnitSpinner.adapter = UnitSelectionAdapter(requireContext(), units, fontSize)

            // Set default selection for spinners
            binding.fromUnitSpinner.setSelection(0)
            binding.toUnitSpinner.setSelection(2)
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors
        }
    }

    private fun setupRecyclerView() {
        // Initialize RecyclerView for displaying conversion results
        try {
            binding.conversionResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            conversionResultAdapter = ConversionResultAdapter(emptyList())
            binding.conversionResultsRecyclerView.adapter = conversionResultAdapter
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors
        }
    }

    private fun observeConvertedValues() {
        // Observe ViewModel for converted values
        viewModel.convertedValue.observe(viewLifecycleOwner) { convertedValues ->
            conversionResultAdapter.updateResults(convertedValues)
        }

        viewModel.outputValue.observe(viewLifecycleOwner) { outputValue ->
            binding.toValueField.isEnabled = true
            binding.toValueField.setText(outputValue) // Update output field with converted value
        }
    }

    private fun setupInputListeners() {
        // Set listener for input field focus change
        binding.fromValueField.setOnFocusChangeListener { _, _ -> onConvert() }

        binding.fromValueField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    onConvert() // Calling conversion method here
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), getString(R.string.toast_conversion_error), Toast.LENGTH_SHORT).show() // Notify user of conversion error
                    e.printStackTrace()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // Spinner selection listeners to trigger conversion
        binding.fromUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                onConvert() // Perform conversion when a unit is selected
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.toUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                try {
                    onConvert() // Calling conversion method here
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), getString(R.string.toast_conversion_error), Toast.LENGTH_SHORT).show() // Notify user of conversion error

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Copy button listener to copy conversion result to clipboard
        binding.copyButton.setOnClickListener {
            try {
                val clipboard = getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager // Using clipboard api to copy text
                val fromValue = binding.fromValueField.text.toString()
                val fromUnit = binding.fromUnitSpinner.selectedItem.toString()
                val toValue = binding.toValueField.text.toString()
                val toUnit = binding.toUnitSpinner.selectedItem.toString()

                // Create the string to copy
                val resultString = "$fromValue $fromUnit = $toValue $toUnit" // Generating text that is to be copied
                val clip: ClipData = ClipData.newPlainText("Conversion Result", resultString) // Putting text in clipboard

                clipboard.setPrimaryClip(clip) // Set the string to the clipboard

                // Show a toast message to inform the user
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { // Checking Android Version
                    Toast.makeText(requireContext(), getString(R.string.toast_copied), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), getString(R.string.toast_copy_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onConvert() {
        // Trigger conversion based on input value and selected units
        try {
            val inputValue = binding.fromValueField.text.toString()
            val fromUnit = binding.fromUnitSpinner.selectedItem.toString()
            val toUnit = binding.toUnitSpinner.selectedItem.toString()

            viewModel.setInputValue(inputValue)
            viewModel.setFromUnit(fromUnit)
            viewModel.setToUnit(toUnit)
            viewModel.convertLength() // Perform conversion in ViewModel
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), getString(R.string.toast_conversion_error), Toast.LENGTH_SHORT).show() // Notify user of conversion error
        }
    }

    override fun setFontSize(size: Float) {
        try {
            fontSize = size // Update the font size
            applyFontSize() // Apply the new font size
        } catch (e: Exception) {
            // Handle any exceptions that occur while setting the font size
            e.printStackTrace()
        }
    }

    private fun applyFontSize() {
        // Update font sizes for input fields and spinners
        try {
            binding.fromValueField.textSize = fontSize
            binding.toValueField.textSize = fontSize

            // Update the font size for spinner adapters
            (binding.fromUnitSpinner.adapter as? UnitSelectionAdapter)?.setFontSize(fontSize)
            (binding.toUnitSpinner.adapter as? UnitSelectionAdapter)?.setFontSize(fontSize)

            // Update the RecyclerView's adapter with the new font size
            conversionResultAdapter.setFontSize(fontSize)
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors
            Toast.makeText(requireContext(), getString(R.string.toast_font_size_failed_to_changed), Toast.LENGTH_SHORT).show() // Notify user of conversion error

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding to avoid memory leaks
    }
}
