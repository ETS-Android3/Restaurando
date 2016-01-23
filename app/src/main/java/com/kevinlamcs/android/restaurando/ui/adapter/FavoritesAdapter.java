package com.kevinlamcs.android.restaurando.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.List;

import static com.kevinlamcs.android.restaurando.utils.TextStyleUtils.setFavoritesListTextStyle;

/**
 * Created by kevin-lam on 1/13/16.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder> {

    private final Context mContext;
    private List<Restaurant> mRestaurantList;

    public FavoritesAdapter(Context context, List<Restaurant> restaurantList) {
        mContext = context;
        mRestaurantList = restaurantList;
    }

    @Override
    public FavoritesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_favorites, parent, false);
        return new FavoritesHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoritesHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.bindRestaurant(restaurant);
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }


    class FavoritesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Restaurant mRestaurant;
        private TextView mRestaurantName;
        private TextView mCategoryName;
        private TextView mRating;
        private ImageView mCategoryImage;

        public FavoritesHolder(View itemView) {
            super(itemView);

            mRestaurantName = (TextView) itemView.findViewById(R.id.list_item_favorites_name);
            mCategoryName = (TextView) itemView.findViewById(R.id
                    .list_item_favorites_category_name);
            mRating = (TextView) itemView.findViewById(R.id.list_item_favorites_rating);
            mCategoryImage = (ImageView) itemView.findViewById(R.id
                    .list_item_favorites_category_image);
        }

        @Override
        public void onClick(View v) {

        }

        public void bindRestaurant(Restaurant restaurant) {
            mRestaurant = restaurant;

            mRestaurantName.setText(mRestaurant.getName());
            setFavoritesListTextStyle(mContext, mRestaurantName, "font/Roboto-Regular.ttf");

            mCategoryName.setText("Mexican");
            setFavoritesListTextStyle(mContext, mCategoryName, "font/Roboto-Regular.ttf");

            mRating.setText("9.1");
            setFavoritesListTextStyle(mContext, mCategoryName, "font/Roboto-Regular.ttf");

            TextDrawable.IBuilder textBuilder = TextDrawable.builder().round();
            TextDrawable textDrawable = textBuilder.build("M", mContext.getResources().getColor
                    (R.color.colorMexican));

            mCategoryImage.setImageDrawable(textDrawable);
        }
    }
}