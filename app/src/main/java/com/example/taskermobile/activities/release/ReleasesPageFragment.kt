package com.example.taskermobile.activities.release

import SharedPreferencesService
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import com.example.taskermobile.viewadapters.ReleaseAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.RegisterActivity
import com.example.taskermobile.model.release.ReleaseModel
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.user.UserUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class ReleasesPageFragment : Fragment() {
    private val sharedPreferencesService: SharedPreferencesService by inject()
    private val viewModel by viewModel<ReleasesPageViewModel>()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.releases_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectId = sharedPreferencesService.retrieveData("lastProjectActive")
        if (projectId == null) {
            findNavController().navigate(R.id.action_releasesPageFragment_to_projectsPageFragment)
            return
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getAll(projectId!!)

        viewModel.releasesResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    recyclerView.adapter = ReleaseAdapter(apiResponse.data)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val addReleaseButton: Button = view.findViewById(R.id.addReleaseButton)// Inside your setOnClickListener block
        addReleaseButton.setOnClickListener {
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

            AlertDialog.Builder(requireContext())
                .setTitle("Add New User")
                .setView(layout)
                .setPositiveButton("Add") { dialog, which ->
                    //get the fucking date
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

                    val startDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d 00:00:00", // Use the format "yyyy-MM-dd HH:mm:ss"
                        defaultCreationDate.year + 1900,
                        defaultCreationDate.month,
                        defaultCreationDate.day
                    )

                    val modle = ReleaseModel(id = UUID.randomUUID().toString(), title = titleSelector.text.toString(), isReleased = false,
                        creationDate = startDate,
                        endDate = endDate, projectId = projectId, tasks = emptyList())

                    viewModel.create(modle)
                    viewModel.releaseResponse.observe(viewLifecycleOwner) { apiResponse ->
                        when (apiResponse) {
                            is ApiResponse.Loading -> {
                                loadingIndicator.visibility = View.VISIBLE
                            }

                            is ApiResponse.Success -> {
                                loadingIndicator.visibility = View.GONE
                            }

                            is ApiResponse.Failure -> {
                                loadingIndicator.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "Network error: ${apiResponse.errorMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
