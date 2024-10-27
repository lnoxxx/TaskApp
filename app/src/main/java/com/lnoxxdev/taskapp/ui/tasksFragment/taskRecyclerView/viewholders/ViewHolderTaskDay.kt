package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders

import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.ItemTaskMainBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.CalendarItem
import com.lnoxxdev.taskapp.ui.tasksFragment.UiTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.taskRvAdapters.RvTaskListAdapter
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppWeekManager

class ViewHolderTaskDay(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemTaskMainBinding.bind(view)

    fun bind(dateState: CalendarItem.Day, listener: TaskListener) {
        val day = dateState.date.dayOfMonth.toString()
        val dayOfWeek =
            itemView.context.getString(AppWeekManager.getWeekDayName(dateState.dayOfWeek))

        binding.tvDateDay.text = day
        binding.tvDayOfWeek.text = dayOfWeek

        binding.cvDateBackground.setOnClickListener {
            listener.addTaskToDay(dateState.date)
        }

        changeUniqueDates(dateState)

        recyclerViewInit(dateState, listener)
    }

    private fun changeUniqueDates(dateState: CalendarItem.Day) {
        val textColorDefault = AppColorManager.getThemeColor(
            itemView.context,
            com.google.android.material.R.attr.colorOnSurface
        )
        val textColorToday = AppColorManager.getThemeColor(
            itemView.context,
            com.google.android.material.R.attr.colorPrimary
        )
        val textColorWeekend = AppColorManager.getThemeColor(
            itemView.context,
            com.google.android.material.R.attr.colorError
        )
        //apply colors
        binding.tvDateDay.setTextColor(textColorDefault)
        binding.cvDateBackground.strokeWidth = 0
        if (dateState.dayOfWeek == 7) {
            binding.tvDateDay.setTextColor(textColorWeekend)
        }
        if (dateState.isToday) {
            binding.tvDateDay.setTextColor(textColorToday)
            binding.cvDateBackground.setStrokeColor(ColorStateList.valueOf(textColorToday))

            val strokeWidth =
                itemView.context.resources.getDimension(R.dimen.today_stroke_width).toInt()
            binding.cvDateBackground.strokeWidth = strokeWidth
        }
    }

    private fun recyclerViewInit(dateState: CalendarItem.Day, listener: TaskListener) {
        if (dateState.tasks == null) {
            binding.rvTasksList.visibility = View.GONE
        } else {
            binding.rvTasksList.visibility = View.VISIBLE
            val allDayTasks = dateState.tasks.filter { it.allDay }
            val groupTasks = groupTasks(dateState.tasks)
            binding.rvTasksList.layoutManager = LinearLayoutManager(itemView.context)
            binding.rvTasksList.adapter = RvTaskListAdapter(allDayTasks, groupTasks, listener)
        }
    }

    private fun groupTasks(task: List<UiTask>): List<List<UiTask>> {
        val defaultTasks = task
            .filter { !it.allDay }
            .groupBy { it.hour to it.minutes }
            .toSortedMap(
                compareBy({ it.first }, { it.second })
            )
        val result = mutableListOf<List<UiTask>>()
        result.addAll(defaultTasks.values)
        return result
    }
}