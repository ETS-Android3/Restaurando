package com.kevinlamcs.android.restaurando.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import static com.kevinlamcs.android.restaurando.database.RestaurantDbSchema.*;

/**
 * Cursor wrapper class used to retrieve values from the database.
 */
public class RestaurantCursorWrapper extends CursorWrapper {

    public RestaurantCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Retrieves values from the database and stores it in a Restaurant object.
     * @return Restaurant
     */
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
