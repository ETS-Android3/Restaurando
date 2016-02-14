package com.kevinlamcs.android.restaurando.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.kevinlamcs.android.restaurando.Database.RestaurantDbSchema;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.FavoritesActivity;
import com.kevinlamcs.android.restaurando.ui.adapter.FavoritesAdapter;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.List;


public class FavoritesFragment extends Fragment {

    private static final String STATE_SELECTED_LIST = "FavoritesAdapter selected state list";

    public static List<Restaurant> restaurantList;

    private Restaurant mRestaurant;
    private RecyclerView mRecyclerView;
    private FavoritesAdapter mFavoritesRestaurantListAdapter;
    private SimpleCursorAdapter favoritesSuggestionListAdapter;

    private List<String> mListCategoriesToFilter;
    private boolean mIsSelectAllRestaurantsForRandomize;

    private FilterOptions filterOptions = new FilterOptions();

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_restaurant_list, container,
                false);
        mRecyclerView = (RecyclerView) view.findViewById
                (R.id.fragment_favorites_restaurant_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupSwipeToDelete();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FavoritesActivity.REQUEST_RESTAURANT && resultCode == Activity.RESULT_OK) {
            mRestaurant = data.getParcelableExtra(AddFragment.EXTRA_MY_RESTAURANT);
            // Move this to file for getting restaurant values
            if (mRestaurant.getCategory() != null) {
                mRestaurant.setCategoryId(mRestaurant.getCategory().substring(0, 1));
            }
            mFavoritesRestaurantListAdapter.addRestaurant(mRestaurant);
        } else if (requestCode == FavoritesActivity.REQUEST_FILTER && resultCode == Activity
                .RESULT_OK) {
            filterOptions = data.getParcelableExtra(FilterFragment.EXTRA_FILTER_OPTIONS);
            mFavoritesRestaurantListAdapter.filter(filterOptions);
        }
    }

    public FilterOptions setupFilterOptions() {
        List<String> categories = mFavoritesRestaurantListAdapter.getRestaurantCategories();
        filterOptions.setAllCategories(categories);
        return filterOptions;
    }

    public void updateUi() {
        if (mFavoritesRestaurantListAdapter == null) {
            mFavoritesRestaurantListAdapter = new FavoritesAdapter(getActivity());
            mRecyclerView.setAdapter(mFavoritesRestaurantListAdapter);
        } else {
            if (filterOptions.getFilteredCategories().isEmpty()) {
                mFavoritesRestaurantListAdapter.setRestaurantList();
            } else {
                mFavoritesRestaurantListAdapter.setRestaurantList(filterOptions.getFilteredCategories());
            }
            filterOptions.setIsFilterFinished(true);
            mFavoritesRestaurantListAdapter.filter(filterOptions);
        }
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Restaurant restaurant = mFavoritesRestaurantListAdapter.getRestaurant(
                        viewHolder.getAdapterPosition());
                mFavoritesRestaurantListAdapter.removeRestaurant(restaurant, viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public List<Restaurant> getActiveRestaurantList() {
        return mFavoritesRestaurantListAdapter.getCurrentRestaurantList();
    }
}
