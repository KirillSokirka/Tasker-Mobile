package com.example.taskermobile.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDto(
    val id: String,
    val title: String,
    val isAdmin: Boolean
) : Parcelable