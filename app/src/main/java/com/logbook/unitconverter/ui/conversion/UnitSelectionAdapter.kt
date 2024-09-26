package com.logbook.unitconverter.ui.conversion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.logbook.unitconverter.R
import com.logbook.unitconverter.databinding.SpinnerItemBinding

class UnitSelectionAdapter(
    context: Context,
    objects: Array<String>,
    private var fontSize: Float
) : ArrayAdapter<String>(context, R.layout.spinner_item, objects) {

    fun setFontSize(size: Float) {
        try {
            fontSize = size
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while setting font size
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return try {
            val binding: SpinnerItemBinding = if (convertView == null) {
                SpinnerItemBinding.inflate(LayoutInflater.from(context), parent, false)
            } else {
                SpinnerItemBinding.bind(convertView)
            }

            binding.spinnerItemText.text = getItem(position)
            binding.spinnerItemText.textSize = fontSize

            binding.root
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while creating the view
            View(context) // Return an empty view on error
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return try {
            val binding: SpinnerItemBinding = if (convertView == null) {
                SpinnerItemBinding.inflate(LayoutInflater.from(context), parent, false)
            } else {
                SpinnerItemBinding.bind(convertView)
            }

            binding.spinnerItemText.text = getItem(position)
            binding.spinnerItemText.textSize = fontSize

            binding.root
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while creating the dropdown view
            View(context) // Return an empty view on error
        }
    }
}
