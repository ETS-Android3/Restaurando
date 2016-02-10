package com.kevinlamcs.android.restaurando.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.fragment.DialogCategoryFragment;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kevin-lam on 1/30/16.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    private List<String> mUnfilteredCategoryList;
    private List<String> mFilteredCategoryList;
    private Set<String> mPreviouslySelectedCategoryList;
    private Set<String> mCurrentSelectedCategoryList = new HashSet<>();
    private List<Boolean> mCheckedState = new ArrayList<>();

    private FilterOptions filterOptions;

    public FilterAdapter(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
        mUnfilteredCategoryList = filterOptions.getAllCategories();
        mFilteredCategoryList = filterOptions.getFilteredCategories();
        mPreviouslySelectedCategoryList = new HashSet<>(mFilteredCategoryList);
        mCurrentSelectedCategoryList = (HashSet)((HashSet) mPreviouslySelectedCategoryList).clone();

        for (int index=0; index < mUnfilteredCategoryList.size(); index++) {
            mCheckedState.add(false);
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
        String category = mUnfilteredCategoryList.get(position);
        holder.bindCategory(category, position);
    }

    @Override
    public int getItemCount() {
        return mUnfilteredCategoryList.size();
    }

    public FilterOptions getFilterOptions() {
        filterOptions.setFilteredCategories(new ArrayList<String>(mCurrentSelectedCategoryList));
        return filterOptions;
    }

    class FilterHolder extends RecyclerView.ViewHolder implements CompoundButton
            .OnCheckedChangeListener {

        private CheckBox mCheckBoxCategory;

        public FilterHolder(View itemView) {
            super(itemView);
            mCheckBoxCategory = (CheckBox)itemView.findViewById(R.id
                    .list_item_filter_check_box_category);
            mCheckBoxCategory.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String category = (String)buttonView.getText();
            int position = Integer.parseInt(buttonView.getTag().toString());
            mCheckedState.set(position, isChecked);
            if (isChecked && !mCurrentSelectedCategoryList.contains(category)) {
                mCurrentSelectedCategoryList.add(category);
            } else if (!isChecked && mCurrentSelectedCategoryList.contains(category)) {
                mCurrentSelectedCategoryList.remove(category);
            }
        }

        private void bindCategory(String category, int position) {
            mCheckBoxCategory.setText(category);
            mCheckBoxCategory.setTag(String.valueOf(position));
            if (mPreviouslySelectedCategoryList.contains(category)) {
                mPreviouslySelectedCategoryList.remove(category);
                mCheckedState.set(position, true);
            }
            mCheckBoxCategory.setChecked(mCheckedState.get(position));
        }
    }
}
