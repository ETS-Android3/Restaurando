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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kevin-lam on 1/21/16.
 */
public class Thought extends TableRow implements View.OnClickListener {

    public static final String THOUGHT = "thought";

    private static final float WEIGHT = 1.0f;

    private static TableLayout mThoughtTable;
    private Context mContext;
    private TextView mBulletPoint;
    private EditText mEditThought;
    private TextView mDisplayThought;
    private ImageButton mButtonRemoveThought;

    private int mThoughtPosition;

    public Thought(Context context, TableLayout table) {
        super(context);
        mContext = context;
        mThoughtTable = table;
    }

    @Override
    public void onClick(View v) {
        removeThought(mThoughtTable);
    }

    public void setupAddThought() {
        TableRow.LayoutParams thoughtRowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(thoughtRowParams);

        mBulletPoint = new TextView(mContext);
        mBulletPoint.setText("•");

        mEditThought = new EditText(mContext);
        TableRow.LayoutParams thoughtParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                WEIGHT);
        mEditThought.setLayoutParams(thoughtParams);

        mButtonRemoveThought = new ImageButton(mContext);
        mButtonRemoveThought.setImageResource(R.drawable.remove_thought);
        mButtonRemoveThought.setBackgroundResource(android.R.color.transparent);
        mButtonRemoveThought.setOnClickListener(this);

        addView(mBulletPoint);
        addView(mEditThought);
        addView(mButtonRemoveThought);

        mThoughtTable.addView(this);
    }

    public void setupDisplayThought(String thought) {
        TableRow.LayoutParams thoughtRowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(thoughtRowParams);

        mBulletPoint = new TextView(mContext);
        mBulletPoint.setText("• ");

        mDisplayThought = new TextView(mContext);
        mDisplayThought.setText(thought);

        addView(mBulletPoint);
        addView(mDisplayThought);

        mThoughtTable.addView(this);

    }

    public void focusThought() {
        requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
    }

    private void removeThought(TableLayout table) {
        table.removeView(this);
    }

    public static void retrieveThought(List<String> list, Restaurant restaurant) {
        if (mThoughtTable == null) {
            return;
        }

        for (int currentThought = 0; currentThought < mThoughtTable.getChildCount(); currentThought++) {
            TableRow currentRow = (TableRow)mThoughtTable.getChildAt(currentThought);
            EditText currentEditText = (EditText)currentRow.getChildAt(1);
            list.add(currentEditText.getText().toString());
        }

        JSONObject json = new JSONObject();
        try {
            json.put(THOUGHT, new JSONArray(list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        restaurant.setThoughtList(json.toString());
    }
}
