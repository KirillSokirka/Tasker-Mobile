package com.example.taskermobile.activities.users

import SharedPreferencesService
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.taskermobile.LoginActivity
import com.example.taskermobile.R
import com.example.taskermobile.model.auth.ChangePasswordModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getEmailFromToken
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.utils.getUsernameFromToken
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserFragment : Fragment() {
    private val tokenViewModel: TokenViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()

    private val sharedPreferences: SharedPreferencesService by inject()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var overlayView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        overlayView = view.findViewById(R.id.overlayView)

        tokenViewModel.token.observe(viewLifecycleOwner, Observer { tokenValue ->
            tokenValue?.let {
                val username = getUsernameFromToken(it.toString())
                val userNameLabel: TextView = view.findViewById(R.id.userName)
                userNameLabel.text = username
            }
        })

        val logOutButton: Button = view.findViewById(R.id.logOut)
        logOutButton.setOnClickListener {
            tokenViewModel.deleteToken()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        val deleteButton: Button = view.findViewById(R.id.deleteAccount)
        val changePasswordButton: Button = view.findViewById(R.id.changePassword)

        tokenViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            deleteButton.isEnabled = !isLoading
            changePasswordButton.isEnabled = !isLoading
        })

        deleteButton.setOnClickListener {
            val idFromJwt = getIdFromToken(tokenViewModel.token.value!!.toString())!!
            userViewModel.delete(idFromJwt)
        }

        changePasswordButton.setOnClickListener {
            val emailFromJwt = getEmailFromToken(tokenViewModel.token.value!!.toString())!!
            userViewModel.changePassword(
                ChangePasswordModel(
                    emailFromJwt,
                    view.findViewById<EditText>(R.id.oldPassword).text.toString(),
                    view.findViewById<EditText>(R.id.newPassword).text.toString()
                )
            )
        }

        setupObservers()
    }

    private fun setupObservers() {
        userViewModel.changePasswordResponse.observe(viewLifecycleOwner) { apiResponse ->
            handleApiResponse(apiResponse)
        }

        userViewModel.deleteResponse.observe(viewLifecycleOwner) { apiResponse ->
            handleApiResponse(apiResponse)
        }
    }

    private fun handleApiResponse(apiResponse: ApiResponse<Any>) {
        when (apiResponse) {
            is ApiResponse.Loading -> showLoading()

            is ApiResponse.Success -> {
                hideLoading()
                sharedPreferences.saveData("lastDetailRelease", null)
                sharedPreferences.saveData("lastKanbanBoard", null)
                sharedPreferences.saveData("lastProjectActive", null)
                tokenViewModel.deleteToken()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }

            is ApiResponse.Failure -> {
                hideLoading()
                Toast.makeText(
                    requireContext(),
                    "Error: ${apiResponse.errorMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        overlayView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        overlayView.visibility = View.GONE
    }
}
