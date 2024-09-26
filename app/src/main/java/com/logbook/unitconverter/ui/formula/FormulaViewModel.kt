package com.logbook.unitconverter.ui.formula // Package declaration for FormulaViewModel

import androidx.lifecycle.ViewModel // Import for ViewModel class

class FormulaViewModel : ViewModel() { // Declaration of FormulaViewModel class extending ViewModel


    // Function to get a list of grouped conversion formulas.
    fun getGroupedConversionFormulas(): List<UnitGroup> {
        return try {
            listOf( // Return a list of UnitGroup instances
                UnitGroup("Metres", listOf( // Create UnitGroup for Metres with corresponding formulas
                    "1 Metre = 1000 Millimetres", // Conversion formula for Metres
                    "1 Metre = 0.000621371 Miles", // Conversion formula for Metres to Miles
                    "1 Metre = 3.28084 Feet" // Conversion formula for Metres to Feet
                )),
                UnitGroup("Millimetres", listOf( // Create UnitGroup for Millimetres with corresponding formulas
                    "1 Millimetre = 0.001 Metres", // Conversion formula for Millimetres to Metres
                    "1 Millimetre = 0.000000621371 Miles", // Conversion formula for Millimetres to Miles
                    "1 Millimetre = 0.00328084 Feet" // Conversion formula for Millimetres to Feet
                )),
                UnitGroup("Miles", listOf( // Create UnitGroup for Miles with corresponding formulas
                    "1 Mile = 1609.34 Metres", // Conversion formula for Miles to Metres
                    "1 Mile = 1609340 Millimetres", // Conversion formula for Miles to Millimetres
                    "1 Mile = 5280 Feet" // Conversion formula for Miles to Feet
                )),
                UnitGroup("Feet", listOf( // Create UnitGroup for Feet with corresponding formulas
                    "1 Foot = 0.3048 Metres", // Conversion formula for Feet to Metres
                    "1 Foot = 304.8 Millimetres", // Conversion formula for Feet to Millimetres
                    "1 Foot = 0.000189394 Miles" // Conversion formula for Feet to Miles
                ))
            )
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while fetching the formulas
            emptyList() // Return an empty list in case of an error
        }
    }
}
