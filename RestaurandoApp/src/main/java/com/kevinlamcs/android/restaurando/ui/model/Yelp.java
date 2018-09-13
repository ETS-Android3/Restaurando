package com.kevinlamcs.android.restaurando.ui.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to retrieve Yelps' response through Gson.
 */
public class Yelp {
    @SerializedName("businesses")
    public ArrayList<Business> businesses;

    public class Business {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;

        @SerializedName("image_url")
        public String image_url;

        @SerializedName("url")
        public String url;

        @SerializedName("review_count")
        public String review_count;

        @SerializedName("categories")
        public ArrayList<Category> categories;

        @SerializedName("rating")
        public String rating;

        @SerializedName("location")
        public Location location;

        @SerializedName("display_phone")
        public String display_phone;

        public class Location {
            @SerializedName("address1")
            public String address1;

            @SerializedName("city")
            public String city;

            @SerializedName("zip_code")
            public String postal_code;

            @SerializedName("state")
            public String state_code;
        }
    }

    public class Category {
        @SerializedName("title")
        public String title;
    }
}
