package com.kevinlamcs.android.restaurando.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kevinlamcs.android.restaurando.Database.RestaurantListing;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.InfoActivity;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.kevinlamcs.android.restaurando.utils.TextStyleUtils.setFavoritesListTextStyle;

/**
 * Created by kevin-lam on 1/13/16.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder> {

    public static final String EXTRA_INFO_RESTAURANT = "com.kevinlamcs.android.restaurando.ui" +
            ".adapter.info_restaurant";

    private final Context mContext;
    private RestaurantListing mRestaurantListing;
    private List<Restaurant> mListRestaurant;
    private Map<String,Boolean> mMapRestaurantSelectedState = new HashMap<>();

    private FilterOptions filterOptions = new FilterOptions();

    public FavoritesAdapter(Context context) {
        mContext = context;
        mRestaurantListing = RestaurantListing.get(context);
        mListRestaurant = mRestaurantListing.getRestaurantList(filterOptions.getSortBy());

        for (Restaurant restaurant : mListRestaurant) {
            mMapRestaurantSelectedState.put(restaurant.getId(), false);
        }
    }

    @Override
    public FavoritesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_favorites, parent, false);
        return new FavoritesHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoritesHolder holder, int position) {
        Restaurant restaurant = mListRestaurant.get(position);
        holder.bindRestaurant(restaurant);
    }

    @Override
    public int getItemCount() {
        return mListRestaurant.size();
    }

    public void setRestaurantList() {
        mListRestaurant = getRestaurantList();
        if (!filterOptions.isFilterFinished()) {
            mMapRestaurantSelectedState.clear();
        }
        updateSelectedStates(mListRestaurant, filterOptions.isSelectAllFiltered());
        notifyDataSetChanged();
    }

    public void setRestaurantList(List<String> categories) {
        if (categories.isEmpty()) {
            mListRestaurant = getRestaurantList();
            updateSelectedStates(mListRestaurant, filterOptions.isSelectAllFiltered());
        } else {
            // Get selected restaurants IDs.
            List<String> listSelectedRestaurantsId = getSelectedRestaurants();

            // Get list of selected restaurants and list of filtered restaurants. Remove duplicates.
            mListRestaurant = getRestaurantList(categories);
            List<Restaurant> listSelectedRestaurants = new ArrayList<>();
            if (!listSelectedRestaurantsId.isEmpty()) {
                listSelectedRestaurants = getSelectedRestaurantList(listSelectedRestaurantsId);
                mListRestaurant = removeDuplicateRestaurants(mListRestaurant, listSelectedRestaurants);
            }

            // Add checked states of new restaurants. Mark them checked/unchecked depending on
            // filter selection.
            updateSelectedStates(mListRestaurant, filterOptions.isSelectAllFiltered());

            // Insert previously selected restaurants to end of new list
            mListRestaurant.addAll(listSelectedRestaurants);
            notifyDataSetChanged();
        }
    }

    public List<Restaurant> getRestaurantList() {
        return mRestaurantListing.getRestaurantList(filterOptions.getSortBy());
    }

    public List<Restaurant> getRestaurantList(List<String> categories) {
        return mRestaurantListing.getRestaurantList(categories, RestaurantListing
                .FILTER_TYPE_CATEGORY, filterOptions.getSortBy());
    }

    public List<Restaurant> getSelectedRestaurantList(List<String> selectedRestaurants) {
        return mRestaurantListing.getRestaurantList(selectedRestaurants, RestaurantListing
                .FILTER_TYPE_SELECTED, filterOptions.getSortBy());
    }

    public Restaurant getRestaurant(int position) {
        return mListRestaurant.get(position);
    }

    public void addRestaurant(Restaurant restaurant) {
        mRestaurantListing.addRestaurant(restaurant);
        setRestaurantList();
        mMapRestaurantSelectedState.put(restaurant.getId(), false);

    }

    public void removeRestaurant(Restaurant restaurant, int position) {
        mRestaurantListing.removeRestaurant(restaurant);
        setRestaurantList();
        mMapRestaurantSelectedState.remove(restaurant.getId());
    }

    public List<String> getRestaurantCategories() {
        Set<String> categoriesSet = new TreeSet<>();
        List<Restaurant> allRestaurantList = getRestaurantList();
        for (Restaurant restaurant : allRestaurantList) {
            categoriesSet.add(restaurant.getCategory());
        }

        return new ArrayList<>(categoriesSet);
    }


    public void filter(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
    }

    // Check for empty return list
    private List<String> getSelectedRestaurants() {
        List<String> listSelectedRestaurants = new ArrayList<>();
        for (Iterator<Map.Entry<String, Boolean>> it = mMapRestaurantSelectedState.entrySet()
                .iterator(); it.hasNext();) {
            Map.Entry<String, Boolean> entry = it.next();
            if (entry.getValue() == true) {
                listSelectedRestaurants.add(entry.getKey());
            } else {
                it.remove();
            }
        }
        return listSelectedRestaurants;
    }

    public List<Restaurant> getCurrentRestaurantList() {
        return new ArrayList<>(mListRestaurant);
    }

    public List<Restaurant> removeDuplicateRestaurants(List<Restaurant> filteredRestaurantList,
                                                      List<Restaurant> selectedRestaurantList) {
        LinkedHashSet<Restaurant> comparisonHashSet = new LinkedHashSet<>(filteredRestaurantList);
        for (Restaurant restaurant : selectedRestaurantList) {
            if (comparisonHashSet.contains(restaurant)) {
                comparisonHashSet.remove(restaurant);
            }
        }

        return new ArrayList<Restaurant>(comparisonHashSet);
    }

    private void updateSelectedStates(List<Restaurant> restaurantList, boolean
            isSelectAllFiltered) {
        for (Restaurant restaurant : restaurantList) {
            if (!mMapRestaurantSelectedState.containsKey(restaurant.getId())) {
                if (isSelectAllFiltered) {
                    mMapRestaurantSelectedState.put(restaurant.getId(), true);
                } else {
                    mMapRestaurantSelectedState.put(restaurant.getId(), false);
                }
            }
        }
    }

    class FavoritesHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View
            .OnLongClickListener {

        private Restaurant mRestaurant;
        private TextView mRestaurantName;
        private TextView mCategoryName;
        private TextView mRating;
        private ImageView mCategoryImage;
        private TextView mRatingToRandomize;
        private ImageView mCheckToRandomize;

        public FavoritesHolder(View itemView) {
            super(itemView);

            mRestaurantName = (TextView) itemView.findViewById(R.id.list_item_favorites_name);
            mCategoryName = (TextView) itemView.findViewById(R.id
                    .list_item_favorites_category_name);
            mRating = (TextView) itemView.findViewById(R.id.list_item_favorites_rating);
            mCategoryImage = (ImageView) itemView.findViewById(R.id
                    .list_item_favorites_category_image);
            mRatingToRandomize = (TextView) itemView.findViewById(R.id
                    .list_item_favorites_rating_for_randomize);
            mCheckToRandomize = (ImageView) itemView.findViewById(R.id
                    .list_item_favorites_check_for_randomize);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), InfoActivity.class);
            intent.putExtra(EXTRA_INFO_RESTAURANT, mRestaurant);
            v.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            Restaurant restaurant = mListRestaurant.get(getAdapterPosition());
            boolean isSelectedForRandomize = mMapRestaurantSelectedState.get(restaurant.getId());
            mMapRestaurantSelectedState.put(restaurant.getId(), !isSelectedForRandomize);
            toggleSelectedForRandomize(!isSelectedForRandomize);
            return true;
        }

        private void bindRestaurant(Restaurant restaurant) {
            mRestaurant = restaurant;

            mRestaurantName.setText(mRestaurant.getName());
            setFavoritesListTextStyle(mContext, mRestaurantName, "font/Roboto-Regular.ttf");

            mCategoryName.setText(mRestaurant.getCategory());
            setFavoritesListTextStyle(mContext, mCategoryName, "font/Roboto-Regular.ttf");

            mRating.setText(mRestaurant.getRating());
            setFavoritesListTextStyle(mContext, mRating, "font/Roboto-Regular.ttf");

            mRatingToRandomize.setText(mRestaurant.getRating());
            setFavoritesListTextStyle(mContext, mRatingToRandomize, "font/Roboto-Regular.ttf");

            toggleSelectedForRandomize(mMapRestaurantSelectedState.get(restaurant.getId()));

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(mRestaurant.getId());
            TextDrawable.IBuilder textBuilder = TextDrawable.builder().round();
            TextDrawable textDrawable = textBuilder.build(mRestaurant.getCategoryId(), color);
            mCategoryImage.setImageDrawable(textDrawable);
        }

        private void toggleSelectedForRandomize(boolean isSelectedForRandomize) {
            if (isSelectedForRandomize) {
                mRating.setVisibility(View.INVISIBLE);
                mRatingToRandomize.setVisibility(View.VISIBLE);
                mCheckToRandomize.setVisibility(View.VISIBLE);
                mRestaurant.setIsSelectedForRandom(true);
            } else {
                mRating.setVisibility(View.VISIBLE);
                mRatingToRandomize.setVisibility(View.INVISIBLE);
                mCheckToRandomize.setVisibility(View.INVISIBLE);
                mRestaurant.setIsSelectedForRandom(false);
            }
        }
    }
}