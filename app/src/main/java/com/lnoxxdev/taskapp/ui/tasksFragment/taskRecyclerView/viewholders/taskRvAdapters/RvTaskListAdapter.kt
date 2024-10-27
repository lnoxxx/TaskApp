package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.taskRvAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.ItemTaskGroupBinding
import com.lnoxxdev.taskapp.databinding.ItemTasksAlldayBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.UiTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class RvTaskListAdapter(
    private val allDayTaskList: List<UiTask>,
    private val groupTasksList: List<List<UiTask>>,
    private val listener: TaskListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolderAllDayTasks(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemTasksAlldayBinding.bind(view)
        fun bind(allDayTasks: List<UiTask>, listener: TaskListener) {
            binding.rvAllDayTasks.layoutManager = LinearLayoutManager(itemView.context)
            val adapter = RvAllDayTaskAdapter(allDayTasks, listener)
            binding.rvAllDayTasks.addItemDecoration(ItemDecorationTask())
            binding.rvAllDayTasks.adapter = adapter
            val swipeHandler =  object : SwipeToDelete(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = allDayTasks[viewHolder.adapterPosition]
                    listener.removeTask(task){adapter.notifyItemChanged(viewHolder.adapterPosition)}
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(binding.rvAllDayTasks)
        }
    }

    class ViewHolderGroupTasks(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemTaskGroupBinding.bind(view)
        fun bind(tasksList: List<UiTask>, listener: TaskListener) {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.of(tasksList[0].hour, tasksList[0].minutes)
            binding.rvTimeTasks.layoutManager = LinearLayoutManager(itemView.context)
            val adapter = RvTimeTaskAdapter(tasksList, listener)
            binding.rvTimeTasks.addItemDecoration(ItemDecorationTask())
            binding.rvTimeTasks.adapter = adapter
            val swipeHandler =  object : SwipeToDelete(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = tasksList[viewHolder.adapterPosition]
                    listener.removeTask(task){adapter.notifyItemChanged(viewHolder.adapterPosition)}
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(binding.rvTimeTasks)
            binding.tvTime.text = time.format(timeFormatter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ALL_DAY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_tasks_allday, parent, false)
                ViewHolderAllDayTasks(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_task_group, parent, false)
                ViewHolderGroupTasks(view)
            }
        }
    }

    override fun getItemCount() = groupTasksList.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAllDayTasks -> holder.bind(allDayTaskList, listener)
            is ViewHolderGroupTasks -> holder.bind(groupTasksList[position - 1], listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_ALL_DAY
            else -> TYPE_GROUP
        }
    }

    companion object {
        const val TYPE_ALL_DAY = 0
        const val TYPE_GROUP = 1
    }
}



