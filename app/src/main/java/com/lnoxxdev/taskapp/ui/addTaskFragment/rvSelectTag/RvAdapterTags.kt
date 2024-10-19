package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.addTaskFragment.AddTaskViewModel
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.viewHolders.ViewHolderAddTagButton
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.viewHolders.ViewHolderTag

class RvAdapterTags(
    private var tagList: List<AddTaskViewModel.UiTag>,
    private val listener: SelectTagRvListener,
    private var selectedTagPosition: Int? = null
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TAG_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_tag, parent, false)
                ViewHolderTag(view)
            }

            ADD_TAG_BUTTON_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_add_tag_button, parent, false)
                ViewHolderAddTagButton(view)
            }

            else -> throw IllegalStateException("unknown viewType RvAdapterTags")
        }
    }

    override fun getItemCount() = tagList.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolderTag) holder.bind(
            tagList[position],
            selectedTagPosition == position,
            listener
        )
        if (holder is ViewHolderAddTagButton) holder.bind(listener)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            tagList.size -> ADD_TAG_BUTTON_TYPE
            else -> TAG_TYPE
        }
    }

    //TODO Rework this
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTagList: List<AddTaskViewModel.UiTag>, selectedTag: AddTaskViewModel.UiTag?) {
        if (selectedTagPosition != null
            && selectedTag == tagList[selectedTagPosition!!]
            && tagList == newTagList) {
            return
        }
        tagList = newTagList
        if (selectedTag == null) {
            selectedTagPosition = null
        } else {
            val newPosition = tagList.indexOf(selectedTag)
            selectedTagPosition = newPosition
        }
        notifyDataSetChanged()
    }

    companion object {
        const val TAG_TYPE = 0
        const val ADD_TAG_BUTTON_TYPE = 1
    }
}