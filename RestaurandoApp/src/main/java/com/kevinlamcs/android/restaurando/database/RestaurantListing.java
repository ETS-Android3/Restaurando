package com.kevinlamcs.android.restaurando.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;

import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import static com.kevinlamcs.android.restaurando.database.RestaurantDbSchema.*;

/**
 * Class which handles querying and insertion into the Restaurant database.
 */
public class RestaurantListing {

    private static final String CATEGORY = "Category";
    private static final String RATING = "Rating";
    private static final String NAME = "Name";

    private static RestaurantListing restaurantListing;
    private final SQLiteDatabase database;

    public RestaurantListing(Context context) {
        this.database = new RestaurantBaseHelper(context).getWritableDatabase();
    }

    /**
     * Retrieves the RestaurantListing to access the database.
     * @param context - database context
     * @return RestaurantListing
     */
    public static RestaurantListing get(Context context) {
        if (restaurantListing == null) {
            restaurantListing = new RestaurantListing(context);
        }
        return restaurantListing;
    }

    /**
     * Adds favorited restaurant into the database.
     * @param listName - name of the current active list
     * @param restaurant - restaurant to be inserted
     */
    public void addRestaurant(String listName, Restaurant restaurant) {

        // Try to retrieve this restaurant and done if it exists in the database
        List<Restaurant> listRestaurants = getRestaurantById(listName, restaurant.getId());
        if (listRestaurants.isEmpty()) {
            ContentValues values = getContentValues(listName, restaurant);
            database.insert(RestaurantTable.TABLE_NAME, null, values);
        } else {
            // If the restaurant exists already, just update the thought list
            updateThoughts(listName, restaurant, restaurant.getThoughtList());
        }
    }

    /**
     * Removes favorited restaurant from the database.
     * @param listName - name of the current active list
     * @param restaurant - restaurant to be removed
     */
    public void removeRestaurant(String listName, Restaurant restaurant) {
        String name = restaurant.getName();
        database.delete(RestaurantTable.TABLE_NAME, RestaurantTable.Cols.LIST_NAME + " = ? AND " +
                RestaurantTable.Cols.NAME + " = ?", new String[]{listName, name});
    }

    /**
     * Renames the current restaurant list.
     * @param oldListName - old restaurant list name
     * @param newListName - new restaurant list name
     */
    public void renameRestaurantList(String oldListName, String newListName) {
        String where = "UPDATE " + RestaurantTable.TABLE_NAME + " SET " + RestaurantTable.Cols
                .LIST_NAME + " = '" + newListName + "' WHERE " + RestaurantTable.Cols.LIST_NAME
                + " = '" + oldListName + "'";
        database.execSQL(where);
    }

    /**
     * Update a restaurant's thought list.
     * @param listName - name of the current active list
     * @param restaurant - restaurant to update thought list
     * @param thoughtList - thought list to be updated
     */
    private void updateThoughts(String listName, Restaurant restaurant, String thoughtList) {
        String where = "UPDATE " + RestaurantTable.TABLE_NAME + " SET " + RestaurantTable.Cols
                .THOUGHTS + " = '" + thoughtList + "' WHERE " + RestaurantTable.Cols.LIST_NAME + " = '"
                + listName + "' AND " + RestaurantTable.Cols.ID + " = '" + restaurant.getId() + "'";
        database.execSQL(where);
    }

    /**
     * Remove the current favorited restaurant list
     * @param listName - favorited restaurant list to be removed
     */
    public void removeRestaurantList(String listName) {
        database.delete(RestaurantTable.TABLE_NAME, RestaurantTable.Cols.LIST_NAME + " = ?",
                new String[]{listName});
    }

