package com.example.taskermobile.activities.release

import com.example.taskermobile.viewadapters.ReleaseAdapter
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReleasesPageActivity : AppCompatActivity() {

    private val viewModel: ReleasesPageViewModel by viewModel()
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.releases_list)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        val projectId = intent.getStringExtra("PROJECT_ID").toString()

        viewModel.getAll(projectId)

        viewModel.releasesResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = ReleaseAdapter(apiResponse.data)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@ReleasesPageActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
