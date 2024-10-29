package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.databinding.ItemTaskGroupBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.TasksGroup
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups.tasksAdapter.ItemDecorationTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups.tasksAdapter.RvTaskAdapter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ViewHolderTasksGroup(view: View, private val listener: TaskListener) :
    RecyclerView.ViewHolder(view) {
    private val binding = ItemTaskGroupBinding.bind(view)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun bind(tasksGroup: TasksGroup) {
        val tasksList = tasksGroup.tasks
        if (tasksGroup.allDay) {
            binding.tvTime.visibility = View.GONE
        } else {
            binding.tvTime.visibility = View.VISIBLE
            val time = LocalTime.of(tasksList[0].hour, tasksList[0].minutes)
            binding.tvTime.text = time.format(timeFormatter)
        }

        binding.rvTasks.layoutManager = LinearLayoutManager(itemView.context)
        binding.rvTasks.addItemDecoration(ItemDecorationTask())
        val adapter = RvTaskAdapter(listener, tasksGroup.tasks.toMutableList())
        binding.rvTasks.adapter = adapter

        val swipeHandler = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = tasksList[viewHolder.adapterPosition]
                listener.removeTask(task) { adapter.notifyItemChanged(viewHolder.adapterPosition) }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.rvTasks)
    }
}
