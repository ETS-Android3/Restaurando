package com.kevinlamcs.android.restaurando.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.kevinlamcs.android.restaurando.Database.RestaurantDbSchema.RestaurantTable;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

/**
 * Created by kevin-lam on 1/22/16.
 */
public class RestaurantCursorWrapper extends CursorWrapper {

    public RestaurantCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Restaurant getRestaurant() {
        String id = getString(getColumnIndex(RestaurantTable.Cols.ID));
        String name = getString(getColumnIndex(RestaurantTable.Cols.NAME));
        String rating = getString(getColumnIndex(RestaurantTable.Cols.RATING));
        String category = getString(getColumnIndex(RestaurantTable.Cols.CATEGORY));
        String categoryId = getString(getColumnIndex(RestaurantTable.Cols.CATEGORY_ID));
        String thoughtList = getString(getColumnIndex(RestaurantTable.Cols.THOUGHTS));

        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setRating(rating);
        restaurant.setCategory(category);
        restaurant.setCategoryId(categoryId);
        restaurant.setThoughtList(thoughtList);

        return restaurant;
    }
}
