package com.lnoxxdev.taskapp.ui.addTaskFragment.rvSelectTag

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R

class ItemDecorationAddTaskTags: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val dp = parent.context.resources.getDimension(R.dimen.tag_item_margin).toInt()
        outRect.bottom = dp
        outRect.top = dp
        outRect.right = dp*2
    }
}