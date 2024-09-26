package com.logbook.unitconverter.ui.formula // Package declaration for FormulaFragment

import android.content.SharedPreferences
import android.os.Bundle // Import for handling saved instance state
import android.view.LayoutInflater // Import for inflating layout views
import android.view.View // Import for View class
import android.view.ViewGroup // Import for ViewGroup class
import android.widget.Toast
import androidx.fragment.app.Fragment // Import for Fragment class
import androidx.fragment.app.viewModels // Import for viewModels delegate
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager // Import for setting RecyclerView layout manager
import com.logbook.unitconverter.R
import com.logbook.unitconverter.databinding.FragmentFormulaBinding // Import for view binding
import com.logbook.unitconverter.ui.FontSizeAware

class FormulaFragment : Fragment(), FontSizeAware {

    private var _binding: FragmentFormulaBinding? = null // Nullable binding variable for view binding
    private val binding get() = _binding!! // Non-nullable binding reference
    private val viewModel: FormulaViewModel by viewModels() // ViewModel for the fragment

    private var fontSize: Float = 16f // Default font size
    private lateinit var adapter: GroupedFormulaAdapter // Declare the adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            // Retrieve font size from SharedPreferences
            val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val selectedFontSizeIndex = sharedPreferences.getInt("FONT_SIZE_INDEX", 1) // Default font size index if not found

            fontSize = when (selectedFontSizeIndex) {
                0 -> resources.getDimension(R.dimen.font_size_small) // Small
                1 -> resources.getDimension(R.dimen.font_size_medium) // Medium
                2 -> resources.getDimension(R.dimen.font_size_large) // Large
                else -> resources.getDimension(R.dimen.font_size_medium) // Default
            }

            _binding = FragmentFormulaBinding.inflate(inflater, container, false) // Inflate the layout and initialize binding
            binding.root
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while creating the view
            View(requireContext()) // Return an empty view on error
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Get grouped conversion formulas from the ViewModel
            val groupedFormulas = viewModel.getGroupedConversionFormulas()
            adapter = GroupedFormulaAdapter(groupedFormulas, fontSize) // Pass the font size to the adapter

            // Set up the RecyclerView
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = adapter

            setFontSize(fontSize) // Set font size for the adapter
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while setting up the view
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks by nullifying the binding reference
    }

    override fun setFontSize(size: Float) {
        try {
            fontSize = size // Update the font size
            adapter.updateFontSize(fontSize) // Update font size in the adapter
        } catch (e: Exception) {
            e.printStackTrace() // Log any errors while updating font size
            Toast.makeText(requireContext(), getString(R.string.toast_font_size_failed_to_changed), Toast.LENGTH_SHORT).show() // Notify user of conversion error

        }
    }
}
