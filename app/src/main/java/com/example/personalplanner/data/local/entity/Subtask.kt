package com.example.personalplanner.data.local.entity

data class Subtask(
    val title: String = "",
    var isFinished: Boolean = false,
    var logTime: Long = 0L
)
