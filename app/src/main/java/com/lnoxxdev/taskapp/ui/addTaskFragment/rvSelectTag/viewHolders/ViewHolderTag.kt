package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.viewHolders

import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lnoxxdev.taskapp.databinding.ItemTagBinding
import com.lnoxxdev.taskapp.ui.addTaskFragment.UiTag
import com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag.SelectTagRvListener

class ViewHolderTag(view: View) : ViewHolder(view) {
    private val binding = ItemTagBinding.bind(view)
    fun bind(tag: UiTag, selected: Boolean, listener: SelectTagRvListener) {
        //view bind
        val backgroundColor = itemView.context.getColor(tag.color.colorContainerId)
        val textColor = itemView.context.getColor(tag.color.colorOnContainerId)
        binding.cvTagCard.setCardBackgroundColor(backgroundColor)
        binding.tvTagName.setTextColor(textColor)
        binding.tvTagName.text = tag.name
        //anim

        itemView.alpha = 0.5f
        itemView.scaleX = 1f
        itemView.scaleY = 1f
        if (selected) {
            itemView.animate().apply {
                duration = 150
                interpolator = OvershootInterpolator(10f)
                scaleX(1.03f)
                scaleY(1.03f)
                alpha(1f)
            }
        }
        Log.d("testlog", binding.cvTagCard.alpha.toString())
        //click listener
        itemView.setOnClickListener {
            listener.changeSelectedTag(if (selected) null else tag)
        }
    }
}