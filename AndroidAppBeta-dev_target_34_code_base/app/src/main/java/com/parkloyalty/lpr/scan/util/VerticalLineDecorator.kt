package com.parkloyalty.lpr.scan.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Created by sab99r
 */
//class VerticalLineDecorator(space: Int) : ItemDecoration() {
//    private var space = 0
//    override fun getItemOffsets(
//        outRect: Rect,
//        view: View,
//        parent: RecyclerView,
//        state: RecyclerView.State
//    ) {
//        if (parent.getChildAdapterPosition(view) == 0) outRect.top = space
//        outRect.bottom = space
//    }
//
//    init {
//        this.space = space
//    }
//}


class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // Add bottom margin to all items except the last one
        if (parent.getChildAdapterPosition(view) != state.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }
}

//How to use
//recyclerView.addItemDecoration(VerticalSpaceItemDecoration(16)) // 16px gap
