package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView

import com.lnoxxdev.taskapp.ui.tasksFragment.UiTask
import java.time.LocalDate

interface TaskListener {
    fun changeDone(task: UiTask)
    fun addTaskToDay(day: LocalDate)
    fun removeTask(task: UiTask, returnItem: () -> Unit)
}
