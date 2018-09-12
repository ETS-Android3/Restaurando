package com.kevinlamcs.android.restaurando.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.database.RestaurantListing;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.widget.SameSelectionSpinner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Adapter class for the list of favorite list names.
 */
public class ToolbarSpinnerAdapter extends BaseAdapter {

    private static final String NEW_TITLE = "New";
    private static final String DROP_DOWN = "Drop down";
    private static final String NON_DROPDOWN = "Non drop down";
    private static final int QUEUE_INCREMENT = 100;

    private final List<String> listFavoritesList = new ArrayList<>();

    private PriorityQueue<String> newTitleQueueFree;
    private PriorityQueue<String> newTitleQueueInUse;
    private int newTitleCount = 0;

    private final Context context;

    private int currentPosition = 0;
    private final SameSelectionSpinner spinner;

    /**
     * Initialize the adapter and set up the queue for new title items.
     * @param context - context of activity
     * @param spinner - drop down spinner that holds the title items
     */
    public ToolbarSpinnerAdapter(Context context, SameSelectionSpinner spinner) {
        this.context = context;
        this.spinner = spinner;
        setUpNewTitleNameQueue();
    }

    @Override
    public int getCount() {
        return listFavoritesList.size();
    }

    @Override
    public Object getItem(int position) {
        return listFavoritesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(NON_DROPDOWN)) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_toolbar_actionbar, parent, false);
            view.setTag(NON_DROPDOWN);
        }
        TextView textView = (TextView) view.findViewById(R.id.spinner_toolbar_actionbar_edit_text);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(DROP_DOWN)) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_toolbar_list_drop_down_item, parent, false);
            view.setTag(DROP_DOWN);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    /**
     * Adds a new favorites list to the spinner.
     */
    public void add() {

        // Initialize the free newTitle queue if its empty
        if (newTitleQueueFree.isEmpty()) {
            newTitleCount += QUEUE_INCREMENT;

            for (int i=newTitleCount; i <= (newTitleCount+QUEUE_INCREMENT); i++) {
                newTitleQueueFree.add(NEW_TITLE + (i + 1));
            }
        }

        // Put the newTitle to the in-use queue and then into the spinner
        String newTitle = newTitleQueueFree.poll();
        newTitleQueueInUse.add(newTitle);
        listFavoritesList.add(newTitle);
        spinner.setSelection(listFavoritesList.size() - 1);
        notifyDataSetChanged();
    }

    /**
     * Stores all favorite list titles into the spinner.
     * @param listFavoritesList - list titles
     */
    public void addItems(List<String> listFavoritesList) {
        this.listFavoritesList.addAll(listFavoritesList);
        setUpNewTitleNameQueue();
        notifyDataSetChanged();
    }

    /**
     * Retrieves the name of the active restaurant list.
     * @return name
     */
    public String getTitle() {
        return currentPosition >= 0 && currentPosition < listFavoritesList.size() ? listFavoritesList.get
                (currentPosition) : "";
    }

    /**
     * Retrieves the name of the restaurant list at the specified position.
     * @param position - specified index
     * @return name
     */
    private String getTitle(int position) {
        return position >= 0 && position < listFavoritesList.size() ? listFavoritesList.get
                (position) : "";
    }

    /**
     * Retrieves a list of all the favorites list names.
     * @return list of names
     */
    public List<String> getListRestaurantList() {
        return listFavoritesList;
    }

    /**
     * Stores the index of the active restaurant list in the spinner.
     * @param position - index of the active restaurant list
     */
    public void setTitlePosition(int position) {
        currentPosition = position;
    }

    /**
     * Changes the name of the active restaurant list.
     * @param oldTitleName - previous restaurant list name
     * @param newTitleName - new restaurant list name
     */
    public void rename(String oldTitleName, String newTitleName) {
        listFavoritesList.set(currentPosition, newTitleName);
        RestaurantListing restaurantListing = new RestaurantListing(context);
        restaurantListing.renameRestaurantList(oldTitleName, newTitleName);
        notifyDataSetChanged();
    }

    /**
     * Removes the active restaurant list from the spinner.
     */
    public void remove() {

        // Removes name from the in-use queue and makes it free.
        String titleName = listFavoritesList.get(currentPosition);
        recycleNewTitle(titleName);
        listFavoritesList.remove(currentPosition);

        // If the spinner is empty, add a default empty list in.
        if (listFavoritesList.isEmpty()) {
            add();
        }

        // Update the position in the spinner
        int newPosition;
        if (currentPosition == 0) {
            newPosition = 0;
        } else if (currentPosition == (listFavoritesList.size())) {
            newPosition = currentPosition - 1;
        } else {
            newPosition = currentPosition;
        }

        spinner.setSelection(newPosition);
        notifyDataSetChanged();
    }

    /**
     * Initializes the newTitle queues.
     */
    private void setUpNewTitleNameQueue() {
        PriorityQueueSort priorityQueueSort = new PriorityQueueSort();
        newTitleQueueFree = new PriorityQueue<>(QUEUE_INCREMENT, priorityQueueSort);
        newTitleQueueInUse = new PriorityQueue<>(QUEUE_INCREMENT, priorityQueueSort);
        for (int i=0; i <= QUEUE_INCREMENT; i++) {
            newTitleQueueFree.add(NEW_TITLE + (i + 1));
        }

        for (String title : listFavoritesList) {
            assignNewTitle(title);
        }
    }

    /**
     * Do not allow duplicate list names.
     * @param newTitleName - name that is to be checked for duplicates
     * @return true if duplicates exist. False otherwise
     */
    public boolean hasDuplicate(String newTitleName) {
        return listFavoritesList.contains(newTitleName);
    }

    /**
     * Reuse default list titles.
     * @param oldTitleName - name to be recycled.
     */
    public void recycleNewTitle(String oldTitleName) {
        if (newTitleQueueInUse.contains(oldTitleName)) {
            newTitleQueueInUse.remove(oldTitleName);
            newTitleQueueFree.add(oldTitleName);
        }
    }

    /**
     * Use default list titles.
     * @param newTitleName - name to be used.
     */
    private void assignNewTitle(String newTitleName) {
        if (newTitleQueueFree.contains(newTitleName)) {
            newTitleQueueFree.remove(newTitleName);
            newTitleQueueInUse.add(newTitleName);
        }
    }

    /**
     * Comparator class for the priority queue to order the default list titles.
     */
    private static class PriorityQueueSort implements Comparator<String> {

        @Override
        public int compare(String lhs, String rhs) {
            String num1 = lhs.substring(3);
            String num2 = rhs.substring(3);

            return Integer.parseInt(num1) - Integer.parseInt(num2);
        }
    }
}
