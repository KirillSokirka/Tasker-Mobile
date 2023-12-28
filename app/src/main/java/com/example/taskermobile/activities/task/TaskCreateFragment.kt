package com.example.taskermobile.activities.users

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.taskermobile.LoginActivity
import com.example.taskermobile.R
import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.model.auth.ChangePasswordModel
import com.example.taskermobile.model.task.TaskCreateModel
import com.example.taskermobile.model.task.TaskModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getEmailFromToken
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.utils.getUsernameFromToken
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskCreateFragment : Fragment() {
    private val tokenViewModel: TokenViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskTitle: EditText = view.findViewById(R.id.createTaskTitle)
        val taskDescription: EditText = view.findViewById(R.id.createTaskDescription)
        val priority: Spinner = view.findViewById(R.id.createTaskPriority)
        var createButton: Button = view.findViewById(R.id.createTaskButton)
        var selectedPriority: String = "MEDIUM"

        priority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedPriority = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPriority = "MEDIUM"
            }
        }

        tokenViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            createButton.isEnabled = !isLoading
        })

        createButton.setOnClickListener{
            val idFromJwt = getIdFromToken(tokenViewModel.token.value!!.toString())!!
            val task: TaskCreateModel = TaskCreateModel(
                title = taskTitle.text.toString(),
                description = taskDescription.text.toString(),
                priority = enumValueOf<TaskPriority>(selectedPriority),
                creatorId = idFromJwt
//                projectId = ""
            )

            selectedPriority = selectedPriority
        }



    }

}