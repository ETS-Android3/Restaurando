package com.kevinlamcs.android.restaurando;

/**
 * Created by kevin-lam on 1/4/16.
 */
public class Restaurant {
    //private List<ArrayList<Category>> mCategories;
    private String mName;
    private String mDisplayPhone;
    //private AddressLocation mLocation;
    private float mRating;
    private int mReviewCount;
    private float mDistance;
    private String mUrl;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDisplayPhone() {
        return mDisplayPhone;
    }

    public void setDisplayPhone(String displayPhone) {
        mDisplayPhone = displayPhone;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public int getReviewCount() {
        return mReviewCount;
    }

    public void setReviewCount(int reviewCount) {
        mReviewCount = reviewCount;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        mDistance = distance;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
