package com.kevinlamcs.android.restaurando.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Adapter class for manipulating the dialog list of restaurant categories.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    private final List<String> unfilteredCategoryList;
    private final List<String> filteredCategoryList;
    private final HashSet<String> previouslySelectedCategoryList;
    private HashSet<String> currentSelectedCategoryList = new HashSet<>();
    private final List<Boolean> checkedState = new ArrayList<>();

    private final FilterOptions filterOptions;

    /**
     * Initializes the filter adapter.
     * @param filterOptions - User's previous filter selection
     */
    public FilterAdapter(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
        unfilteredCategoryList = filterOptions.getAllCategories();
        filteredCategoryList = filterOptions.getFilteredCategories();
        previouslySelectedCategoryList = new HashSet<>(filteredCategoryList);
        currentSelectedCategoryList = new HashSet<>(previouslySelectedCategoryList);

        // Initializes the default state of the checkboxes to unchecked
        for (int index=0; index < unfilteredCategoryList.size(); index++) {
            checkedState.add(false);
        }
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_filter, parent, false);
        return new FilterHolder(view);
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, int position) {
        String category = unfilteredCategoryList.get(position);
        holder.bindCategory(category, position);
    }

    @Override
    public int getItemCount() {
        return unfilteredCategoryList.size();
    }

    /**
     * Retrieves user's filter selection.
     * @return filter options
     */
    public FilterOptions getFilterOptions() {
        filterOptions.setFilteredCategories(new ArrayList<>(currentSelectedCategoryList));
        return filterOptions;
    }

    /**
     * Holder class representing all restaurant categories.
     */
    class FilterHolder extends RecyclerView.ViewHolder implements
            CompoundButton.OnCheckedChangeListener {

        private final CheckBox checkBoxCategory;

        public FilterHolder(View itemView) {
            super(itemView);
            checkBoxCategory = (CheckBox)itemView.findViewById(
                    R.id.list_item_filter_check_box_category);
            checkBoxCategory.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String category = (String)buttonView.getText();
            int position = Integer.parseInt(buttonView.getTag().toString());
            checkedState.set(position, isChecked);
            if (isChecked && !currentSelectedCategoryList.contains(category)) {
                currentSelectedCategoryList.add(category);
            } else if (!isChecked && currentSelectedCategoryList.contains(category)) {
                currentSelectedCategoryList.remove(category);
            }
        }

        /**
         * Sets the name of each category along with the checked state.
         * @param category - restaurant category
         * @param position - position of the category in the list
         */
        private void bindCategory(String category, int position) {
            checkBoxCategory.setText(category);
            checkBoxCategory.setTag(String.valueOf(position));
            if (previouslySelectedCategoryList.contains(category)) {
                previouslySelectedCategoryList.remove(category);
                checkedState.set(position, true);
            }
            checkBoxCategory.setChecked(checkedState.get(position));
        }
    }
}
