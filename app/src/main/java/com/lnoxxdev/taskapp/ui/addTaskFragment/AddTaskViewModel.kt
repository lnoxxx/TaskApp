package com.lnoxxdev.taskapp.ui.addTaskFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lnoxxdev.data.DateTimeManager
import com.lnoxxdev.data.tagRepository.TagRepository
import com.lnoxxdev.data.tasksRepository.TasksRepository
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val tagRepository: TagRepository,
    private val dateTimeManager: DateTimeManager,
) : ViewModel() {

    private val initDefaultTime = dateTimeManager.getNowTime()
    private val initDefaultDate = dateTimeManager.getNowDate()
    private val initReminderTime = UiReminderTime.NONE
    private val initSecondsDelay =
        tasksRepository.secondsDelay(initDefaultDate, initDefaultTime) - initReminderTime.seconds
    private val defaultState = AddTaskUiState(
        tags = null,
        selectedTag = null,
        date = initDefaultDate,
        dateEpochMilli = null,
        time = initDefaultTime,
        isAllDayTask = false,
        reminderTime = initReminderTime,
        reminderSecondsDelay = initSecondsDelay
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

    fun initByDate(date: LocalDate) {
        val epochMilli = dateTimeManager.getDateEpochMilli(date)
        changeTaskDate(date, epochMilli)
    }

    fun changeTaskDate(localDate: LocalDate, dateEpoch: Long) {
        val newState = _uiState.value.copy(date = localDate, dateEpochMilli = dateEpoch)
        viewModelScope.launch { emitNewState(newState) }
    }

    fun changeTaskTime(localTime: LocalTime) {
        val newState = _uiState.value.copy(time = localTime)
        viewModelScope.launch { emitNewState(newState) }
    }

    fun changeIsAllDayTask(isAllDayTask: Boolean) {
        val newState = _uiState.value.copy(isAllDayTask = isAllDayTask)
        viewModelScope.launch { emitNewState(newState) }
    }

    fun changeSelectedTag(tag: UiTag?) {
        val newState = _uiState.value.copy(selectedTag = tag)
        viewModelScope.launch { _uiState.emit(newState) }
    }

    fun changeReminderTime(time: UiReminderTime) {
        val newState = _uiState.value.copy(reminderTime = time)
        viewModelScope.launch { emitNewState(newState) }
    }

    private suspend fun emitNewState(newState: AddTaskUiState) {
        val taskMinute = if (!newState.isAllDayTask) newState.time.minute else 0
        val taskHour = if (!newState.isAllDayTask) newState.time.hour else 0
        val targetTime = LocalTime.of(taskHour, taskMinute)
        var delay =
            tasksRepository.secondsDelay(newState.date, targetTime) - newState.reminderTime.seconds
        if (delay < 0 || newState.reminderTime.seconds == 0L) delay = 0
        val fixedNewState = newState.copy(reminderSecondsDelay = delay)
        _uiState.emit(fixedNewState)
    }

    fun saveTask(name: String): Int? {
        //validate
        val nameError = tasksRepository.taskNameValidate(name)
        if (nameError != null) {
            return nameError
        }
        //save task
        val savedState = _uiState.value
        CoroutineScope(Dispatchers.IO).launch {
            tasksRepository.createTask(
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

}
