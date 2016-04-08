package com.kevinlamcs.android.restaurando.database;

/**
 * Database scheme class to list all the values in the database.
 */
public class RestaurantDbSchema {

    public static final class RestaurantTable {

        public static final String TABLE_NAME = "Restaurants";

        public static final class Cols {
            public static final String LIST_NAME = "list_name";
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String RATING = "rating";
            public static final String CATEGORY = "category";
            public static final String CATEGORY_ID = "category_id";
            public static final String THOUGHTS = "thought_list";
        }
    }
}
