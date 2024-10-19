package com.lnoxxdev.taskapp.ui.addTaskFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.BottomSheetAddTaskBinding
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.RvAdapterTags
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.SelectTagRvListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class AddTaskBottomSheet : BottomSheetDialogFragment(), SelectTagRvListener {

    private var _binding: BottomSheetAddTaskBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("AddTaskFragmentBinding null")

    private val viewmodel: AddTaskViewModel by viewModels()

    private var adapter: RvAdapterTags? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddTaskBinding.inflate(inflater)
        lifecycleScope.launch {
            viewmodel.uiState.collect { uiState ->
                bindUiState(uiState)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Bottom sheet behavior
        val bottomSheet: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            isDraggable = false
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        //on click listeners
        binding.tvTaskDate.setOnClickListener {
            showDatePicker()
        }
        //time
        binding.tvTaskTime.setOnClickListener {
            showTimePicker()
        }
        //all day check box
        binding.cbAllDay.setOnCheckedChangeListener { _, isChecked ->
            viewmodel.changeIsAllDayTask(isChecked)
        }
        //save task
        binding.btnSaveTask.setOnClickListener {
            val name = binding.etTaskName.text.toString()
            val error = viewmodel.saveTask(name)
            if (error == null) {
                dialog?.dismiss()
            } else {
                showError(error)
            }
        }
        //close icon
        binding.ivIconClose.setOnClickListener {
            dialog?.dismiss()
        }
        setRadioButtonClickListeners()
    }

    override fun onResume() {
        super.onResume()
        bindUiState(viewmodel.uiState.value)
    }

    private fun bindUiState(uiState: AddTaskViewModel.AddTaskUiState) {
        bindDate(uiState.date)
        bindTime(uiState.time, uiState.isAllDayTask)
        uiState.tags?.let {
            bindTagList(it, uiState.selectedTag)
        }
        bindRadioButton(uiState.reminderTime)
        bindReminderDelay(uiState.reminderSecondsDelay)
    }

    private fun bindDate(date: LocalDate) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val dateText = date.format(dateFormatter)
        binding.tvTaskDate.text = dateText
    }

    private fun bindTime(time: LocalTime, isAllDayTask: Boolean) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val timeText = time.format(timeFormatter)
        binding.tvTaskTime.text = timeText
        if (isAllDayTask) {
            binding.tvTaskTime.animate().apply {
                duration = 100
                interpolator = DecelerateInterpolator()
                scaleX(0.9f)
                scaleY(0.9f)
                alpha(0f)
                withEndAction {
                    binding.tvTaskTime.visibility = View.GONE
                }
            }
        } else {
            binding.tvTaskTime.animate().apply {
                binding.tvTaskTime.visibility = View.VISIBLE
                binding.tvTaskTime.animate().apply {
                    scaleX(1f)
                    scaleY(1f)
                    duration = 100
                    interpolator = DecelerateInterpolator()
                    alpha(1f)
                }
            }
        }
    }

    private fun bindReminderDelay(delay: Long) {
        binding.tvNotificationDelay.text = convertSeconds(delay)
    }

    private fun convertSeconds(seconds: Long): String {
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        return getString(
            R.string.add_task_reminder_delay,
            days.toString(),
            hours.toString(),
            minutes.toString()
        )
    }

    private fun bindTagList(
        tagList: List<AddTaskViewModel.UiTag>,
        selectedTag: AddTaskViewModel.UiTag?,
    ) {
        if (adapter == null) {
            adapter = RvAdapterTags(tagList, this)
            binding.rvSelectTag.adapter = adapter
            val flexboxLayoutManager = FlexboxLayoutManager(context)
            flexboxLayoutManager.apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            binding.rvSelectTag.layoutManager = flexboxLayoutManager
        } else {
            adapter!!.updateData(tagList, selectedTag)
        }
    }

    private fun bindRadioButton(time: AddTaskViewModel.UiReminderTime) {
        val radioButton = when (time) {
            AddTaskViewModel.UiReminderTime.HOUR1 -> binding.rb1Hour
            AddTaskViewModel.UiReminderTime.HOUR2 -> binding.rb2Hour
            AddTaskViewModel.UiReminderTime.HOUR12 -> binding.rb12Hour
            AddTaskViewModel.UiReminderTime.DAY -> binding.rbDay
            AddTaskViewModel.UiReminderTime.NONE -> binding.rbNone
        }
        radioButton.isChecked = true
    }

    private fun showError(error: Int) {
        binding.etTaskName.animate().apply {
            duration = 50
            translationX(-10f)
            withEndAction {
                binding.etTaskName.animate().apply {
                    duration = 50
                    translationX(0f)
                }
            }
        }
        binding.tvError.text = getString(error)
        binding.tvError.visibility = View.VISIBLE
    }

    private fun showTimePicker() {
        val timePicker =
            MaterialTimePicker.Builder()
                .setHour(viewmodel.uiState.value.time.hour)
                .setMinute(viewmodel.uiState.value.time.minute)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()
        timePicker.show(childFragmentManager, null)
        timePicker.addOnPositiveButtonClickListener {
            val localTime = LocalTime.of(timePicker.hour, timePicker.minute)
            viewmodel.changeTaskTime(localTime)
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(viewmodel.uiState.value.dateEpochMilli).build()
        datePicker.show(childFragmentManager, null)
        datePicker.addOnPositiveButtonClickListener {
            val instant = Instant.ofEpochMilli(it)
            val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            viewmodel.changeTaskDate(localDate, it)
        }
    }

    private fun setRadioButtonClickListeners() {
        binding.rbNone.setOnClickListener {
            viewmodel.changeReminderTime(AddTaskViewModel.UiReminderTime.NONE)
        }
        binding.rbDay.setOnClickListener {
            viewmodel.changeReminderTime(AddTaskViewModel.UiReminderTime.DAY)
        }
        binding.rb1Hour.setOnClickListener {
            viewmodel.changeReminderTime(AddTaskViewModel.UiReminderTime.HOUR1)
        }
        binding.rb2Hour.setOnClickListener {
            viewmodel.changeReminderTime(AddTaskViewModel.UiReminderTime.HOUR2)
        }
        binding.rb12Hour.setOnClickListener {
            viewmodel.changeReminderTime(AddTaskViewModel.UiReminderTime.HOUR12)
        }
    }

    override fun createNewTag() {
        findNavController().navigate(R.id.addTagDialog)
    }

    override fun changeSelectedTag(tag: AddTaskViewModel.UiTag?) {
        viewmodel.changeSelectedTag(tag)
    }

}