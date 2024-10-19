package com.lnoxxdev.taskapp.ui.tasksFragment

import android.os.Bundle
import android.util.Log
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
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.FragmentTasksBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.RvAdapterTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.taskRvAdapters.TaskListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

@AndroidEntryPoint
class TasksFragment : Fragment(), TaskListener {

    private var _binding: FragmentTasksBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("TaskFragmentBinding null")

    private val viewModel: TasksViewModel by viewModels()

    private var adapter: RvAdapterTask? = null

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
        hideCalendar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // on click listeners
        binding.fabAddTask.setOnClickListener {
            showAddTaskBottomSheet()
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

    override fun onResume() {
        super.onResume()
        recyclerviewInit()
        val uiState = viewModel.uiState.value
        bindUiState(uiState)
        Log.d("testlog", "${binding.rvTasks.adapter}")
        Log.d("testlog", "${adapter}")
    }


    private fun bindUiState(uiState: TaskFragmentUiState) {
        uiState.calendarItems?.let {
            bindCalendarItems(it, uiState.scrollToPosition)
        }
    }

    private fun bindCalendarItems(calendarItems: List<CalendarItem>, scrollPosition: Int?) {
        if (adapter == null) {
            adapter = RvAdapterTask(calendarItems.toMutableList(), this)
            recyclerviewInit()
        } else {
            adapter!!.updateCalendarItems(calendarItems)
            scrollPosition?.let {
                scrollToPosition(it)
            }
        }
    }

    private fun recyclerviewInit() {
        binding.rvTasks.layoutManager = LinearLayoutManager(context)
        binding.rvTasks.adapter = adapter
        binding.rvTasks.itemAnimator = null
        val todayPosition = viewModel.uiState.value.todayPosition ?: 0
        binding.rvTasks.scrollToPosition(todayPosition)

        binding.rvTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val itemCount = layoutManager.itemCount

                if (lastVisibleItem == itemCount - 1) {
                    viewModel.loadFeatureDays()
                }
                if (firstVisibleItem == 0) {
                    viewModel.loadPastDays()
                }

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
        val instant = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        binding.cvCalendar.date = instant
        binding.mlCalendarMotion.transitionToEnd()
    }

    private fun hideCalendar() {
        binding.mlCalendarMotion.transitionToStart()
    }

    private fun scrollToToday() {
        viewModel.uiState.value.todayPosition?.let {
            binding.rvTasks.smoothScrollToPosition(it)
        }
    }

    private fun scrollToPosition(position: Int) {
        binding.rvTasks.smoothScrollToPosition(position)
        viewModel.scrollFinish()
    }

    private fun showAddTaskBottomSheet() {
        findNavController().navigate(R.id.action_tasksFragment_nav_to_addTaskBottomSheet)
    }

    private fun getFirstVisibleItemDateOrNow(): LocalDate {
        if (adapter == null) return LocalDate.now()
        val pos =
            (binding.rvTasks.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (pos == -1) return LocalDate.now()
        return when (val item = viewModel.uiState.value.calendarItems?.get(pos)) {
            is CalendarItem.Day -> item.date
            is CalendarItem.Month -> LocalDate.of(item.year, item.month, 1)
            else -> LocalDate.now()
        }
    }

    override fun changeDone(task: UiTask) {
        viewModel.changeTaskDoneStatus(task)
    }
}
