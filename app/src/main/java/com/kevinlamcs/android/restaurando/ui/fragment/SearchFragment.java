package com.kevinlamcs.android.restaurando.ui.fragment;


import android.app.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.operations.YelpSearch;
import com.kevinlamcs.android.restaurando.ui.adapter.SearchAdapter;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final int TITLE_TEXT_SIZE = 20;

    private SearchView mSearchViewTerm;
    private SearchView mSearchViewLocation;

    private RecyclerView mRecyclerView;
    private List<Restaurant> mRestaurantList = new ArrayList<>();

    public static boolean isSearchNearby = true;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSearchViewLocation = (SearchView) getActivity().findViewById(R.id.search_bottom);
        setupSearchView(mSearchViewLocation, "Nearby");

        View v = inflater.inflate(R.layout.fragment_search_restaurant_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id
                .fragment_search_restaurant_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Test
        mRecyclerView.setAdapter(new SearchAdapter(mRestaurantList, getActivity()));

        return v;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_top, menu);

        mSearchViewTerm = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id
                .search_top));
        setupSearchView(mSearchViewTerm, "Search");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void onSearch() {
        String term = mSearchViewTerm.getQuery().toString();
        String location = mSearchViewLocation.getQuery().toString();
        if (location.isEmpty()) {
            location = LocationUtils.getNearbyGPSCoordinates(getContext());
            isSearchNearby = true;
        } else {
            isSearchNearby = false;
        }
        new BackgroundYelpSearch().execute(term, location);
    }


    private void setupSearchView(final SearchView searchView, String hint) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context
                .SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo
                (getActivity().getComponentName()));
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setQueryHint(hint);
        if (searchView == mSearchViewLocation) {
            searchView.clearFocus();
        }

        searchView.setFocusable(false);


        // Set searchView text size to 20sp
        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoCompleteTextView.setTextSize(TITLE_TEXT_SIZE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch();
                mSearchViewTerm.clearFocus();
                mSearchViewLocation.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private class BackgroundYelpSearch extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return new YelpSearch().queryYelp(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurantList) {
            mRestaurantList = restaurantList;

            if (isAdded()) {
                mRecyclerView.setAdapter(new SearchAdapter(mRestaurantList, getActivity()));
            }
        }
    }
}
