package com.example.taskermobile.model.release

data class ReleasePreviewModel(
    val id: String,
    val title: String,
    val endDate: String? = null,
    val isReleased: Boolean,
)
