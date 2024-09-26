package com.logbook.unitconverter.ui.formula // Package declaration for GroupedFormulaAdapter

import android.content.Context
import android.view.LayoutInflater // Import for inflating layout views
import android.view.View
import android.view.ViewGroup // Import for ViewGroup class
import android.widget.TextView // Import for TextView class
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView // Import for RecyclerView class
import com.logbook.unitconverter.databinding.ItemFormulaBinding // Import for view binding

class GroupedFormulaAdapter(
    private val groupedFormulas: List<UnitGroup>,
    private var fontSize: Float // Add a fontSize parameter
) : RecyclerView.Adapter<GroupedFormulaAdapter.GroupedViewHolder>() {

    inner class GroupedViewHolder(private val binding: ItemFormulaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the unit group to the view holder.
         * @param unitGroup The UnitGroup to be displayed.
         */
        fun bind(unitGroup: UnitGroup) {
            try {
                binding.unitTextView.text = unitGroup.unitName
                binding.formulasContainer.removeAllViews() // Clear previous formulas

                for ((index, formula) in unitGroup.formulas.withIndex()) {
                    try {
                        val formulaTextView = TextView(binding.root.context).apply {
                            text = formula
                            textSize = fontSize
                            id = View.generateViewId() // Generate unique ID for each TextView
                        }

                        // Create layout params for vertical constraints
                        val layoutParams = ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            // Set margins
                            setMargins(0, 8.dpToPx(binding.root.context), 0, 8.dpToPx(binding.root.context))

                            if (index == 0) {
                                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            } else {
                                topToBottom = binding.formulasContainer.getChildAt(index - 1).id
                            }
                            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        }

                        // Add the TextView to the ConstraintLayout
                        formulaTextView.layoutParams = layoutParams
                        binding.formulasContainer.addView(formulaTextView)

                    } catch (e: Exception) {
                        e.printStackTrace() // Log any errors while adding formula TextView
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log any errors during binding
            }
        }

        // Extension function to convert dp to pixels
        fun Int.dpToPx(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }

    }

    /**
     * Creates a new ViewHolder for the RecyclerView.
     * @param parent The parent ViewGroup.
     * @param viewType The type of view to create.
     * @return A new GroupedViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupedViewHolder {
        return try {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemFormulaBinding.inflate(inflater, parent, false)
            GroupedViewHolder(binding)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error creating view holder") // Rethrow exception with context
        }
    }

    /**
     * Binds the data to the ViewHolder at a specific position.
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the groupedFormulas list.
     */
    override fun onBindViewHolder(holder: GroupedViewHolder, position: Int) {
        try {
            holder.bind(groupedFormulas[position])
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during binding
        }
    }

    /**
     * Returns the total number of items in the grouped formulas list.
     * @return The size of the groupedFormulas list.
     */
    override fun getItemCount(): Int {
        return try {
            groupedFormulas.size
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while getting item count
            0 // Return 0 as a fallback
        }
    }

    /**
     * Updates the font size for the adapter and refreshes the views.
     * @param newFontSize The new font size to be set.
     */
    fun updateFontSize(newFontSize: Float) {
        try {
            fontSize = newFontSize // Update the font size
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during font size update
        }
    }
}
