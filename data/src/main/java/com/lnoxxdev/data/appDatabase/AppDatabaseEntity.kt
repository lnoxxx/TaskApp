package com.lnoxxdev.data.appDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

//notes

@Entity(tableName = "notes")
data class Note(
    val name: String,
    val text: String,
    val colorId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)

//tasks

@Entity(tableName = "tags")
data class Tag(
    val name: String,
    val colorId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)

@Entity(tableName = "tasks")
data class Task(
    val name: String,
    val minutes: Int,
    val hour: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val allDay: Boolean,
    val tagId: Int?,
    val remindWorkId: String?,
    val isDone: Boolean,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
