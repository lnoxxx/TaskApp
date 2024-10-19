package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.databinding.ItemTaskMonthBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.CalendarItem
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppMonthManager
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppMonthManager.getNameOfMonth

class ViewHolderTaskMonth(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemTaskMonthBinding.bind(view)
    fun bind(month: CalendarItem.Month) {
        val monthImage = AppCompatResources.getDrawable(
            itemView.context,
            AppMonthManager.getImageOfMonth(month.month)
        )
        val monthText = "${itemView.context.getString(getNameOfMonth(month.month))} ${month.year}"

        binding.ivMonth.setImageDrawable(monthImage)
        binding.tvMonthName.text = monthText
    }
}