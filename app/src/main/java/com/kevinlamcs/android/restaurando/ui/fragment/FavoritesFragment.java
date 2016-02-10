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
    private FavoritesAdapter mFavoritesAdapter;

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

        //updateUi();
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
            mFavoritesAdapter.addRestaurant(mRestaurant);
        } else if (requestCode == FavoritesActivity.REQUEST_FILTER && resultCode == Activity
                .RESULT_OK) {
            filterOptions = data.getParcelableExtra(FilterFragment.EXTRA_FILTER_OPTIONS);
            mFavoritesAdapter.filter(filterOptions);
        }
    }

    public FilterOptions setupFilterOptions() {
        List<String> categories = mFavoritesAdapter.getRestaurantCategories();
        filterOptions.setAllCategories(categories);
        return filterOptions;
    }

    public void updateUi() {
        if (mFavoritesAdapter == null) {
            mFavoritesAdapter = new FavoritesAdapter(getActivity());
            mRecyclerView.setAdapter(mFavoritesAdapter);
        } else {
            /*if (filterOptions.getFilteredCategories() == null || filterOptions
                    .getFilteredCategories().isEmpty()) {
                mFavoritesAdapter.setRestaurantList();
            } else {*/
            if (filterOptions.getFilteredCategories().isEmpty()) {
                mFavoritesAdapter.setRestaurantList();
            } else {
                mFavoritesAdapter.setRestaurantList(filterOptions.getFilteredCategories());
            }
            filterOptions.setIsFilterFinished(true);
            mFavoritesAdapter.filter(filterOptions);
            //}
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
                Restaurant restaurant = mFavoritesAdapter.getRestaurant(
                        viewHolder.getAdapterPosition());
                mFavoritesAdapter.removeRestaurant(restaurant, viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
