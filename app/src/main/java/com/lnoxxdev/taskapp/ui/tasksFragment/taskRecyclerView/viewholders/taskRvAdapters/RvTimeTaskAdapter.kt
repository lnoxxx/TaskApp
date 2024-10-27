package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.taskRvAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.tasksFragment.UiTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener

class RvTimeTaskAdapter(
    private val timeTaskList: List<UiTask>,
    private val listener: TaskListener,
) :
    RecyclerView.Adapter<ViewHolderTask>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTask {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return ViewHolderTask(view)
    }

    override fun getItemCount() = timeTaskList.size

    override fun onBindViewHolder(holder: ViewHolderTask, position: Int) {
        holder.bind(timeTaskList[position], listener)
    }
}