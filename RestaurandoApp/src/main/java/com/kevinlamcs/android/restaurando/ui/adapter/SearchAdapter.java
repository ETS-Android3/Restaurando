package com.kevinlamcs.android.restaurando.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.AddActivity;
import com.kevinlamcs.android.restaurando.ui.fragment.FavoritesFragment;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.List;

/**
 * Adapter class for displaying the results from the Yelp search.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchRestaurantHolder> {

    public static final String EXTRA_YELP_RESTAURANT = "com.kevinlamcs.android.restaurando.ui" +
            ".extra.YELP_RESTAURANT";

    private List<Restaurant> restaurantList;

    public SearchAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public SearchRestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_search, parent, false);
        return new SearchRestaurantHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchRestaurantHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.bindSearchRestaurant(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    /**
     * Stores the list of restaurants retrieved from Yelp.
     * @param restaurantList - list of restaurants
     */
    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged();
    }

    /**
     * Holder class representing each restaurant retrieved from Yelp.
     */
    class SearchRestaurantHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnTouchListener {

        private Restaurant restaurant;
        private final LinearLayout container;
        private final TextView name;
        private final TextView streetAddress;
        private final TextView cityStateZip;
        private final TextView rating;
        private final ImageView star;

        public SearchRestaurantHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.list_item_search_restaurant_name);
            streetAddress = (TextView)itemView.findViewById(R.id.list_item_search_street_address);
            cityStateZip = (TextView)itemView.findViewById(R.id.list_item_search_city_state_zipcode);
            rating = (TextView)itemView.findViewById(R.id.list_item_search_rating);
            star = (ImageView)itemView.findViewById(R.id.list_item_search_star);
            container = (LinearLayout)itemView.findViewById(R.id.list_item_search_container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), AddActivity.class);
            intent.putExtra(EXTRA_YELP_RESTAURANT, restaurant);
            ((Activity) v.getContext()).startActivityForResult(intent, FavoritesFragment.REQUEST_RESTAURANT);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    container.setSelected(true);
                    break;
                case MotionEvent.ACTION_UP:
                    container.setSelected(false);
                    break;
            }
            return false;
        }

        /**
         * Sets the text and images to the holder for the restaurant.
         * @param restaurant - restaurant represented by the holder
         */
        private void bindSearchRestaurant(Restaurant restaurant) {
            int starImage;

            this.restaurant = restaurant;

            name.setText(this.restaurant.getName());
            if (this.restaurant.getStreetAddress() != null) {
                streetAddress.setText(this.restaurant.getStreetAddress());
                cityStateZip.setText(this.restaurant.getCityStateZip());
            } else {
                streetAddress.setText(R.string.no_address);
            }
            rating.setText(this.restaurant.getRating());
            switch((String) rating.getText()) {
                case "0":
                    starImage = R.drawable.zero_star;
                    break;
                case "1.0":
                    starImage = R.drawable.one_star;
                    break;
                case "1.5":
                    starImage = R.drawable.one_point_five_star;
                    break;
                case "2.0":
                    starImage = R.drawable.two_star;
                    break;
                case "2.5":
                    starImage = R.drawable.two_point_five_star;
                    break;
                case "3.0":
                    starImage = R.drawable.three_star;
                    break;
                case "3.5":
                    starImage = R.drawable.three_point_five_star;
                    break;
                case "4.0":
                    starImage = R.drawable.four_star;
                    break;
                case "4.5":
                    starImage = R.drawable.four_point_five_star;
                    break;
                case "5.0":
                    starImage = R.drawable.five_star;
                    break;
                default:
                    starImage = 0;
            }

            star.setImageResource(starImage);
        }
    }
}
