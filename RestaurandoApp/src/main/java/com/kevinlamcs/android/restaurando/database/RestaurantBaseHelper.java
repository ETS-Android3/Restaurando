package com.kevinlamcs.android.restaurando.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kevinlamcs.android.restaurando.database.RestaurantDbSchema.RestaurantTable;

/**
 * Database helper class which aids in the creation of the favorited restaurant database
 */
public class RestaurantBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "restaurantBase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE = "CREATE TABLE " + RestaurantTable.TABLE_NAME + "("
            + " _id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RestaurantTable.Cols.LIST_NAME + ", "
            + RestaurantTable.Cols.ID + ", "
            + RestaurantTable.Cols.NAME + ", "
            + RestaurantTable.Cols.RATING + ", "
            + RestaurantTable.Cols.CATEGORY + ", "
            + RestaurantTable.Cols.CATEGORY_ID + ", "
            + RestaurantTable.Cols.THOUGHTS + ")";

    public RestaurantBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
