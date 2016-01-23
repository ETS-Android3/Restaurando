package com.kevinlamcs.android.restaurando.ui.model;


import java.util.List;

/**
 * Created by kevin-lam on 1/10/16.
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
            public List<String> display_address;
            public String city;
            public String postal_code;
            public String state_code;
        }
    }
}
