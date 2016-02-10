package com.kevinlamcs.android.restaurando.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.AddActivity;
import com.kevinlamcs.android.restaurando.ui.activity.FavoritesActivity;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.List;

/**
 * Created by kevin-lam on 1/12/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchRestaurantHolder> {

    public static final String EXTRA_YELP_RESTAURANT = "com.kevinlamcs.android.restaurando.ui" +
            ".adapter.yelp_restaurant";

    private List<Restaurant> mRestaurantList;
    protected Activity mActivity;

    public SearchAdapter(List<Restaurant> restaurantList, Activity activity) {
        mRestaurantList = restaurantList;
        mActivity = activity;
    }

    @Override
    public SearchRestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_search, parent, false);
        return new SearchRestaurantHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchRestaurantHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.bindSearchRestaurant(restaurant);
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    class SearchRestaurantHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Restaurant mRestaurant;
        private RelativeLayout mContainer;
        private TextView mName;
        private TextView mStreetAddress;
        private TextView mCityStateZip;
        private TextView mRating;
        private ImageView mStar;

        public SearchRestaurantHolder(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.list_item_search_restaurant_name);
            mStreetAddress = (TextView)itemView.findViewById(R.id.list_item_search_street_address);
            mCityStateZip = (TextView)itemView.findViewById(R.id.list_item_search_city_state_zipcode);
            mRating = (TextView)itemView.findViewById(R.id.list_item_search_rating);
            mStar = (ImageView)itemView.findViewById(R.id.list_item_search_star);
            mContainer = (RelativeLayout)itemView.findViewById(R.id.list_item_search_container);
            mContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), AddActivity.class);
            intent.putExtra(EXTRA_YELP_RESTAURANT, mRestaurant);
            ((Activity)v.getContext()).startActivityForResult(intent, FavoritesActivity.REQUEST_RESTAURANT);
        }

        private void bindSearchRestaurant(Restaurant restaurant) {
            int starImage;

            mRestaurant = restaurant;

            mName.setText(mRestaurant.getName());
            if (mRestaurant.getStreetAddress() != null) {
                mStreetAddress.setText(mRestaurant.getStreetAddress());
                mCityStateZip.setText(mRestaurant.getCityStateZip());
            } else {
                mStreetAddress.setText(R.string.no_address);
            }
            mRating.setText(mRestaurant.getRating());
            switch((String)mRating.getText()) {
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

            mStar.setImageResource(starImage);
        }
    }
}