    /**
     * Retrieves list of restaurant suggestions when doing single restaurant search.
     * @param listName - name of current favorited restaurant list
     * @param sortOrder - sorting order for the restaurants
     * @param matrixCursor - cursor to store the restaurants
     * @param query - current user input
     */
    public void getRestaurantSuggestions(String listName, String sortOrder, MatrixCursor matrixCursor,
                                         String query) {

        // Retrieve all the restaurants in the current favorited restaurant list
        RestaurantCursorWrapper cursor = (RestaurantCursorWrapper) queryRestaurants(
                RestaurantTable.Cols.LIST_NAME + " = ?", new String[]{listName}, setUpSortOrderClause(sortOrder));

        // Go through the restaurants. Check if the user's input matches the restaurant names. If
        // it does, store it in the matrix cursor
        try {
            int index = 0;
            int lastCharacter = query.length();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Restaurant restaurant = cursor.getRestaurant();
                if (restaurant.getName().length() >= query.length()) {
                    String substringRestaurant = restaurant.getName().substring(0,
                            lastCharacter).toLowerCase();
                    String substringQuery = query.substring(0, lastCharacter).toLowerCase();
                    if (substringRestaurant.equals(substringQuery)) {
                        matrixCursor.addRow(new Object[]{index, restaurant.getName(), restaurant.getId()});
                        index++;
                    }
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Retrieves either all restaurants in a favorited restaurant list or restaurants with the
     * specified categories.
     * @param listName - name of current favorited restaurant list
     * @param categories - categories for filtering
     * @param sortOrder - order to sort the restaurants
     * @return List of restaurants queried
     */
    public List<Restaurant> getRestaurantList(String listName, List<String> categories,
                                              String sortOrder) {

        RestaurantCursorWrapper cursor;

        if (categories.isEmpty()) {
            cursor = (RestaurantCursorWrapper) queryRestaurants(RestaurantTable.Cols.LIST_NAME
                            + " = ?", new String[]{listName}, setUpSortOrderClause(sortOrder));
        } else {
            List<String> listCategories = new ArrayList<>(categories);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer = setUpWhereClause(listCategories, stringBuffer);
            listCategories.add(0, listName);
            String[] whereArgs = new String[listCategories.size()];
            cursor = (RestaurantCursorWrapper) queryRestaurants(stringBuffer.toString(),
                    listCategories.toArray(whereArgs), setUpSortOrderClause(sortOrder));
        }
        return navigateCursorForRestaurants(cursor);
    }

    /**
     * Retrieve a single restaurant by id.
     * @param listName - name of current favorited restaurant list
     * @param restaurantId- id of the restaurant
     * @return the single restaurant
     */
    public List<Restaurant> getRestaurantById(String listName, String restaurantId) {

        // Return no restaurant if id is null
        if (restaurantId.equals("")) {
            return new ArrayList<>();
        }

        String whereClause = RestaurantTable.Cols.LIST_NAME + " = ? AND "
                + RestaurantTable.Cols.ID + " = ?";
        String[] whereArgs = new String[]{listName, restaurantId};
        RestaurantCursorWrapper cursor = (RestaurantCursorWrapper) queryRestaurants(whereClause,
                whereArgs, null);
        return navigateCursorForRestaurants(cursor);
    }

    /**
     * Sets up the where clause (database values) which is used to search through the database.
     * @param values - value of the column name
     * @param buffer - buffer to place the where clause
     * @return Buffer
     */
    private StringBuffer setUpWhereClause(List<String> values, StringBuffer buffer) {
        buffer.append(RestaurantTable.Cols.LIST_NAME + " = ? AND " + RestaurantTable.Cols.CATEGORY
                + " IN(");
        for (int index=0; index < values.size(); index++) {
            if (index + 1 >= values.size()) {
                buffer.append("?)");
                break;
            }
            buffer.append("?,");
        }
        return buffer;
    }

    /**
     * Goes through all the retrieved restaurants and puts them into a list.
     * @param cursor - cursor returned by the query
     * @return List of restaurants
     */
    private List<Restaurant> navigateCursorForRestaurants(RestaurantCursorWrapper cursor) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                restaurantList.add(cursor.getRestaurant());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return restaurantList;
    }

    /**
     * Sets up the clause used for sorting the queried restaurants.
     * @param sortOrder - sorting order keyword
     * @return String clause for sort order
     */
    private String setUpSortOrderClause(String sortOrder) {
        String sortOrderClause = "";
        switch (sortOrder) {
            case CATEGORY:
                sortOrderClause = sortOrderClause + RestaurantTable.Cols.CATEGORY_ID + " ASC, " + RestaurantTable.Cols
                        .NAME + " ASC";
                break;
            case NAME:
                sortOrderClause = sortOrderClause + RestaurantTable.Cols.NAME + " ASC";
                break;
            case RATING:
                sortOrderClause = sortOrderClause + RestaurantTable.Cols.RATING + " ASC," + RestaurantTable.Cols
                        .NAME + " ASC";
                break;
        }
        return sortOrderClause;
    }

    /**
     * Takes the values from the restaurant and inserts it into a ContentValue which can be inserted
     * into the database.
     * @param listName - name of current favorited restaurant list
     * @param restaurant - restaurant to retrieve values
     * @return ContentValues
     */
    private static ContentValues getContentValues(String listName, Restaurant restaurant) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RestaurantTable.Cols.LIST_NAME, listName);
        contentValues.put(RestaurantTable.Cols.NAME, restaurant.getName());
        contentValues.put(RestaurantTable.Cols.ID, restaurant.getId());
        contentValues.put(RestaurantTable.Cols.RATING, restaurant.getRating());
        contentValues.put(RestaurantTable.Cols.CATEGORY, restaurant.getCategory());
        contentValues.put(RestaurantTable.Cols.CATEGORY_ID, restaurant.getCategoryId());
        contentValues.put(RestaurantTable.Cols.THOUGHTS, restaurant.getThoughtList());
        return contentValues;
    }

    /**
     * Queries the database.
     * @param whereClause - Name of columns to query the database
     * @param whereArgs - Values within column
     * @param sortOrderClause - Phrase used to sort the results
     * @return Cursor to iterate through results
     */
    private CursorWrapper queryRestaurants(String whereClause, String[] whereArgs, String
            sortOrderClause) {
        Cursor cursor = database.query(RestaurantTable.TABLE_NAME, null, whereClause, whereArgs,
                null, null, sortOrderClause);
        return new RestaurantCursorWrapper(cursor);
    }
}
