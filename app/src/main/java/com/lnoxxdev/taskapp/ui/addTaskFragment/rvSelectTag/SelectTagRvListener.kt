package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag

import com.lnoxxdev.taskapp.ui.addTaskFragment.UiTag

interface SelectTagRvListener {
    fun createNewTag()
    fun changeSelectedTag(tag: UiTag?)
}