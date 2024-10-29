package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups.tasksAdapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.databinding.ItemTaskBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.UiTask
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager

class ViewHolderTask(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemTaskBinding.bind(view)

    private val doneCardColor = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.doneColorContainerId
    )
    private val doneTextColor = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.doneColorOnContainerId
    )
    private val doneTagColor = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.doneColorVariantId
    )

    private val noTagCardColor = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.noTagColorContainerId
    )
    private val noTagTextColor = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.noTagColorOnContainerId
    )
    private val noTagTagColor = AppColorManager.getThemeColor(
        itemView.context,
        AppColorManager.noTagColorVariantId
    )

    fun bind(task: UiTask, listener: TaskListener) {
        binding.tvTaskName.text = task.name
        if (task.isDone) {
            setStrikeText(task.name)
            setTaskColor(doneCardColor, doneTextColor, doneTagColor)
            binding.tvTagName.visibility = View.GONE
        } else {
            if (task.tag != null) {
                binding.tvTagName.text = task.tag.name
                val color = AppColorManager.getColorById(task.tag.colorId)
                val backgroundColor = itemView.context.getColor(color.colorContainerId)
                val tagColor = itemView.context.getColor(color.colorVariant)
                val textColor = itemView.context.getColor(color.colorOnContainerId)
                setTaskColor(backgroundColor, textColor, tagColor)
                binding.tvTagName.text = task.tag.name
                binding.tvTagName.visibility = View.VISIBLE
            } else {
                setTaskColor(noTagCardColor, noTagTextColor, noTagTagColor)
                binding.tvTagName.visibility = View.GONE
            }
        }
        
        itemView.setOnLongClickListener {
            itemView.animate().apply {
                duration = 50
                scaleX(0.95f)
                scaleY(0.95f)
                interpolator = AccelerateDecelerateInterpolator()
                withEndAction {
                    listener.changeDone(task)
                }
            }
            return@setOnLongClickListener true
        }
    }

    private fun setTaskColor(backgroundColor: Int, textColor: Int, tagColor: Int) {
        binding.cvTaskCard.setCardBackgroundColor(backgroundColor)
        binding.tvTaskName.setTextColor(textColor)
        binding.tvTagName.setTextColor(tagColor)
    }

    private fun setStrikeText(text: String) {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            StrikethroughSpan(),
            0,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTaskName.text = spannableString
    }
}
