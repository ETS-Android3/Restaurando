package com.kevinlamcs.android.restaurando.ui.model;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
 * Class that represents a user's thoughts on a particular restaurant.
 */
public class Thought extends TableRow implements View.OnClickListener {

    public static final String THOUGHT = "thought";

    private static final float WEIGHT = 1.0f;

    private static final int TEXT_SIZE = 16;
    private static final int THOUGHT_RIGHT_MARGIN = 144;

    private static TableLayout thoughtTable;
    private Context context;
    private TextView bulletPoint;

    public Thought(Context context) {
        super(context);
    }

    public Thought(Context context, TableLayout table) {
        super(context);
        this.context = context;
        thoughtTable = table;
    }

    @Override
    public void onClick(View v) {
        removeThought(thoughtTable);
    }

    /**
     * Sets up adding thoughts in a table layout starting with a bullet point in each row.
     */
    public void setupAddThought() {
        TableRow.LayoutParams thoughtRowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(thoughtRowParams);

        bulletPoint = new TextView(context);
        bulletPoint.setText("•");

        EditText editThought = new EditText(context);
        TableRow.LayoutParams thoughtParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                WEIGHT);
        editThought.setLayoutParams(thoughtParams);
        editThought.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);
        editThought.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        ImageButton buttonRemoveThought = new ImageButton(context);
        buttonRemoveThought.setImageResource(R.drawable.remove_thought);
        buttonRemoveThought.setBackgroundResource(R.drawable.button_square_rounded);
        buttonRemoveThought.setOnClickListener(this);

        addView(bulletPoint);
        addView(editThought);
        addView(buttonRemoveThought);

        thoughtTable.addView(this);
    }

    /**
     * Sets up displaying thoughts.
     * @param thought - User's thought
     */
    public void setupDisplayThought(String thought) {
        TableRow.LayoutParams thoughtRowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(thoughtRowParams);

        bulletPoint = new TextView(context);
        bulletPoint.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Regular.ttf"));
        bulletPoint.setTextSize(TEXT_SIZE);
        bulletPoint.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkText));
        bulletPoint.setText("• ");

        TextView displayThought = new TextView(context);
        displayThought.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Regular.ttf"));
        displayThought.setMaxWidth(getContext().getResources().getDisplayMetrics().widthPixels -
                THOUGHT_RIGHT_MARGIN);
        displayThought.setTextSize(TEXT_SIZE);
        displayThought.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkTextCaptionDark));
        displayThought.setText(thought);

        addView(bulletPoint);
        addView(displayThought);

        thoughtTable.addView(this);

    }

    /**
     * Set focus on the edit text.
     */
    public void focusThought() {
        requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Removes the thought.
     * @param table - table where the though resides
     */
    private void removeThought(TableLayout table) {
        table.removeView(this);
    }

    /**
     * Retrieve the thoughts to store them long term.
     * @param list - list to store thoughts
     * @param restaurant - restaurant that the thoughts belong to
     */
    public static void retrieveThought(List<String> list, Restaurant restaurant) {
        if (thoughtTable == null) {
            return;
        }

        for (int currentThought = 0; currentThought < thoughtTable.getChildCount(); currentThought++) {
            TableRow currentRow = (TableRow) thoughtTable.getChildAt(currentThought);
            if (currentRow.getChildAt(1) instanceof EditText) {
                EditText currentEditText = (EditText) currentRow.getChildAt(1);
                if (!(currentEditText.getText().toString().trim().length() == 0)) {
                    list.add(currentEditText.getText().toString());
                }
            }
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
