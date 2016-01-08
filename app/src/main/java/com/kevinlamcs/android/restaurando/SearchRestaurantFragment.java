package com.kevinlamcs.android.restaurando;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchRestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchRestaurantFragment extends Fragment {

    public static SearchRestaurantFragment newInstance() {
        SearchRestaurantFragment fragment = new SearchRestaurantFragment();
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
        return inflater.inflate(R.layout.fragment_search_restaurant, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_restaurant, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context
                .SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconified(false);
        searchView.onActionViewExpanded();;
        LinearLayout ll1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll1.getChildAt(2);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ll3.getChildAt(0);
        autoCompleteTextView.setTextSize(20);
    }


}
