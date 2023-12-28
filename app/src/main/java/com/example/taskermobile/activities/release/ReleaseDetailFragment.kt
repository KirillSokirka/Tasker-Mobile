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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.LoginActivity
import com.example.taskermobile.R
import com.example.taskermobile.RegisterActivity
import com.example.taskermobile.model.release.ReleaseModel
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.user.UserUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class ReleaseDetailFragment : Fragment() {
    private val viewModel by viewModel<ReleasesPageViewModel>()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var deleteButton: Button
    private lateinit var releaseNameView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.release_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val releaseName = arguments?.getString("RELEASE_NAME")
        val releaseId = arguments?.getString("RELEASE_ID")

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        releaseNameView = view.findViewById(R.id.releaseName)
        deleteButton = view.findViewById(R.id.deleteReleaseButton)

        releaseNameView.text = releaseName

        deleteButton.setOnClickListener {
            viewModel.delete(releaseId!!)
        }

        viewModel.deleteReleaseResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> loadingIndicator.visibility = View.VISIBLE

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.VISIBLE
                    findNavController().navigate(
                        R.id.action_releaseDetailFragment_to_releasesPageFragment)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
