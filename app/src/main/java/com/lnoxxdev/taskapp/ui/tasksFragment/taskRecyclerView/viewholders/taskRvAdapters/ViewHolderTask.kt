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
import com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.TaskListener
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager

class ViewHolderTask(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemTaskBinding.bind(view)
    fun bind(task: UiTask, listener: TaskListener) {
        val color = AppColorManager.getColorById(task.tag?.colorId ?: 0)
        binding.tvTaskName.text = task.name

        if (task.isDone) {
            val spannableString = SpannableString(task.name)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                task.name.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvTaskName.text = spannableString

            val backgroundColor = AppColorManager.getThemeColor(
                itemView.context,
                AppColorManager.noTagColorContainerId
            )
            val textColor = AppColorManager.getThemeColor(
                itemView.context,
                AppColorManager.noTagColorOnContainerId
            )
            setTaskColor(backgroundColor, textColor)
            binding.tvTagName.visibility = View.GONE
        } else {
            if (task.tag != null) {
                binding.tvTagName.text = task.tag.name

                val backgroundColor = itemView.context.getColor(color.colorContainerId)
                val variantColor = itemView.context.getColor(color.colorVariant)
                val textColor = itemView.context.getColor(color.colorOnContainerId)
                setTaskColor(backgroundColor, textColor)

                binding.tvTagName.visibility = View.VISIBLE
                binding.tvTagName.setTextColor(variantColor)
                binding.tvTagName.text = task.tag.name
            } else {
                val backgroundColor = AppColorManager.getThemeColor(
                    itemView.context,
                    AppColorManager.noTagColorContainerId
                )
                val textColor = AppColorManager.getThemeColor(
                    itemView.context,
                    AppColorManager.noTagColorOnContainerId
                )
                setTaskColor(backgroundColor, textColor)
                binding.tvTagName.visibility = View.GONE
            }
        }

        itemView.setOnClickListener {
            itemView.animate().apply {
                duration = 50L
                interpolator = AccelerateDecelerateInterpolator()
                scaleX(0.90f)
                scaleY(0.90f)
                withEndAction {
                    listener.changeDone(task)
                }
            }
        }
    }

    private fun setTaskColor(backgroundColor: Int, textColor: Int) {
        binding.cvTaskCard.setCardBackgroundColor(backgroundColor)
        binding.tvTaskName.setTextColor(textColor)
        binding.cvTaskCard.checkedIconTint = ColorStateList.valueOf(textColor)
    }
}
