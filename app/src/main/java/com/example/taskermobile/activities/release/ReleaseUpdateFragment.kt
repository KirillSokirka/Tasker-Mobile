package com.example.taskermobile.activities.release

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.taskermobile.R
import java.util.Locale

class ReleaseUpdateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.releases_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val titleSelector = EditText(requireContext())
        layout.addView(titleSelector)

        val defaultCreationDate = Calendar.getInstance().time

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
            },
            defaultCreationDate.year + 1900,
            defaultCreationDate.month,
            defaultCreationDate.date
        )

        layout.addView(datePickerDialog.datePicker)

        val selectedYear = datePickerDialog.datePicker.year
        val selectedMonth = datePickerDialog.datePicker.month
        val selectedDay = datePickerDialog.datePicker.dayOfMonth

        val selectedDate = Calendar.getInstance()
        selectedDate.set(selectedYear, selectedMonth, selectedDay)

        val endDate = String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d 00:00:00", // Use the format "yyyy-MM-dd HH:mm:ss"
            selectedYear,
            selectedMonth + 1, // Months are zero-based in DatePicker
            selectedDay
        )

    }
}