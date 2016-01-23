package com.kevinlamcs.android.restaurando.ui.model;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.R;

import java.util.List;

/**
 * Created by kevin-lam on 1/21/16.
 */
public class Thought extends TableRow implements View.OnClickListener {

    public static final float WEIGHT = 1.0f;

    public static int thoughtCount = 0;

    private static TableLayout mThoughtTable;
    private TextView mBulletPoint;
    private EditText mThought;
    private ImageButton mButtonRemoveThought;

    private int mThoughtPosition;

    public Thought(Context context, TableLayout table) {
        super(context);
        mThoughtTable = table;

        setupThought(context);

        thoughtCount++;
    }

    @Override
    public void onClick(View v) {
        removeThought(mThoughtTable);
        thoughtCount--;

    }

    private void setupThought(Context context) {
        TableRow.LayoutParams thoughtRowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(thoughtRowParams);

        mBulletPoint = new TextView(context);
        mBulletPoint.setText("•");

        mThought = new EditText(context);
        TableRow.LayoutParams thoughtParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                WEIGHT);
        mThought.setLayoutParams(thoughtParams);

        mButtonRemoveThought = new ImageButton(context);
        mButtonRemoveThought.setImageResource(R.drawable.remove_thought);
        mButtonRemoveThought.setBackgroundResource(android.R.color.transparent);
        mButtonRemoveThought.setOnClickListener(this);

        addView(mBulletPoint);
        addView(mThought);
        addView(mButtonRemoveThought);

        mThoughtTable.addView(this);
    }

    public void focusThought(Context context) {
        requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
    }

    private void removeThought(TableLayout table) {
        table.removeView(this);
    }

    public static void retrieveThought(List<String> list) {
        for (int currentThought = 0; currentThought < thoughtCount; currentThought++) {
            TableRow currentRow = (TableRow)mThoughtTable.getChildAt(currentThought);
            EditText currentEditText = (EditText)currentRow.getChildAt(1);
            list.add(currentEditText.getText().toString());
        }
    }
}
