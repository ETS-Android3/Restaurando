package com.kevinlamcs.android.restaurando.ui.model;


import java.util.List;

/**
 * Class to retrieve Yelps' response through Gson.
 */
public class Yelp {
    public int total;
    public List<Businesses> businesses;

    public class Businesses {
        public String rating;
        public String mobile_url;
        public String name;
        public String review_count;
        public List<List<String>> categories;
        public String display_phone;
        public String image_url;
        public String id;
        public Location location;

        public class Location {
            public List<String> address;
            public String city;
            public String postal_code;
            public String state_code;
        }
    }
}
