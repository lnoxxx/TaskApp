package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.taskRvAdapters

import android.content.res.ColorStateList
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.databinding.ItemTaskBinding
import com.lnoxxdev.taskapp.ui.tasksFragment.UiTask
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager

class ViewHolderTask(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemTaskBinding.bind(view)
    fun bind(task: UiTask, listener: TaskListener) {
        val color = AppColorManager.getColorById(task.tag?.colorId ?: 0)

        if (task.tag != null) {
            val backgroundColor = itemView.context.getColor(color.colorContainerId)
            val variantColor = itemView.context.getColor(color.colorVariant)
            val textColor = itemView.context.getColor(color.colorOnContainerId)

            binding.cvTaskCard.setCardBackgroundColor(backgroundColor)
            binding.tvTaskName.setTextColor(textColor)
            binding.tvTaskName.text = task.name

            //binding.cvTaskCard.setStrokeColor(ColorStateList.valueOf(variantColor))

            binding.tvTagName.visibility = View.VISIBLE
            binding.tvTagName.setTextColor(variantColor)
            binding.tvTagName.text = task.tag.name
        } else {
            val backgroundColor = AppColorManager.getThemeColor(itemView.context, color.colorContainerId)
            val variantColor = AppColorManager.getThemeColor(itemView.context, color.colorVariant)
            val textColor = AppColorManager.getThemeColor(itemView.context, color.colorOnContainerId)

            binding.cvTaskCard.setCardBackgroundColor(backgroundColor)
            binding.tvTaskName.setTextColor(textColor)
            binding.tvTaskName.text = task.name

            //binding.cvTaskCard.setStrokeColor(ColorStateList.valueOf(variantColor))

            binding.tvTagName.visibility = View.GONE
        }

        itemView.alpha = 1f
        if (task.isDone) {
            itemView.alpha = 0.2f
            val spannableString = SpannableString(task.name)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                task.name.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvTaskName.text = spannableString
        }

        itemView.setOnClickListener {
            val newAlpha = if (task.isDone) 1f else 0.2f
            itemView.animate().apply {
                duration = 50L
                interpolator = AccelerateDecelerateInterpolator()
                scaleX(0.98f)
                scaleY(0.98f)
                alpha(newAlpha)
                withEndAction {
                    listener.changeDone(task)
                }
            }
        }
    }
}
