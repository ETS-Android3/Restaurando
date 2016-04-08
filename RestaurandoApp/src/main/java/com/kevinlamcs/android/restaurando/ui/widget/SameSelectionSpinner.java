package com.kevinlamcs.android.restaurando.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Class for creating a drop down spinner with same selection capabilities.
 */
public class SameSelectionSpinner extends Spinner {

    private int oldPosition;
    private int newPosition;

    public SameSelectionSpinner(Context context) {
        super(context);
    }

    public SameSelectionSpinner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SameSelectionSpinner(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    public void setSelection(int position) {
        oldPosition = getSelectedItemPosition();
        newPosition = position;
        boolean sameSelected = newPosition == oldPosition;
        super.setSelection(position);
        if (sameSelected) {
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position, boolean animate) {
        oldPosition = getSelectedItemPosition();
        newPosition = position;
        boolean sameSelected = newPosition == oldPosition;
        super.setSelection(position);
        if (sameSelected) {
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    /**
     * Is a different item selected?
     * @return true if different item selected. False if same item is selected
     */
    public boolean isItemChanged() {
        return oldPosition != newPosition;
    }
}
