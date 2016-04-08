package com.kevinlamcs.android.restaurando.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kevinlamcs.android.restaurando.R;

/**
 * Class which draws the selected item subheader in the FavoritesList.
 */
public class Subheader extends RecyclerView.ItemDecoration {

    private static final int CHILD_TOP_HEIGHT = 64;
    private static final float SUBHEADER_FONT_SIZE = 44f;
    private static final int CHILD_X = 32;
    private static final int CHILD_Y = 16;

    private final Context context;
    private final int position;

    public Subheader(Context context, int position) {
        this.context = context;
        this.position = position;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawSubheader(c, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        if (parent.getChildAdapterPosition(view) == position) {
            outRect.top = CHILD_TOP_HEIGHT;
        }
    }

    /**
     * Draws the subheader on the canvas.
     * @param c - canvas to draw the subheader
     * @param parent - recyclerview to retrieve the holder views
     */
    private void drawSubheader(Canvas c, RecyclerView parent) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, android.R.color.black));
        paint.setTextSize(SUBHEADER_FONT_SIZE);
        paint.setTextAlign(Paint.Align.LEFT);

        Paint paint2 = new Paint();
        paint2.setColor(ContextCompat.getColor(context, R.color.colorDarkDivider));
        paint2.setStyle(Paint.Style.FILL);

        for (int i=0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) == position) {
                int top = child.getTop() - CHILD_TOP_HEIGHT;
                int bottom = child.getTop();

                float x = CHILD_X;
                float y = bottom - CHILD_Y;

                c.drawRect(0, top, parent.getWidth(), bottom, paint2);
                c.drawText("Selected Items", x, y, paint);
            }
        }
    }
}
