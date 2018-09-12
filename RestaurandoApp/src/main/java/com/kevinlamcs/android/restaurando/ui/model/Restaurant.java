package com.kevinlamcs.android.restaurando.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents a favorited restaurant.
 */
public class Restaurant implements Parcelable {
    private String name;
    private String id;
    private String displayPhone;
    private String streetAddress;
    private String cityStateZip;
    private String rating;
    private String reviewCount;
    private float distance;
    private String url;
    private String imageUrl;
    private String category;
    private String categoryId;
    private String thoughtList;

    public Restaurant() {
    }

    private Restaurant(Parcel source) {
        String[] stringMembers = new String[12];

        source.readStringArray(stringMembers);
        name = stringMembers[0];
        id = stringMembers[1];
        displayPhone = stringMembers[2];
        streetAddress = stringMembers[3];
        cityStateZip = stringMembers[4];
        rating = stringMembers[5];
        category = stringMembers[6];
        categoryId = stringMembers[7];
        reviewCount = stringMembers[8];
        url = stringMembers[9];
        imageUrl = stringMembers[10];
        thoughtList = stringMembers[11];
        distance = source.readFloat();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!Restaurant.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        Restaurant restaurant = (Restaurant) object;

        return this.id.equals(restaurant.getId());
    }

    @Override
    public int hashCode() {
        return 23 + (this.id != null ? this.id.hashCode() : 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayPhone() {
        return displayPhone;
    }

    public void setDisplayPhone(String displayPhone) {
        this.displayPhone = displayPhone;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCityStateZip() {
        return cityStateZip;
    }

    public void setCityStateZip(String cityStateZip) {
        this.cityStateZip = cityStateZip;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThoughtList() {
        return thoughtList;
    }

    public void setThoughtList(String thoughtList) {
        this.thoughtList = thoughtList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.name, this.id, this.displayPhone, this.streetAddress, this.cityStateZip,
                this.rating, this.category, this.categoryId, this.reviewCount, this.url,
                this.imageUrl, this.thoughtList});
        dest.writeFloat(distance);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
