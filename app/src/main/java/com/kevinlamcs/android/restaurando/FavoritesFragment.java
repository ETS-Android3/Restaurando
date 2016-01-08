package com.kevinlamcs.android.restaurando;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.ViewHolder;


public class FavoritesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private FavoritesAdapater mFavoritesAdapater;

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
                (R.id.fragment_restaurant_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //Test
        if (mFavoritesAdapater == null) {
            Restaurant restaurant1 = new Restaurant();
            restaurant1.setName("Happy Ending Palace");
            Restaurant restaurant2 = new Restaurant();
            restaurant2.setName("Maid Cumsluts Heaven");
            ArrayList<Restaurant> arrayList = new ArrayList<>();
            arrayList.add(restaurant1);
            arrayList.add(restaurant2);
            for (int i=0; i < 12; i++) {
                arrayList.add(restaurant1);
            }
            mFavoritesAdapater = new FavoritesAdapater(arrayList);
            mRecyclerView.setAdapter(mFavoritesAdapater);
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

    private class FavoritesAdapater extends RecyclerView.Adapter<FavoritesHolder> {

        private List<Restaurant> mRestaurants;

        public FavoritesAdapater(List<Restaurant> restaurants) {
            mRestaurants = restaurants;
        }

        @Override
        public FavoritesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_restaurant, parent, false);
            return new FavoritesHolder(view);
        }

        @Override
        public void onBindViewHolder(FavoritesHolder holder, int position) {
            Restaurant restaurant = mRestaurants.get(position);
            holder.bindRestaurant(restaurant);
        }

        @Override
        public int getItemCount() {
            return mRestaurants.size();
        }
    }

    private class FavoritesHolder extends ViewHolder implements View.OnClickListener {

        private Restaurant mRestaurant;
        private TextView mRestaurantName;
        private TextView mCategoryName;
        private TextView mRating;
        private ImageView mCategoryImage;

        public FavoritesHolder(View itemView) {
            super(itemView);

            mRestaurantName = (TextView) itemView.findViewById(R.id.list_item_restaurant_name);
            mCategoryName = (TextView) itemView.findViewById(R.id
                    .list_item_restaurant_category_name);
            mRating = (TextView) itemView.findViewById(R.id.list_item_restaurant_rating);
            mCategoryImage = (ImageView) itemView.findViewById(R.id
                    .list_item_restaurant_category_image);
        }

        @Override
        public void onClick(View v) {

        }

        public void bindRestaurant(Restaurant restaurant) {
            mRestaurant = restaurant;

            mRestaurantName.setText(mRestaurant.getName());
            setFont(mRestaurantName, "font/Roboto-Regular.ttf");

            mCategoryName.setText("Mexican");
            setFont(mCategoryName, "font/Roboto-Regular.ttf");

            mRating.setText("9.1");
            setFont(mCategoryName, "font/Roboto-Regular.ttf");

            TextDrawable.IBuilder textBuilder = TextDrawable.builder().round();
            TextDrawable textDrawable = textBuilder.build("M", getResources().getColor(R.color.colorMexican));

            mCategoryImage.setImageDrawable(textDrawable);
        }
    }

    public void setFont(TextView textView, String path) {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), path);
        textView.setTypeface(typeface);
    }
}
