package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.databinding.ItemAddTagButtonBinding
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.SelectTagRvListener

class ViewHolderAddTagButton(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemAddTagButtonBinding.bind(view)
    fun bind(listener: SelectTagRvListener){
        binding.btnAddTag.setOnClickListener {
            listener.createNewTag()
        }
    }
}