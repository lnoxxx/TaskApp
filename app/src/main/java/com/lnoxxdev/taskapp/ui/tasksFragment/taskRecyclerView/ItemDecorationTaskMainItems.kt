package com.lnoxxdev.taskapp.ui.tasksFragment.taskRecyclerView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R

class ItemDecorationTaskMainItems: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val dp = parent.context.resources.getDimension(R.dimen.calendar_item_margin).toInt()
        outRect.top = dp
        outRect.right = dp
        outRect.left = dp
    }
}