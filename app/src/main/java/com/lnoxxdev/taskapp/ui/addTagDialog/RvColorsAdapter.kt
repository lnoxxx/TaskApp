package com.lnoxxdev.taskapp.ui.addTagDialog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.ItemColorBinding
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager

class RvColorsAdapter(private val listener: ColorSelectedListener) :
    RecyclerView.Adapter<RvColorsAdapter.ViewHolderColor>() {

    private val colorList = AppColorManager.TagColor.entries

    private var selectColorPosition = 0

    inner class ViewHolderColor(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemColorBinding.bind(view)
        fun bind(colorItem: AppColorManager.TagColor, selected: Boolean) {
            // selected anim
            itemView.alpha = 0.5f
            itemView.scaleX = 1f
            itemView.scaleY = 1f
            if (selected) {
                itemView.animate().apply {
                    duration = 100
                    interpolator = OvershootInterpolator(10f)
                    alpha(1f)
                    scaleY(1.1f)
                    scaleX(1.1f)
                }
            }
            // set color
            val color =
                itemView.resources.getColor(colorItem.colorContainerId, itemView.context.theme)
            binding.cvColor.setCardBackgroundColor(color)
            //click listener
            itemView.setOnClickListener {
                changeSelectedColor(colorItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderColor {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ViewHolderColor(view)
    }

    override fun getItemCount() = colorList.size

    override fun onBindViewHolder(holder: ViewHolderColor, position: Int) {
        holder.bind(colorList[position], position == selectColorPosition)
    }

    fun getSelectedColor(): AppColorManager.TagColor {
        return colorList[selectColorPosition]
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeSelectedColor(newColor: AppColorManager.TagColor) {
        selectColorPosition = colorList.indexOf(newColor)
        notifyDataSetChanged()
        listener.selectColor(newColor)
    }
}