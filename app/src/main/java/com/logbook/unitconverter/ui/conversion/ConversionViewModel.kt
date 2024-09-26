package com.logbook.unitconverter.ui.conversion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

/**
 * ViewModel for handling unit conversions.
 * This ViewModel stores input values, selected units, and the resulting converted values.
 */
class ConversionViewModel : ViewModel() {

    // LiveData to hold the input value as a String
    private val _inputValue = MutableLiveData("1")
    private val _outputValue = MutableLiveData("1")

    // LiveData to hold the selected unit to convert from
    private val _fromUnit = MutableLiveData<String>()

    // LiveData to hold the selected unit to convert to
    private val _toUnit = MutableLiveData<String>()

    // LiveData to hold the converted values in a Map format
    private val _convertedValue = MutableLiveData<Map<String, String>>()

    // Exposed LiveData for the converted values to observe from UI
    val convertedValue: LiveData<Map<String, String>> get() = _convertedValue

    // Exposed LiveData for the output values to observe from UI
    val outputValue: LiveData<String> get() = _outputValue

    // Initialization block to set default units if needed
    init {
        _fromUnit.value = "Metre" // Default from unit
        _toUnit.value = "Mile"   // Default to unit
        _convertedValue.value = emptyMap() // Initialize converted values
    }

    /**
     * Sets the input value for conversion.
     * @param value The value to be converted as a String.
     */
    fun setInputValue(value: String) {
        try {
            _inputValue.value = value
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during setting input value
        }
    }

    /**
     * Sets the unit to convert from.
     * @param unit The unit to convert from as a String.
     */
    fun setFromUnit(unit: String) {
        try {
            _fromUnit.value = unit
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during setting from unit
        }
    }

    /**
     * Sets the unit to convert to.
     * @param unit The unit to convert to as a String.
     */
    fun setToUnit(unit: String) {
        try {
            _toUnit.value = unit
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during setting to unit
        }
    }

    /**
     * Converts the input length value from the specified 'from' unit to all supported units.
     * Updates the _convertedValue LiveData with the results.
     */
    fun convertLength() {
        try {
            // Convert the input value to a Double, return if it's null
            val numberValue = _inputValue.value?.toDoubleOrNull() ?: return
            // Get the unit to convert from, return if it's null
            val fromUnitValue = _fromUnit.value ?: return

            // Convert the input value to meters based on the from unit
            val meters = when (fromUnitValue) {
                "Metre" -> numberValue
                "Millimetre" -> numberValue / 1000
                "Mile" -> numberValue * 1609.34
                "Foot" -> numberValue * 0.3048
                else -> return // Return if the unit is unsupported
            }

            // Create a map of converted values
            val locale: Locale = Locale.US
            val convertedValues = mutableMapOf<String, String>().apply {
                put("Metre", String.format(locale,"%.2f", meters))
                put("Millimetre", String.format(locale,"%.2f", meters * 1000))
                put("Mile", String.format(locale,"%.6f", meters / 1609.34))
                put("Foot", String.format(locale,"%.2f", meters / 0.3048))
            }

            // Update the LiveData with the converted values
            _convertedValue.value = convertedValues

            // Assign the converted value to _outputValue based on the selected unit
            _outputValue.value = convertedValues[_toUnit.value]?.toString() ?: "0" // Updates the output value based on the selected 'to' unit.
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during conversion
            _outputValue.value = "Error" // Set output to error on exception
        }
    }
}
