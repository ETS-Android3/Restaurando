package com.kevinlamcs.android.restaurando.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.FavoritesActivity;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;

import java.util.List;

/**
 * Created by kevin-lam on 1/28/16.
 */
public class FilterFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String EXTRA_FILTER_OPTIONS = "com.kevinlamcs.android.restaurando.ui" +
            ".fragment.FilterFragment.filterOptions";

    private static final int REQUEST_SELECTED_CATEGORIES = 0;

    private SwitchCompat mSwitchSelectAllFilteredItems;
    private TextView mTextViewDisplayedCategories;
    private TextView mTextViewClearCategories;
    private TextView textViewSortOption;
    private ListPopupWindow listPopupWindow;

    private FilterOptions filterOptions = new FilterOptions();

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        filterOptions = getActivity()
                .getIntent()
                .getParcelableExtra(FavoritesActivity.EXTRA_FILTER_OPTIONS);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter, container, false);

        mSwitchSelectAllFilteredItems = (SwitchCompat)v.findViewById(
                R.id.fragment_filter_switch_select_all_filtered_items);
        mSwitchSelectAllFilteredItems.setChecked(filterOptions.isSelectAllFiltered());
        mSwitchSelectAllFilteredItems.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        filterOptions.setIsSelectAllFiltered(isChecked);
                    }
                });

        LinearLayout linearLayoutSortBy = (LinearLayout)v.findViewById(R.id
                .fragment_filter_sort_by);
        textViewSortOption = (TextView)v.findViewById(R.id.fragment_filter_displayed_sort_option);
        listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(new ArrayAdapter(getContext(), R.layout.list_drop_down_item,
                new String[]{"Category", "Name", "Rating", "Distance"}));
        listPopupWindow.setAnchorView(linearLayoutSortBy);
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setVerticalOffset(-224);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(this);
        linearLayoutSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });



        mTextViewDisplayedCategories = (TextView)v.findViewById(
                R.id.fragment_filter_displayed_categories);
        updateUi(filterOptions.getFilteredCategories(), filterOptions.getSortBy());

        mTextViewClearCategories = (TextView)v.findViewById(R.id.fragment_filter_clear_categories);
        mTextViewClearCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOptions.clearFilteredCategories();
                updateUi(filterOptions.getFilteredCategories(), filterOptions.getSortBy());
            }
        });

        LinearLayout linearLayout = (LinearLayout)v.findViewById(R.id.fragment_filter_filter_categories);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DialogCategoryFragment dialog = DialogCategoryFragment.newInstance(filterOptions);
                dialog.setTargetFragment(FilterFragment.this, REQUEST_SELECTED_CATEGORIES);
                dialog.show(fragmentManager, "dialog");
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                filterOptions.setIsFilterFinished(false);

                Intent data = new Intent();
                data.putExtra(EXTRA_FILTER_OPTIONS, filterOptions);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_SELECTED_CATEGORIES) {
            filterOptions = data.getParcelableExtra(DialogCategoryFragment.EXTRA_FILTER_OPTIONS);
            updateUi(filterOptions.getFilteredCategories(), filterOptions.getSortBy());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String sortOption = (String)parent.getItemAtPosition(position);
        filterOptions.setSortBy(sortOption);
        updateUi(filterOptions.getFilteredCategories(), filterOptions.getSortBy());
        listPopupWindow.dismiss();

    }

    private void updateUi(List<String> selectedCategories, String sortOption) {
        String displayedCategoriesString = "";

        if (selectedCategories.isEmpty()) {
            displayedCategoriesString = displayedCategoriesString + "None";
        } else {
            for (int index=0; index < selectedCategories.size(); index++) {
                displayedCategoriesString = displayedCategoriesString + selectedCategories.get(index);
                if (index + 1 >= selectedCategories.size()) {
                    break;
                }
                displayedCategoriesString = displayedCategoriesString + ", ";
            }
        }

        mTextViewDisplayedCategories.setText(displayedCategoriesString);
        textViewSortOption.setText(sortOption);

    }
}
