package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag

import com.lnoxxdev.taskapp.ui.addTaskFragment.AddTaskViewModel

interface SelectTagRvListener {
    fun createNewTag()
    fun changeSelectedTag(tag: AddTaskViewModel.UiTag?)
}