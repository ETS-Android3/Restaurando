package com.kevinlamcs.android.restaurando.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kevinlamcs.android.restaurando.Database.RestaurantDbSchema.RestaurantTable;

/**
 * Created by kevin-lam on 1/22/16.
 */
public class RestaurantBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "restaurantBase.db";
    private static final int DATABASE_VERSION = 1;

    public RestaurantBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + RestaurantTable.TABLE_NAME + "(" + " _id integer primary key " +
                "autoincrement, " + RestaurantTable.Cols.ID + ", " + RestaurantTable.Cols.NAME +
                ", " + RestaurantTable.Cols.RATING + ", " + RestaurantTable.Cols.CATEGORY + ", "
                + RestaurantTable.Cols.CATEGORY_ID + ", " + RestaurantTable.Cols.THOUGHTS + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
