package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView.viewholders.tasksGroups

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager

abstract class SwipeToDelete : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val context = itemView.context
        val itemHeight = itemView.bottom - itemView.top

        //background
        val backgroundColor =
            AppColorManager.getThemeColor(context, com.google.android.material.R.attr.colorErrorContainer)
        val background = Paint()
        background.isAntiAlias = true
        background.setColor(backgroundColor)
        background.style = Paint.Style.FILL
        val rect = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
        val corners = context.resources.getDimension(R.dimen.app_base_corner_radius)
        c.drawRoundRect(rect, corners, corners, background)

        //icon
        val icon = AppCompatResources.getDrawable(context, R.drawable.ic_delete)
        val iconMargin = context.resources.getDimension(R.dimen.ic_delete_margin).toInt()
        val iconSize = context.resources.getDimension(R.dimen.ic_delete_size).toInt()

        val iconLeft = itemView.left + iconMargin
        val iconTop = itemView.top + (itemHeight/2) - iconSize/2
        val iconRight = iconLeft + iconSize
        val iconBottom = itemView.bottom - (itemHeight/2) + iconSize/2
        icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}