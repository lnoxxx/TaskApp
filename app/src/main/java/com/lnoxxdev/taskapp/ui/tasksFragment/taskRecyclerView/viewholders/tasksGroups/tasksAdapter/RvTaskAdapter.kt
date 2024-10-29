package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups.tasksAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.tasksFragment.UiTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener

class RvTaskAdapter(
    private val listener: TaskListener,
    private val taskList: List<UiTask>
) : RecyclerView.Adapter<ViewHolderTask>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTask {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return ViewHolderTask(view)
    }

    override fun getItemCount() = taskList.size

    override fun onBindViewHolder(holder: ViewHolderTask, position: Int) {
        holder.bind(taskList[position], listener)
    }
}