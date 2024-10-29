package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.tasksFragment.TasksGroup
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener

class RvTaskGroupsAdapter(
    private val listener: TaskListener,
    private val taskGroups: List<TasksGroup>
) : RecyclerView.Adapter<ViewHolderTasksGroup>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTasksGroup {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_group, parent, false)
        return ViewHolderTasksGroup(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolderTasksGroup, position: Int) {
        holder.bind(taskGroups[position])
    }

    override fun getItemCount() = taskGroups.size
}



