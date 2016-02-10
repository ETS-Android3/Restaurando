package com.kevinlamcs.android.restaurando.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import static com.kevinlamcs.android.restaurando.Database.RestaurantDbSchema.*;

/**
 * Created by kevin-lam on 1/23/16.
 */
public class RestaurantListing {
    public static final String FILTER_TYPE_CATEGORY = "category";
    public static final String FILTER_TYPE_SELECTED = "selected";

    private static RestaurantListing sRestaurantLisiting;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public RestaurantListing(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new RestaurantBaseHelper(mContext).getWritableDatabase();
    }

    public static RestaurantListing get(Context context) {
        if (sRestaurantLisiting == null) {
            sRestaurantLisiting = new RestaurantListing(context);
        }
        return sRestaurantLisiting;
    }

    public void addRestaurant(Restaurant restaurant) {
        ContentValues values = getContentValues(restaurant);
        mDatabase.insert(RestaurantTable.TABLE_NAME, null, values);
    }

    public void removeRestaurant(Restaurant restaurant) {
        String name = restaurant.getName();
        mDatabase.delete(RestaurantTable.TABLE_NAME, RestaurantTable.Cols.NAME + " = ?",
                new String[]{name});
    }

    public List<Restaurant> getRestaurantList() {
        List<Restaurant> restaurantList = new ArrayList<>();
        RestaurantCursorWrapper cursor = (RestaurantCursorWrapper) queryRestaurants(null, null);
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

    public List<Restaurant> getRestaurantList(List<String> queryList, String filterType) {
        StringBuffer stringBuffer = new StringBuffer();
        if (filterType.equals(FILTER_TYPE_CATEGORY)) {
            stringBuffer = setupWhereClause(RestaurantTable.Cols.CATEGORY, queryList, stringBuffer);
        } else if (filterType.equals(FILTER_TYPE_SELECTED)) {
            stringBuffer = setupWhereClause(RestaurantTable.Cols.ID, queryList, stringBuffer);
        }

        String[] whereArgs = new String[queryList.size()];
        RestaurantCursorWrapper cursor = (RestaurantCursorWrapper) queryRestaurants(stringBuffer
                .toString(), queryList.toArray(whereArgs));
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

    private StringBuffer setupWhereClause(String columnValue, List<String> values, StringBuffer buffer) {
        buffer.append(columnValue + " IN(");
        for (int index=0; index < values.size(); index++) {
            if (index + 1 >= values.size()) {
                buffer.append("?)");
                break;
            }
            buffer.append("?,");
        }
        return buffer;
    }

    private static ContentValues getContentValues(Restaurant restaurant) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RestaurantTable.Cols.NAME, restaurant.getName());
        contentValues.put(RestaurantTable.Cols.ID, restaurant.getId());
        contentValues.put(RestaurantTable.Cols.RATING, restaurant.getRating());
        contentValues.put(RestaurantTable.Cols.CATEGORY, restaurant.getCategory());
        contentValues.put(RestaurantTable.Cols.CATEGORY_ID, restaurant.getCategoryId());
        contentValues.put(RestaurantTable.Cols.THOUGHTS, restaurant.getThoughtList());
        return contentValues;
    }

    private CursorWrapper queryRestaurants(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(RestaurantTable.TABLE_NAME, null, whereClause, whereArgs,
                null, null, RestaurantTable.Cols.CATEGORY_ID + " ASC, " + RestaurantTable.Cols
                        .NAME + " ASC");
        return new RestaurantCursorWrapper(cursor);
    }
}
