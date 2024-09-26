package com.logbook.unitconverter.ui.conversion

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.logbook.unitconverter.R

/**
 * Adapter for displaying conversion results in a RecyclerView.
 * Each item in the list represents a unit and its corresponding converted value.
 */
class ConversionResultAdapter(private var results: List<Pair<String, String>>) :
    RecyclerView.Adapter<ConversionResultAdapter.ResultViewHolder>() {

    private var fontSize: Float = 16f // Default font size

    /**
     * ViewHolder for holding the views of each item in the conversion results.
     */
    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView for displaying the unit
        val unitTextView: TextView = itemView.findViewById(R.id.unitTextView)
        // TextView for displaying the converted value
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)

        fun setFontSize(size: Float) {
            try {
                // Set the font size for both TextViews
                unitTextView.textSize = size
                valueTextView.textSize = size
            } catch (e: Exception) {
                e.printStackTrace() // Log any errors when setting font size
            }
        }
    }

    /**
     * Creates a new ViewHolder for an item in the RecyclerView.
     * @param parent The ViewGroup into which the new view will be added.
     * @param viewType The type of view to create (not used here).
     * @return A new ResultViewHolder containing the inflated item view.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return try {
            // Inflate the layout for each conversion result item
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_conversion_result, parent, false)
            ResultViewHolder(view)
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during ViewHolder creation
            ResultViewHolder(View(parent.context)) // Return an empty ViewHolder on error
        }
    }

    /**
     * Binds the data to the views in the ViewHolder for a specific position.
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the results list.
     */
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        try {
            // Get the unit and value for the current position
            val (unit, value) = results.getOrNull(position) ?: return // Safely get item or return

            // Set the unit and value in the respective TextViews
            holder.unitTextView.text = unit
            holder.valueTextView.text = value

            // Apply the font size to the TextViews
            holder.setFontSize(fontSize)
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during data binding
        }
    }

    /**
     * Returns the total number of items in the results list.
     * @return The size of the results list.
     */
    override fun getItemCount(): Int {
        return try {
            results.size
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors when getting item count
            0 // Return 0 if there's an error
        }
    }

    /**
     * Updates the results and notifies the adapter to refresh the displayed data.
     * @param newResults A map of units and their corresponding converted values.
     */
    @SuppressLint("NotifyDataSetChanged") // to suppress warning about using notifyDataSetChanged()
    fun updateResults(newResults: Map<String, String>) {
        try {
            // Convert the map to a list of pairs and update the results
            results = newResults.map { it.toPair() }
            notifyDataSetChanged() // to force re-rendering of view-holder when value changed
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors during results update
        }
    }

    /**
     * Sets the font size for the adapter and refreshes the views.
     * @param size The font size to set.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setFontSize(size: Float) {
        try {
            fontSize = size
            notifyDataSetChanged() // to force re-rendering of view-holder
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors when setting font size
        }
    }
}
