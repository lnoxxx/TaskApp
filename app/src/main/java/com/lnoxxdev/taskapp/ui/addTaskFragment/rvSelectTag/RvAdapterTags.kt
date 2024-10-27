package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.addTaskFragment.UiTag
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.viewHolders.ViewHolderAddTagButton
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.viewHolders.ViewHolderTag

class RvAdapterTags(private val listener: SelectTagRvListener) :
    RecyclerView.Adapter<ViewHolder>() {

    private var selectedTag: UiTag? = null
    private var tagList: MutableList<UiTag> = mutableListOf()

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
            selectedTag == tagList[position],
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

    fun updateData(newTagList: List<UiTag>, newSelectedTag: UiTag?) {
        val oldTagList = tagList
        val oldSelectedTag = selectedTag

        val diffCallback = TagItemsDiffUtil(oldTagList, newTagList, oldSelectedTag, newSelectedTag)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tagList.clear()
        tagList.addAll(newTagList)
        selectedTag = newSelectedTag

        diffResult.dispatchUpdatesTo(this)
    }

    companion object {
        const val TAG_TYPE = 0
        const val ADD_TAG_BUTTON_TYPE = 1
    }
}


