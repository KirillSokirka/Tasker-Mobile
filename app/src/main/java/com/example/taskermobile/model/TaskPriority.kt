package com.example.taskermobile.model

enum class TaskPriority {
    NONE, LOW, MEDIUM, HIGH, URGENT;
    companion object {
        fun fromInt(value: Int) = when (value) {
            0 -> NONE
            1 -> LOW
            2 -> MEDIUM
            3 -> HIGH
            4 -> URGENT
            else -> throw IllegalArgumentException("Invalid priority value")
        }
    }
}

