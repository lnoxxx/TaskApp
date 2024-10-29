package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders

import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.ItemTaskMainBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.CalendarItem
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups.RvTaskGroupsAdapter
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppWeekManager

class ViewHolderTaskDay(view: View, private val listener: TaskListener) :
    RecyclerView.ViewHolder(view) {
    private val binding = ItemTaskMainBinding.bind(view)

    // colors
    private val textColorDefault = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.colorDateDefault
    )
    private val textColorToday = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.colorDateToday
    )
    private val textColorWeekend = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.colorDateWeekend
    )

    // stroke
    private val todayStrokeWidth =
        itemView.context.resources.getDimension(R.dimen.today_stroke_width).toInt()

    fun bind(dateState: CalendarItem.Day) {
        // base date info
        val day = dateState.date.dayOfMonth.toString()
        val dayOfWeek =
            itemView.context.getString(AppWeekManager.getWeekDayName(dateState.dayOfWeek))
        binding.tvDateDay.text = day
        binding.tvDayOfWeek.text = dayOfWeek
        // click listeners
        binding.cvDateBackground.setOnClickListener {
            listener.addTaskToDay(dateState.date)
        }
        // decor today/weekend days
        changeUniqueDates(dateState)
        // update recycler view
        recyclerViewInit(dateState)
    }

    private fun changeUniqueDates(dateState: CalendarItem.Day) {
        //apply colors
        binding.tvDateDay.setTextColor(textColorDefault)
        binding.cvDateBackground.strokeWidth = 0

        // today > weekend !
        if (dateState.dayOfWeek == 7) {
            //weekend
            binding.tvDateDay.setTextColor(textColorWeekend)
        }
        if (dateState.isToday) {
            //today
            binding.tvDateDay.setTextColor(textColorToday)
            binding.cvDateBackground.setStrokeColor(ColorStateList.valueOf(textColorToday))
            binding.cvDateBackground.strokeWidth = todayStrokeWidth
        }
    }

    private fun recyclerViewInit(dateState: CalendarItem.Day) {
        if (dateState.tasks.isEmpty()) {
            binding.rvTasksList.visibility = View.GONE
        } else {
            binding.rvTasksList.visibility = View.VISIBLE
            binding.rvTasksList.layoutManager = LinearLayoutManager(itemView.context)
            binding.rvTasksList.adapter =
                RvTaskGroupsAdapter(listener, dateState.groupedTasks.toMutableList())
        }
    }
}