package com.kevinlamcs.android.restaurando.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.database.RestaurantListing;
import com.kevinlamcs.android.restaurando.ui.activity.InfoActivity;
import com.kevinlamcs.android.restaurando.ui.callback.FavoritesAdapterCallback;
import com.kevinlamcs.android.restaurando.ui.model.FavoritesList;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Adapter class for manipulating the restaurant favorites list.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder> {

    public static final String EXTRA_INFO = "com.kevinlamcs.android.restaurando.ui.adapter.extra.INFO";

    private final RestaurantListing restaurantListing;
    private final FavoritesList<Restaurant> listFavorites;

    private FilterOptions filterOptions;

    private String currListName;

    private String singleSearchRestaurantId = "";

    private final FavoritesAdapterCallback favoritesAdapterCallback;


    /**
     * Initializes the favorites list.
     * @param context - context of the activity
     * @param favoritesAdapterCallback - callback to change the selected subheader and empty state
     * @param currListName - name of the current favorites list
     * @param listFavorites - favorites list to be saved to preferences
     * @param filterOptions - user's chosen filter options
     */
    public FavoritesAdapter(Context context, FavoritesAdapterCallback favoritesAdapterCallback,
                            String currListName, FavoritesList<Restaurant> listFavorites,
                            FilterOptions filterOptions) {

        // Initialize member variables
        this.currListName = currListName;
        this.favoritesAdapterCallback = favoritesAdapterCallback;
        this.filterOptions = filterOptions;
        restaurantListing = RestaurantListing.get(context);

        // Either retain the old favorites list or create a new one with all favorited restaurants
        if (listFavorites != null) {
            this.listFavorites = new FavoritesList<>(listFavorites);
        } else {
            this.listFavorites = new FavoritesList<>(new ArrayList<Restaurant>());
            List<Restaurant> listRestaurants = getRestaurantList(new ArrayList<String>());
            this.listFavorites.replaceAll(listRestaurants, false);
            this.filterOptions.setAllCategories(this.listFavorites.getAllCategories
                    (listRestaurants));
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
        Restaurant restaurant = listFavorites.get(position);
        holder.bindRestaurant(restaurant);
    }

    @Override
    public int getItemCount() {
        favoritesAdapterCallback.emptyStateVisibilityCallback(listFavorites.isEmpty());
        return listFavorites.size();
    }

    /**
     * Called when returning from filter activity. Reorders the favorites list depending on filter
     * options and refreshes.
     */
    public void onFilter() {

        // Sets sort by order
        listFavorites.setComparator(filterOptions.getSortBy());

        // Narrows either by filtered category or no category
        if (filterOptions.getFilteredCategories().isEmpty()) {
            listFavorites.replaceAll(getRestaurantList(filterOptions.getFilteredCategories()),
                    filterOptions.isSelectAllFiltered());
            notifyDataSetChanged();
        } else {
            listFavorites.replaceOld(getRestaurantList(filterOptions.getFilteredCategories()),
                    filterOptions.isSelectAllFiltered());
            notifyDataSetChanged();
        }

        toggleSubheader(false);
    }

    /**
     * Called when returning from single search. Narrows favorites list down to the searched item.
     */
    public void onSingleSearch() {
        listFavorites.replaceOld(getRestaurantById(singleSearchRestaurantId),
                filterOptions.isSelectAllFiltered());
        notifyDataSetChanged();
        toggleSubheader(false);
    }

    /**
     * Called when returning from selecting a new favorites list. Retrieves all the restaurants in
     * that list from the database and displays them.
     */
    public void onListChange() {
        List<Restaurant> listRestaurants = getRestaurantList(new ArrayList<String>());

        filterOptions.setIsSelectAllFiltered(false);
        listFavorites.replaceAll(listRestaurants, filterOptions.isSelectAllFiltered());
        filterOptions.setAllCategories(listFavorites.getAllCategories(listRestaurants));
        filterOptions.clearFilteredCategories();
        notifyDataSetChanged();
        favoritesAdapterCallback.emptyStateVisibilityCallback(listFavorites.isEmpty());
    }

    /**
     * Adds a new restaurant to the current favorites list.
     * @param restaurant - restaurant to be added
     */
    public void addRestaurant(Restaurant restaurant) {
        // Add the restaurant to database and then the favorites list
        restaurantListing.addRestaurant(currListName, restaurant);
        boolean addedNewItem = listFavorites.add(restaurant);
        filterOptions.setAllCategories(listFavorites.getCategories());

        // Item was added. If the item already exists, only the thoughts would be changed
        if (addedNewItem) {
            int changedPosition = listFavorites.getPosition(restaurant);
            notifyItemInserted(changedPosition);
            notifyItemRangeChanged(changedPosition, getItemCount());
            toggleSubheader(false);
        }
    }

    /**
     * Removes the restaurant from the favorites list
     * @param restaurant - restaurant to be removed
     */
    public void removeRestaurant(Restaurant restaurant) {
        restaurantListing.removeRestaurant(currListName, restaurant);
        int changedPosition = listFavorites.getPosition(restaurant);
        listFavorites.remove(restaurant);
        removeFilteredCategory(restaurant.getCategory());
        notifyItemRemoved(changedPosition);
        notifyItemRangeChanged(changedPosition, getItemCount());
        toggleSubheader(false);
    }

    /**
     * Remove the current restaurant favorites list.
     */
    public void removeRestaurantList() {
        restaurantListing.removeRestaurantList(currListName);
    }

    /**
     * Remove the restaurant's category from the list of categories.
     * @param category - category to be removed
     */
    private void removeFilteredCategory(String category) {
        if (listFavorites.hasCategory(category)) {
            return;
        }

        List<String> filteredCategories = filterOptions.getFilteredCategories();

        for (int i=0; i < filteredCategories.size(); i++) {
            if (filteredCategories.get(i).equals(category)) {
                filteredCategories.remove(i);
            }
        }
        filterOptions.setFilteredCategories(filteredCategories);
    }

    /**
     * Retrieves restaurants by category from the database.
     * @param categories - only restaurants with the listed categories will be returned
     * @return list of restaurants
     */
    private List<Restaurant> getRestaurantList(List<String> categories) {
        return restaurantListing.getRestaurantList(currListName, categories, filterOptions.getSortBy());
    }

    /**
     * Retrieves restaurants by id from the database.
     * @param restaurantId - only restaurants with the specified id will be returned
     * @return list of restaurants
     */
    private List<Restaurant> getRestaurantById(String restaurantId) {
        return restaurantListing.getRestaurantById(currListName, restaurantId);
    }

    /**
     * Retrieves the restaurant at the specified position from the favorites list.
     * @param position - Index in the favorites list
     * @return restaurant
     */
    public Restaurant getRestaurant(int position) {
        return listFavorites.get(position);
    }

    /**
     * Retrieves the favorites list
     * @return favorites list
     */
    public FavoritesList<Restaurant> getFavoritesList() {
        return listFavorites;
    }

    /**
     * Randomly retrieves a restaurant from those selected.
     * @return restaurant
     */
    public Restaurant getRandomRestaurant() {
        Random random = new Random();
        if (listFavorites.hasSelected()) {
            return listFavorites.getSelected(random.nextInt(listFavorites.getSelectedSize()));
        }
        return null;
    }

    /**
     * Retrieves the index in the favorites list where the selected items begin.
     * @return index position
     */
    public int getSelectedItemStartPosition() {
        return listFavorites.getSelectedItemStartPosition();
    }

    /**
     * Stores the restaurant being searched to display in favorites list.
     * @param restaurantId - id of restaurant being searched
     */
    public void setSingleSearchRestaurant(String restaurantId) {
        singleSearchRestaurantId = restaurantId;
    }

    /**
     * Stores the name of the active favorites restaurant list.
     * @param listName - name of the list
     */
    public void setCurrentRestaurantList(String listName) {
        this.currListName = listName;
    }

    /**
     * Stores the user's filter selection.
     * @param filterOptions - filter options
     */
    public void setFilterOptions(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
    }

    /**
     * Retrieves the user's filter selection.
     * @return filter options
     */
    public FilterOptions getFilterOptions() {
        return filterOptions;
    }

    /**
     * Makes the selected subheader visible or invisible if selected items exist.
     * @param state - whether selected items exist
     */
    public void toggleSubheader(boolean state) {
        if (state) {
            favoritesAdapterCallback.addSubheaderCallback();
        } else {
            if (listFavorites.hasSelected()) {
                favoritesAdapterCallback.addSubheaderCallback();
            } else {
                favoritesAdapterCallback.removeSubheaderCallback();
            }
        }
    }

    /**
     * Holder class for the individual restaurants in the favorites list
     */
    public class FavoritesHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnTouchListener {

        private static final int TEXT_DRAWABLE_SIZE = 24;

        private final RelativeLayout root;
        private Restaurant restaurant;
        private final TextView restaurantName;
        private final TextView categoryName;
        private final TextView rating;
        private final ImageView categoryImage;
        private final TextView ratingToRandomize;
        private final ImageView checkToRandomize;

        /**
         * Initializes the holder
         * @param itemView - view for the restaurant
         */
        public FavoritesHolder(View itemView) {
            super(itemView);

            root = (RelativeLayout) itemView.findViewById(R.id.list_item_favorites_root);
            restaurantName = (TextView) itemView.findViewById(R.id.list_item_favorites_name);
            categoryName = (TextView) itemView.findViewById(
                    R.id.list_item_favorites_category_name);
            rating = (TextView) itemView.findViewById(R.id.list_item_favorites_rating);
            categoryImage = (ImageView) itemView.findViewById(
                    R.id.list_item_favorites_category_image);
            ratingToRandomize = (TextView) itemView.findViewById(
                    R.id.list_item_favorites_rating_for_randomize);
            checkToRandomize = (ImageView) itemView.findViewById(
                    R.id.list_item_favorites_check_for_randomize);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), InfoActivity.class);
            intent.putExtra(EXTRA_INFO, restaurant);
            v.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            int previousPosition = getAdapterPosition();
            Restaurant restaurant = listFavorites.onSelected(previousPosition);
            boolean state = listFavorites.getSelectedState(restaurant);
            toggleSelectedForRandomize(state);
            int currentPosition = listFavorites.getPosition(restaurant);
            notifyItemMoved(previousPosition, currentPosition);
            toggleSubheader(state);
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    root.setSelected(true);
                    break;
                case MotionEvent.ACTION_UP:
                    root.setSelected(false);
                    break;
            }
            return false;
        }

        /**
         * Places text and images onto the holder.
         * @param restaurant - restaurant in which this holder represents
         */
        private void bindRestaurant(Restaurant restaurant) {
            this.restaurant = restaurant;

            restaurantName.setText(this.restaurant.getName());
            categoryName.setText(this.restaurant.getCategory());
            rating.setText(this.restaurant.getRating());
            ratingToRandomize.setText(this.restaurant.getRating());
            toggleSelectedForRandomize(listFavorites.getSelectedState(restaurant));

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(this.restaurant.getCategory());
            TextDrawable.IBuilder textBuilder = TextDrawable
                    .builder()
                    .roundRect(TEXT_DRAWABLE_SIZE);
            TextDrawable textDrawable = textBuilder.build(this.restaurant.getCategoryId(), color);
            categoryImage.setImageDrawable(textDrawable);
        }

        /**
         * Switches between the selected state and the unselected state for randomizing
         * @param isSelectedForRandomize - state
         */
        private void toggleSelectedForRandomize(boolean isSelectedForRandomize) {
            if (isSelectedForRandomize) {
                rating.setVisibility(View.INVISIBLE);
                ratingToRandomize.setVisibility(View.VISIBLE);
                checkToRandomize.setVisibility(View.VISIBLE);
            } else {
                rating.setVisibility(View.VISIBLE);
                ratingToRandomize.setVisibility(View.INVISIBLE);
                checkToRandomize.setVisibility(View.INVISIBLE);
            }
        }
    }
}