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
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;

import java.util.List;

/**
 * Fragment class for filtering restaurants from the favorited list.
 */
public class FilterFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String EXTRA_FILTER = "com.kevinlamcs.android.restaurando.ui" +
            ".extra.FILTER";

    private static final int REQUEST_SELECTED_CATEGORIES = 0;

    private static final int CATEGORY_OFFSET = -224;
    private static final int NAME_OFFSET = -416;
    private static final int RATING_OFFSET = -608;

    private TextView textViewDisplayedCategories;
    private TextView textViewClearCategories;
    private TextView textViewSortOption;
    private ListPopupWindow listPopupWindow;

    private FilterOptions filterOptions = new FilterOptions();

    /**
     * Constructs a new FilterFragment.
     * @return FilterFragment
     */
    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        filterOptions = getActivity()
                .getIntent()
                .getParcelableExtra(FavoritesFragment.EXTRA_FILTER_OPTIONS);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter, container, false);

        SwitchCompat switchSelectAllFilteredItems = (SwitchCompat) v.findViewById(
                R.id.fragment_filter_switch_select_all_filtered_items);
        switchSelectAllFilteredItems.setChecked(filterOptions.isSelectAllFiltered());
        switchSelectAllFilteredItems.setOnCheckedChangeListener(
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
        listPopupWindow.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_drop_down_item,
                getContext().getResources().getStringArray(R.array.sort_order_values)));
        listPopupWindow.setAnchorView(linearLayoutSortBy);
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(this);

        linearLayoutSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sortOption = (String)textViewSortOption.getText();
                switch (sortOption) {
                    case "Category":
                        listPopupWindow.setVerticalOffset(CATEGORY_OFFSET);
                        break;
                    case "Name":
                        listPopupWindow.setVerticalOffset(NAME_OFFSET);
                        break;
                    case "Rating":
                        listPopupWindow.setVerticalOffset(RATING_OFFSET);
                        break;
                }
                listPopupWindow.show();
            }
        });

        textViewDisplayedCategories = (TextView)v.findViewById(
                R.id.fragment_filter_displayed_categories);
        updateUi(filterOptions.getFilteredCategories(), filterOptions.getSortBy());

        textViewClearCategories = (TextView)v.findViewById(R.id.fragment_filter_clear_categories);
        textViewClearCategories.setOnClickListener(new View.OnClickListener() {
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
                CategoryDialogFragment dialog = CategoryDialogFragment.newInstance(filterOptions);
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
                data.putExtra(EXTRA_FILTER, filterOptions);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_SELECTED_CATEGORIES) {
            filterOptions = data.getParcelableExtra(CategoryDialogFragment.EXTRA_FILTER_SELECTION);
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

    /**
     * Updates the filter choices made by the user.
     * @param selectedCategories - Categories selected by user
     * @param sortOption - Sort order selected by user
     */
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

        textViewDisplayedCategories.setText(displayedCategoriesString);
        textViewSortOption.setText(sortOption);
    }
}
