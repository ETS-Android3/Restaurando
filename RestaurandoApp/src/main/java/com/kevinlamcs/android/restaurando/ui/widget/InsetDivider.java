package com.kevinlamcs.android.restaurando.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kevinlamcs.android.restaurando.R;

/**
 * Class that represents an inset divider used in the favorites list.
 */
public class InsetDivider extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    public InsetDivider(Context context) {
        divider = ContextCompat.getDrawable(context, R.drawable.inset_divider);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        outRect.set(0, 0, 0, divider.getIntrinsicHeight());
    }

    /**
     * Draws an inset divider below every restaurant item in the favorites list.
     * @param c - canvas to draw the divider
     * @param parent - recyclerview to retrieve each holder item
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i=0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
