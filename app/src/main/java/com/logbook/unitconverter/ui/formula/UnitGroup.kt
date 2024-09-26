package com.logbook.unitconverter.ui.formula // Package declaration for UnitGroup

// Data class to represent a group of units and their conversion formulas
data class UnitGroup(
    val unitName: String, // Property to hold the name of the unit
    val formulas: List<String> // Property to hold a list of conversion formulas related to the unit
)
