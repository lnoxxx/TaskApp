package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag

import androidx.recyclerview.widget.DiffUtil
import com.lnoxxdev.taskapp.ui.addTaskFragment.UiTag

class TagItemsDiffUtil(
    private val oldList: List<UiTag>,
    private val newList: List<UiTag>,
    private val oldSelectedTag: UiTag?,
    private val newSelectedTag: UiTag?,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        val oldIsSelected = oldItem == oldSelectedTag
        val newIsSelected = newItem == newSelectedTag

        return oldItem.tagId == newItem.tagId && oldIsSelected == newIsSelected
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        val oldIsSelected = oldItem == oldSelectedTag
        val newIsSelected = newItem == newSelectedTag

        return oldIsSelected == newIsSelected
    }
}