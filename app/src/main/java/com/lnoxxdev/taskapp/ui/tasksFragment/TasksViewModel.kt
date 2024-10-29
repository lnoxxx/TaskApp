package com.lnoxxdev.taskapp.ui.tasksFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lnoxxdev.data.DateTimeManager
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
    private val tagRepository: TagRepository,
    private val dateTimeManager: DateTimeManager,
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
                // map of tags - id to UiTag
                val tagMap = tagList.associate { it.id to UiTag(it.id, it.colorId, it.name) }
                // map of task - LocalDate to UiTask
                val taskMap = createTaskMap(taskList, tagMap)
                // createCalendarItemList
                val calendarItems = dateListToCalendarItemsList(dateState.dateList, taskMap)

                //find selectedDatePosition if selected
                val selectedDate = dateState.selectedDate
                val scrollPosition: Int? =
                    if (selectedDate != null) findPositionInCalendarItems(
                        selectedDate,
                        calendarItems
                    ) else null

                // return updated ui state
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

    private fun dateListToCalendarItemsList(
        dateList: List<LocalDate>,
        taskMap: Map<LocalDate, List<UiTask>>
    ): List<CalendarItem> {
        val result = mutableListOf<CalendarItem>()
        val today = dateTimeManager.getNowDate()

        for (day in dateList) {
            var tasks = taskMap[day]
            val groupedTasks = if (tasks != null) {
                val allDayGroup = TasksGroup(true, tasks.filter { it.allDay })
                val defaultGroups = groupDefaultTasks(tasks).toMutableList()
                defaultGroups.add(0, allDayGroup)
                defaultGroups
            } else {
                tasks = listOf()
                listOf()
            }

            result.add(
                CalendarItem.Day(
                    date = day,
                    tasks = tasks,
                    groupedTasks = groupedTasks,
                    isToday = day == today,
                    dayOfWeek = day.dayOfWeek.value,
                )
            )

            if (day.isLastInMonthDay()) {
                val month = day.plusDays(1).month
                val year = day.plusDays(1).year
                result.add(CalendarItem.Month(month.value, year))
            }
        }
        return result
    }

    private fun groupDefaultTasks(task: List<UiTask>): List<TasksGroup> {
        val defaultTasks = task
            .filter { !it.allDay }
            .groupBy { it.hour to it.minutes }
            .toSortedMap(
                compareBy({ it.first }, { it.second })
            )
        val groups = defaultTasks.values.toList().map { TasksGroup(false, it) }
        return groups
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

    private suspend fun emitNewState(newState: TaskFragmentUiState) {
        //change today position in new list
        val calendarItems = newState.calendarItems
        val today = dateTimeManager.getNowDate()
        val todayPosition =
            if (calendarItems != null) findPositionInCalendarItems(today, calendarItems) else null

        val state = newState.copy(todayPosition = todayPosition)
        _uiState.emit(state)
    }

    fun loadFeatureDays() {
        dataRepository.extendDateFuture()
    }

    fun loadPastDays() {
        dataRepository.extendDatePast()
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

    fun deleteTask(task: UiTask) {
        CoroutineScope(Dispatchers.IO).launch {
            tasksRepository.delete(task.id)
        }
    }
}