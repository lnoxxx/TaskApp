package com.lnoxxdev.taskapp.ui.tasksFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lnoxxdev.data.appDatabase.Task
import com.lnoxxdev.data.dateRepository.DateRepository
import com.lnoxxdev.data.tagRepository.TagRepository
import com.lnoxxdev.data.tasksRepository.TasksRepository
import com.lnoxxdev.taskapp.isLastInMonthDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dataRepository: DateRepository,
    private val tasksRepository: TasksRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val defaultState = TaskFragmentUiState(
        calendarItems = null,
        todayPosition = null,
        scrollToPosition = null,
    )

    private val _uiState = MutableStateFlow(defaultState)
    val uiState: StateFlow<TaskFragmentUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                dataRepository.dateState,
                tasksRepository.tasks,
                tagRepository.tags
            ) { dateState, taskList, tagList ->
                val tagMap = tagList.associate { it.id to UiTag(it.id, it.colorId, it.name) }
                val taskMap = createTaskMap(taskList, tagMap)
                val calendarItems = dateListToCalendarItemsList(dateState.dateList, taskMap)
                val selectedDate = dateState.selectedDate
                val scrollPosition: Int? = if (selectedDate != null) findPositionInCalendarItems(
                    selectedDate,
                    calendarItems
                ) else null
                _uiState.value.copy(
                    calendarItems = calendarItems,
                    scrollToPosition = scrollPosition
                )
            }.collect { newState ->
                viewModelScope.launch { emitNewState(newState) }
            }
        }
    }

    private fun createTaskMap(
        taskList: List<Task>,
        tagMap: Map<Int, UiTag>
    ): Map<LocalDate, List<UiTask>> {
        return taskList.groupBy({ task -> LocalDate.of(task.year, task.month, task.day) },
            { task ->
                UiTask(
                    id = task.id,
                    name = task.name,
                    minutes = task.minutes,
                    hour = task.hour,
                    allDay = task.allDay,
                    tag = tagMap[task.tagId],
                    remindWorkId = task.remindWorkId,
                    isDone = task.isDone
                )
            }
        )
    }

    private suspend fun emitNewState(newState: TaskFragmentUiState) {
        val calendarItems = newState.calendarItems
        val todayPosition = if (calendarItems != null) findPositionInCalendarItems(
            LocalDate.now(),
            calendarItems
        ) else null
        val state = newState.copy(todayPosition = todayPosition)
        _uiState.emit(state)
    }

    fun loadFeatureDays() {
        dataRepository.extendDateFuture()
    }

    fun loadPastDays() {
        dataRepository.extendDatePast()
    }

    private fun dateListToCalendarItemsList(
        dateList: List<LocalDate>,
        taskMap: Map<LocalDate, List<UiTask>>
    ): List<CalendarItem> {
        val result = mutableListOf<CalendarItem>()
        for (day in dateList) {
            result.add(
                CalendarItem.Day(
                    day,
                    taskMap[day],
                    day == LocalDate.now(),
                    day.dayOfWeek.value,
                    day.isBefore(LocalDate.now()),
                )
            )
            if (day.isLastInMonthDay()) {
                val month = day.plusDays(1).month
                val year = day.plusDays(1).year
                result.add(CalendarItem.Month(month.value, year, day.isBefore(LocalDate.now())))
            }
        }
        return result
    }

    private fun findPositionInCalendarItems(date: LocalDate, list: List<CalendarItem>): Int? {
        for (index in list.indices) {
            when (val item = list[index]) {
                is CalendarItem.Day -> if (item.date.isEqual(date)) return index
                else -> continue
            }
        }
        return null
    }

    fun changeTaskDoneStatus(task: UiTask) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksRepository.updateDoneStatus(task.id)
        }
    }

    fun scrollToDate(date: LocalDate) {
        dataRepository.selectDate(date)
    }

    fun scrollFinish() {
        dataRepository.dateSelected()
    }
}