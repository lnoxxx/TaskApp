package com.lnoxxdev.taskapp.ui.addTaskFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lnoxxdev.data.tagRepository.TagRepository
import com.lnoxxdev.data.tasksRepository.TasksRepository
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val tagRepository: TagRepository,
) : ViewModel() {

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
        HOUR1(3600),
        HOUR2(7200),
        HOUR12(43200),
        DAY(86400),
        NONE(0),
    }

    private val initDefaultTime = LocalTime.now()
    private val initDefaultDate = LocalDate.now()
    private val initReminderTime = UiReminderTime.NONE
    private val defaultState = AddTaskUiState(
        tags = null,
        selectedTag = null,
        date = initDefaultDate,
        dateEpochMilli = null,
        time = initDefaultTime,
        isAllDayTask = false,
        reminderTime = initReminderTime,
        reminderSecondsDelay = secondsDelay(
            initDefaultDate,
            initDefaultTime
        ) - initReminderTime.seconds
    )

    private val _uiState = MutableStateFlow(defaultState)
    val uiState: StateFlow<AddTaskUiState> = _uiState

    init {
        viewModelScope.launch {
            tagRepository.tags.collect { tagList ->
                val uiTagList = tagList.map {
                    UiTag(
                        name = it.name,
                        color = AppColorManager.getColorById(it.colorId),
                        tagId = it.id
                    )
                }
                val newState = _uiState.value.copy(tags = uiTagList)
                _uiState.emit(newState)
            }
        }
    }

    fun changeTaskDate(localDate: LocalDate, dateEpoch: Long) {
        val newState = _uiState.value.copy(date = localDate, dateEpochMilli = dateEpoch)
        viewModelScope.launch { emitNewStateWithNewReminderDelay(newState) }
    }

    fun changeTaskTime(localTime: LocalTime) {
        val newState = _uiState.value.copy(time = localTime)
        viewModelScope.launch { emitNewStateWithNewReminderDelay(newState) }
    }

    fun changeIsAllDayTask(isAllDayTask: Boolean) {
        val newState = _uiState.value.copy(isAllDayTask = isAllDayTask)
        viewModelScope.launch { emitNewStateWithNewReminderDelay(newState) }
    }

    fun changeSelectedTag(tag: UiTag?) {
        val newState = _uiState.value.copy(selectedTag = tag)
        viewModelScope.launch { _uiState.emit(newState) }
    }

    fun changeReminderTime(time: UiReminderTime) {
        val newState = _uiState.value.copy(reminderTime = time)
        viewModelScope.launch { emitNewStateWithNewReminderDelay(newState) }
    }

    fun saveTask(name: String): Int? {
        val nameError = taskNameValidate(name)
        if (nameError != null) {
            return nameError
        }
        //save task
        val savedState = _uiState.value
        CoroutineScope(Dispatchers.IO).launch {
            tasksRepository.insert(
                name = name,
                date = savedState.date,
                time = savedState.time,
                isAllDayTask = savedState.isAllDayTask,
                reminderSecondsDelay = savedState.reminderTime.seconds,
                taskTagId = savedState.selectedTag?.tagId
            )
        }
        return null
    }

    private fun taskNameValidate(name: String): Int? {
        if (name.isEmpty()) return R.string.error_empty_name
        if (name.length < 2) return R.string.error_short_name
        if (name.length > 300) return R.string.error_long_name
        return null
    }

    private suspend fun emitNewStateWithNewReminderDelay(newState: AddTaskUiState) {
        val taskMinute = if (!newState.isAllDayTask) newState.time.minute else 0
        val taskHour = if (!newState.isAllDayTask) newState.time.hour else 0
        val targetTime = LocalTime.of(taskHour, taskMinute)
        var delay = secondsDelay(newState.date, targetTime) - newState.reminderTime.seconds
        if (delay < 0 || newState.reminderTime.seconds == 0L) delay = 0
        val fixedNewState = newState.copy(reminderSecondsDelay = delay)
        _uiState.emit(fixedNewState)
    }

    private fun secondsDelay(date: LocalDate, time: LocalTime): Long {
        val targetDateTime = LocalDateTime.of(date, time)
        val currentDateTime = LocalDateTime.now()
        var delay = Duration.between(currentDateTime, targetDateTime).seconds
        if (delay < 0) delay = 0
        return delay
    }

}
