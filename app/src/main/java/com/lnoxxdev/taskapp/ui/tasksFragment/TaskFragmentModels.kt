package com.lnoxxdev.taskapp.ui.tasksFragment

import java.time.LocalDate

data class TaskFragmentUiState(
    val calendarItems: List<CalendarItem>?,
    val todayPosition: Int?,
    val scrollToPosition: Int?,
)

sealed class CalendarItem {

    data class Day(
        val date: LocalDate,
        val tasks: List<UiTask>?,
        val isToday: Boolean,
        val dayOfWeek: Int,
        val isPastDay: Boolean,
    ) : CalendarItem()

    data class Month(
        val month: Int,
        val year: Int,
        val isPastMonth: Boolean,
    ) : CalendarItem()
}

data class UiTask(
    val id: Int,
    val name: String,
    val minutes: Int,
    val hour: Int,
    val allDay: Boolean,
    val tag: UiTag?,
    val remindWorkId: String?,
    val isDone: Boolean,
)

data class UiTag(
    val id: Int,
    val colorId: Int,
    val name: String,
)