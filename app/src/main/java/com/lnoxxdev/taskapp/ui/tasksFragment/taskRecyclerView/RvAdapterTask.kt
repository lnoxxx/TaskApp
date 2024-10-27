package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.tasksFragment.CalendarItem
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.ViewHolderTaskDay
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.ViewHolderTaskMonth

class RvAdapterTask(private val listener: TaskListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val calendarItems = mutableListOf<CalendarItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DAY_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_task_main, parent, false)
                ViewHolderTaskDay(view)
            }

            MONTH_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_task_month, parent, false)
                ViewHolderTaskMonth(view)
            }

            else -> throw IllegalStateException("Unknown view type in Task rv")
        }
    }

    override fun getItemCount() = calendarItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderTaskDay -> {
                val day = calendarItems[position] as CalendarItem.Day
                holder.bind(day, listener)
            }

            is ViewHolderTaskMonth -> {
                val month = calendarItems[position] as CalendarItem.Month
                holder.bind(month)
            }
        }
    }

    fun updateCalendarItems(newCalendarItems: List<CalendarItem>): Boolean {
        val needScrollToToday = calendarItems.isEmpty()
        val diffCallback = CalendarItemsDiffUtil(calendarItems, newCalendarItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        calendarItems.clear()
        calendarItems.addAll(newCalendarItems)

        diffResult.dispatchUpdatesTo(this)
        return needScrollToToday
    }

    override fun getItemViewType(position: Int): Int {
        return when (calendarItems[position]) {
            is CalendarItem.Day -> DAY_VIEW_TYPE
            is CalendarItem.Month -> MONTH_VIEW_TYPE
        }
    }

    companion object {
        const val MONTH_VIEW_TYPE = 0
        const val DAY_VIEW_TYPE = 1
    }
}
