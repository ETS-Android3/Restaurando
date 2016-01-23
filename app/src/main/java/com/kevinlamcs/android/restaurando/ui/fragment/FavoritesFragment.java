package com.kevinlamcs.android.restaurando.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.FavoritesActivity;
import com.kevinlamcs.android.restaurando.ui.adapter.FavoritesAdapter;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.ArrayList;
import java.util.List;


public class FavoritesFragment extends Fragment {
    private Restaurant mRestaurant;
    private RecyclerView mRecyclerView;
    private FavoritesAdapter mFavoritesAdapter;
    private List<Restaurant> mRestaurantList;

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


        //Test
        if (mFavoritesAdapter == null) {
            mRestaurantList = new ArrayList<>();
            mFavoritesAdapter = new FavoritesAdapter(getContext(), mRestaurantList);
            mRecyclerView.setAdapter(mFavoritesAdapter);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRestaurant != null) {
            mRestaurantList.add(mRestaurant);
            mRecyclerView.setAdapter(mFavoritesAdapter);
        }
        mRestaurant = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FavoritesActivity.REQUEST_RESTAURANT) {
            if (resultCode == Activity.RESULT_OK) {
                mRestaurant = data.getParcelableExtra(AddFragment.EXTRA_MY_RESTAURANT);
            }
        }
    }

}
