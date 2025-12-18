package com.example.personalplanner.data.local.database

import androidx.room.TypeConverter
import com.example.personalplanner.data.local.entity.Subtask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSubtaskList(subtasks: List<Subtask>): String {
        return gson.toJson(subtasks)
    }

    @TypeConverter
    fun toSubtaskList(subtasksString: String): List<Subtask> {
        val type = object : TypeToken<List<Subtask>>() {}.type
        return gson.fromJson(subtasksString, type)
    }
}
