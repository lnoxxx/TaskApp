package com.lnoxxdev.taskapp.ui.addTagDialog

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lnoxxdev.taskapp.R

class ItemDecorationAddTagColors: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val dp = view.context.resources.getDimension(R.dimen.color_item_margin).toInt()
        outRect.right = dp
        outRect.top = dp
        outRect.bottom = dp
    }
}