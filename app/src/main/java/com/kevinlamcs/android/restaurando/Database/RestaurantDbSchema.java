package com.kevinlamcs.android.restaurando.Database;

import java.util.List;

/**
 * Created by kevin-lam on 1/22/16.
 */
public class RestaurantDbSchema {
    public static final class RestaurantTable {
        public static final String TABLE_NAME = "Restaurants";

        public static final class Cols {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String RATING = "rating";
            public static final String CATEGORY = "category";
            public static final String CATEGORY_ID = "category_id";
            public static final String THOUGHTS = "thought_list";
        }
    }
}
