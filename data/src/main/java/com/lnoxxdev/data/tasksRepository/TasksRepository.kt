package com.lnoxxdev.data.tasksRepository

import com.lnoxxdev.data.appDatabase.Task
import com.lnoxxdev.data.appDatabase.TasksDao
import com.lnoxxdev.data.dateRepository.DateRepository
import com.lnoxxdev.data.notification.ReminderManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val tasksDao: TasksDao,
    private val reminderManager: ReminderManager,
    dateRepository: DateRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: Flow<List<Task>> = dateRepository.dateState.flatMapLatest { dateState ->
        val startDate = dateState.dateList.first()
        val endDate = dateState.dateList.last()
        tasksDao.getInDateRange(
            startYear = startDate.year,
            startMonth = startDate.monthValue,
            startDay = startDate.dayOfMonth,
            endYear = endDate.year,
            endMonth = endDate.monthValue,
            endDay = endDate.dayOfMonth
        )
    }

    suspend fun insert(
        name: String,
        date: LocalDate,
        time: LocalTime,
        isAllDayTask: Boolean,
        reminderSecondsDelay: Long,
        taskTagId: Int?,
    ) {
        val taskName = name.trim(' ')
        val taskMinute = if (!isAllDayTask) time.minute else 0
        val taskHour = if (!isAllDayTask) time.hour else 0

        val taskReminderWorkId = if (reminderSecondsDelay != 0L) {
            val targetTime = LocalTime.of(taskHour, taskMinute)
            var delay = secondsDelay(date, targetTime) - reminderSecondsDelay
            if (delay < 0) delay = 0
            val notificationId = NotificationId(name, date, time, reminderSecondsDelay).hashCode()
            reminderManager.scheduleReminder(taskName, delay, notificationId)
        } else {
            null
        }

        val task = Task(
            name = taskName,
            minutes = taskMinute,
            hour = taskHour,
            day = date.dayOfMonth,
            month = date.monthValue,
            year = date.year,
            allDay = isAllDayTask,
            tagId = taskTagId,
            remindWorkId = taskReminderWorkId,
            isDone = false
        )
        tasksDao.insert(task)
    }

    data class NotificationId(
        val name: String,
        val date: LocalDate,
        val time: LocalTime,
        val reminderSecondsDelay: Long,
    )

    suspend fun delete(task: Task) {
        tasksDao.delete(task)
    }

    suspend fun update(task: Task) {
        tasksDao.update(task)
    }

    suspend fun updateDoneStatus(id: Int){
        val task = tasksDao.getTaskById(id)
        update(task.copy(isDone = !task.isDone))
    }

    private fun secondsDelay(date: LocalDate, time: LocalTime): Long {
        val targetDateTime = LocalDateTime.of(date, time)
        val currentDateTime = LocalDateTime.now()
        var delay = Duration.between(currentDateTime, targetDateTime).seconds
        if (delay < 0) delay = 0
        return delay
    }
}