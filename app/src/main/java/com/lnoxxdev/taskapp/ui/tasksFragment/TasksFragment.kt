package com.lnoxxdev.taskapp.ui.tasksFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lnoxxdev.data.DateTimeManager
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.FragmentTasksBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.ItemDecorationTaskMainItems
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.RvAdapterTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class TasksFragment : Fragment(), TaskListener {

    private var _binding: FragmentTasksBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("TaskFragmentBinding null")

    private val viewModel: TasksViewModel by viewModels()

    @Inject
    lateinit var dateTimeManager: DateTimeManager

    private var adapter = RvAdapterTask(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater)
        lifecycleScope.launch {
            viewModel.uiState.collect {
                bindUiState(it)
            }
        }
        binding.tbTasks.setOnApplyWindowInsetsListener { v, insets ->
            val inset = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = inset.top)
            insets
        }
        recyclerviewInit()
        hideCalendar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // on click listeners
        binding.fabAddTask.setOnClickListener {
            showAddTaskFragment()
        }
        binding.fabToToday.setOnClickListener {
            hideCalendar()
            scrollToToday()
        }
        binding.cvCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            hideCalendar()
            val date = LocalDate.of(year, month + 1, dayOfMonth)
            viewModel.scrollToDate(date)
        }
        binding.ivIconOpenCalendar.setOnClickListener {
            showCalendar()
        }
        binding.ivIconCloseCalendar.setOnClickListener {
            hideCalendar()
        }
    }

    private fun bindUiState(uiState: TaskFragmentUiState) {
        uiState.calendarItems?.let {
            bindCalendarItems(it, uiState.scrollToPosition)
        }
    }

    private fun bindCalendarItems(calendarItems: List<CalendarItem>, scrollPosition: Int?) {
        val todayScroll = adapter.updateCalendarItems(calendarItems)
        // first scroll to today
        if (todayScroll) scrollToToday()
        // if need scroll to position
        scrollPosition?.let {
            scrollToPosition(it)
        }
    }

    private fun recyclerviewInit() {
        binding.rvTasks.layoutManager = LinearLayoutManager(context)
        binding.rvTasks.addItemDecoration(ItemDecorationTaskMainItems())
        binding.rvTasks.adapter = adapter
        binding.rvTasks.itemAnimator = null
        // scroll listener
        binding.rvTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // base info
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val itemCount = layoutManager.itemCount
                // dynamic loading
                if (lastVisibleItem == itemCount - 1) {
                    viewModel.loadFeatureDays()
                }
                if (firstVisibleItem == 0) {
                    viewModel.loadPastDays()
                }
                // show/hide today fab
                todayFab(firstVisibleItem, lastVisibleItem)
            }
        })
    }

    private fun todayFab(first: Int, last: Int) {
        val todayPosition = viewModel.uiState.value.todayPosition
        todayPosition?.let {
            if (it < first || it > last) {
                if (!binding.fabToToday.isShown)
                    binding.fabToToday.show()
            } else {
                if (binding.fabToToday.isShown)
                    binding.fabToToday.hide()
            }
        }
    }

    private fun showCalendar() {
        val date = getFirstVisibleItemDateOrNow()
        val instant = dateTimeManager.getDateEpochMilli(date)
        binding.cvCalendar.date = instant
        binding.mlCalendarMotion.transitionToEnd()
    }

    private fun getFirstVisibleItemDateOrNow(): LocalDate {
        val position = firstCompletelyVisibleItemPosition() ?: return dateTimeManager.getNowDate()
        return when (val item = viewModel.uiState.value.calendarItems?.get(position)) {
            is CalendarItem.Day -> item.date
            is CalendarItem.Month -> LocalDate.of(item.year, item.month, 1)
            else -> dateTimeManager.getNowDate()
        }
    }

    private fun firstCompletelyVisibleItemPosition(): Int? {
        val layoutManager = binding.rvTasks.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstCompletelyVisibleItemPosition()
        return if (position != RecyclerView.NO_POSITION) position else null
    }

    private fun hideCalendar() {
        binding.mlCalendarMotion.transitionToStart()
    }

    private fun scrollToToday() {
        viewModel.uiState.value.todayPosition?.let {
            binding.rvTasks.scrollToPosition(it)
        }
    }

    private fun scrollToPosition(position: Int) {
        binding.rvTasks.scrollToPosition(position)
        viewModel.scrollFinish()
    }

    private fun showAddTaskFragment(day: LocalDate = dateTimeManager.getNowDate()) {
        val action = TasksFragmentDirections.actionTasksFragmentNavToAddTaskBottomSheet(
            day.dayOfMonth,
            day.monthValue,
            day.year
        )
        findNavController().navigate(action)
    }

    private fun showDeleteTaskDialog(task: UiTask, returnItem: () -> Unit) {
        val text = getString(R.string.delete_task_dialog_text, task.name)
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_delete_dialog)
            .setTitle(R.string.delete_task_dialog_title)
            .setMessage(text)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNeutralButton(R.string.cancel, null)
            .setOnDismissListener {
                returnItem.invoke()
            }
            .show()
    }

    override fun changeDone(task: UiTask) {
        viewModel.changeTaskDoneStatus(task)
    }

    override fun addTaskToDay(day: LocalDate) {
        showAddTaskFragment(day)
    }

    override fun removeTask(task: UiTask, returnItem: () -> Unit) {
        showDeleteTaskDialog(task, returnItem)
    }
}
