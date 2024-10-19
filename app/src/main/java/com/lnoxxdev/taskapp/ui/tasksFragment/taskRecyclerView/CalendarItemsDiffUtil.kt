package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView

import androidx.recyclerview.widget.DiffUtil
import com.lnoxxdev.taskapp.ui.tasksFragment.CalendarItem

class CalendarItemsDiffUtil(
    private val oldList: List<CalendarItem>,
    private val newList: List<CalendarItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when (oldItem) {
            is CalendarItem.Month -> {
                when (newItem) {
                    is CalendarItem.Month -> (oldItem.year == newItem.year && oldItem.month == newItem.month)
                    is CalendarItem.Day -> false
                }
            }

            is CalendarItem.Day -> {
                when (newItem) {
                    is CalendarItem.Month -> false
                    is CalendarItem.Day -> oldItem.date == newItem.date
                }
            }
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when (oldItem) {
            is CalendarItem.Month -> {
                when (newItem) {
                    is CalendarItem.Month -> (oldItem.year == newItem.year && oldItem.month == newItem.month)
                    is CalendarItem.Day -> false
                }
            }

            is CalendarItem.Day -> {
                when (newItem) {
                    is CalendarItem.Month -> false
                    is CalendarItem.Day -> oldItem.tasks == newItem.tasks
                }
            }
        }
    }
}