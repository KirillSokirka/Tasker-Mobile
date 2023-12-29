package com.example.taskermobile.activities.release

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.example.taskermobile.R
import com.example.taskermobile.model.release.ReleaseUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class ReleaseUpdateFragment : Fragment() {
    private val viewModel by viewModel<ReleasesPageViewModel>()
    private var selectedStatus: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.release_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val releaseId = arguments?.getString("RELEASE_ID")
            ?: throw Exception("Missing release id")
        val projectId = arguments?.getString("PROJECT_ID")
            ?: throw Exception("Missing project id")
        var loadingIndicator : ProgressBar = view.findViewById(R.id.loadingIndicator)
        val titleSelector: EditText = view.findViewById(R.id.newReleaseName)
        val statusSelector: Spinner = view.findViewById(R.id.newReleaseStatus)
        val endDateSelector: CalendarView = view.findViewById(R.id.newReleaseEndDate)
        var endDate: String = ""
        val updateButton: Button = view.findViewById(R.id.updateButton)

        viewModel.get(releaseId)
        viewModel.releaseByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    titleSelector.setText(apiResponse.data!!.title)
                    if (apiResponse.data!!.isReleased) statusSelector.setSelection(1) else statusSelector.setSelection(0)

                    // Parse the date part from apiResponse.data!!.endDate
                    val datePart = apiResponse.data!!.endDate.split("T")[0]
                    val parts = datePart.split("-")
                    val year = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Calendar month is zero-based
                    val dayOfMonth = parts[2].toInt()

                    endDateSelector.setDate(Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.timeInMillis, true, true)

                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Getting release with id ${releaseId} failed: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    view.findNavController().navigate(R.id.action_releaseUpdateFragment_to_releasesPageFragment)
                }
            }
        }

        setUpReleaseSpinner(statusSelector)

        endDateSelector.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Set the time to midnight (00:00:00)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Format the date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd 00:00:00")
            endDate = dateFormat.format(calendar.time)
        }

        updateButton.setOnClickListener {
            viewModel.update(ReleaseUpdateModel(id = releaseId, projectId = projectId, title = titleSelector.text.toString(),
                isReleased = selectedStatus, endDate = endDate, tasks = emptyList() ))
        }

        viewModel.releaseUpdateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    view.findNavController().navigate(R.id.action_releaseUpdateFragment_to_releasesPageFragment)
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Release update error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    view.findNavController().navigate(R.id.action_releaseUpdateFragment_to_releasesPageFragment)
                }
            }
        }

        fun setUpListeneres() {

        }
    }

    private fun setUpReleaseSpinner(statusSelector: Spinner) {
        val statusList = listOf(false, true).toMutableList()
        val statusAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSelector.adapter = statusAdapter

        statusSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedStatus = parent.getItemAtPosition(position) as Boolean
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedStatus = false
            }
        }
    }
}
