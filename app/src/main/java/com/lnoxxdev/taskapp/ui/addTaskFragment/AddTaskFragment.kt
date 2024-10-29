package com.lnoxxdev.taskapp.ui.addTaskFragment

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.lnoxxdev.data.DateTimeManager
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.FragmentAddTaskBinding
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.ItemDecorationAddTaskTags
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.RvAdapterTags
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.SelectTagRvListener
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class AddTaskFragment : Fragment(), SelectTagRvListener {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("AddTaskFragmentBinding null")

    private val args: AddTaskFragmentArgs by navArgs()
    private val viewmodel: AddTaskViewModel by lazy {
        val viewmodel: AddTaskViewModel by viewModels()
        val date = LocalDate.of(args.year, args.month, args.day)
        viewmodel.initByDate(date)
        viewmodel
    }

    @Inject
    lateinit var dateTimeManager: DateTimeManager

    private var adapter: RvAdapterTags = RvAdapterTags(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater)
        lifecycleScope.launch {
            viewmodel.uiState.collect { uiState ->
                bindUiState(uiState)
            }
        }
        binding.mainLayout.setOnApplyWindowInsetsListener { v, insets ->
            val inset = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = inset.top)
            insets
        }
        recyclerViewInit()
        changeNameTextViewColor(null)
        binding.tvError.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                findNavController().navigateUp()
            } else {
                showError(error)
            }
        }
        //close icon
        binding.ivIconClose.setOnClickListener {
            findNavController().navigateUp()
        }
        setRadioButtonClickListeners()
    }

    override fun onResume() {
        super.onResume()
        bindUiState(viewmodel.uiState.value)
    }

    private fun bindUiState(uiState: AddTaskUiState) {
        bindDate(uiState.date)
        bindTime(uiState.time, uiState.isAllDayTask)
        uiState.tags?.let {
            bindTagList(it, uiState.selectedTag)
        }
        bindRadioButton(uiState.reminderTime)
        bindReminderDelay(uiState.reminderSecondsDelay)
        changeNameTextViewColor(uiState.selectedTag?.color)
    }

    private fun bindDate(date: LocalDate) {
        val dateFormatter = dateTimeManager.getDateFormatter()
        val dateText = date.format(dateFormatter)
        binding.tvTaskDate.text = dateText
    }

    private fun bindTime(time: LocalTime, isAllDayTask: Boolean) {
        val timeFormatter = dateTimeManager.getTimeFormatter()
        val timeText = time.format(timeFormatter)
        binding.tvTaskTime.text = timeText
        if (isAllDayTask) {
            hideTaskTime()
        } else {
            showTaskTime()
        }
    }

    private fun showTaskTime() {
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

    private fun hideTaskTime() {
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

    private fun recyclerViewInit() {
        val flexboxLayoutManager = FlexboxLayoutManager(context)
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        binding.rvSelectTag.layoutManager = flexboxLayoutManager
        binding.rvSelectTag.addItemDecoration(ItemDecorationAddTaskTags())
        binding.rvSelectTag.itemAnimator = null // fuck this >:(
        binding.rvSelectTag.adapter = adapter
    }

    private fun bindTagList(tagList: List<UiTag>, selectedTag: UiTag?) {
        adapter.updateData(tagList, selectedTag)
    }

    private fun bindRadioButton(time: UiReminderTime) {
        val radioButton = when (time) {
            UiReminderTime.MINUTE5 -> binding.rb5Minutes
            UiReminderTime.HOUR1 -> binding.rb1Hour
            UiReminderTime.HOUR2 -> binding.rb2Hour
            UiReminderTime.HOUR12 -> binding.rb12Hour
            UiReminderTime.DAY -> binding.rbDay
            UiReminderTime.NONE -> binding.rbNone
        }
        radioButton.isChecked = true
    }

    private fun showError(error: Int) {
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
            val date = dateTimeManager.getDateFromEpochMilli(it)
            viewmodel.changeTaskDate(date, it)
        }
    }

    private fun setRadioButtonClickListeners() {
        binding.rbNone.setOnClickListener {
            viewmodel.changeReminderTime(UiReminderTime.NONE)
        }
        binding.rbDay.setOnClickListener {
            viewmodel.changeReminderTime(UiReminderTime.DAY)
        }
        binding.rb5Minutes.setOnClickListener {
            viewmodel.changeReminderTime(UiReminderTime.MINUTE5)
        }
        binding.rb1Hour.setOnClickListener {
            viewmodel.changeReminderTime(UiReminderTime.HOUR1)
        }
        binding.rb2Hour.setOnClickListener {
            viewmodel.changeReminderTime(UiReminderTime.HOUR2)
        }
        binding.rb12Hour.setOnClickListener {
            viewmodel.changeReminderTime(UiReminderTime.HOUR12)
        }
    }

    override fun createNewTag() {
        findNavController().navigate(R.id.addTagDialog)
    }

    override fun changeSelectedTag(tag: UiTag?) {
        viewmodel.changeSelectedTag(tag)
    }

    private fun changeNameTextViewColor(color: AppColorManager.TagColor?) {
        val colorContainer: Int
        val colorText: Int
        val colorHint: Int

        if (color != null) {
            colorContainer = resources.getColor(color.colorContainerId, context?.theme)
            colorText = resources.getColor(color.colorOnContainerId, context?.theme)
            colorHint = resources.getColor(color.colorVariant, context?.theme)
        } else {
            colorContainer = AppColorManager.getThemeColor(
                requireContext(),
                AppColorManager.noTagColorContainerId
            )
            colorText = AppColorManager.getThemeColor(
                requireContext(),
                AppColorManager.noTagColorOnContainerId
            )
            colorHint =
                AppColorManager.getThemeColor(requireContext(), AppColorManager.noTagColorVariantId)
        }

        ObjectAnimator.ofArgb(
            binding.cvTaskName,
            "cardBackgroundColor",
            binding.cvTaskName.cardBackgroundColor.defaultColor, colorContainer
        ).start()

        binding.cvTaskName.setStrokeColor(ColorStateList.valueOf(colorHint))
        binding.etTaskName.setTextColor(colorText)
        binding.etTaskName.setHintTextColor(colorHint)
    }

}