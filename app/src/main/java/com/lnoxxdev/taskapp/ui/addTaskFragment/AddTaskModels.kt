package com.lnoxxdev.taskapp.ui.addTaskFragment

import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import java.time.LocalDate
import java.time.LocalTime

data class AddTaskUiState(
    val tags: List<UiTag>?,
    val selectedTag: UiTag?,
    val date: LocalDate,
    val dateEpochMilli: Long?,
    val time: LocalTime,
    val isAllDayTask: Boolean,
    val reminderTime: UiReminderTime,
    val reminderSecondsDelay: Long,
)

data class UiTag(val name: String, val color: AppColorManager.TagColor, val tagId: Int)

enum class UiReminderTime(val seconds: Long) {
    MINUTE5(300),
    HOUR1(3600),
    HOUR2(7200),
    HOUR12(43200),
    DAY(86400),
    NONE(0),
}