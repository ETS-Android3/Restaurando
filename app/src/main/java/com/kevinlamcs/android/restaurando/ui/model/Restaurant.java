package com.kevinlamcs.android.restaurando.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin-lam on 1/4/16.
 */
public class Restaurant implements Parcelable {
    private String mName;
    private String mId;
    private String mDisplayPhone;
    private String mStreetAddress;
    private String mCityStateZip;
    private String mRating;
    private String mReviewCount;
    private float mDistance;
    private String mUrl;
    private String mImageUrl;
    private String mCategory;
    private String mCategoryId;
    private String mThoughtList;
    private boolean mIsSelectedForRandom;

    public Restaurant() {
    }

    public Restaurant(Parcel source) {
        String[] stringMembers = new String[11];

        source.readStringArray(stringMembers);
        mName = stringMembers[0];
        mId = stringMembers[1];
        mDisplayPhone = stringMembers[2];
        mStreetAddress = stringMembers[3];
        mCityStateZip = stringMembers[4];
        mRating = stringMembers[5];
        mCategory = stringMembers[6];
        mReviewCount = stringMembers[7];
        mUrl = stringMembers[8];
        mImageUrl = stringMembers[9];
        mThoughtList = stringMembers[10];
        mDistance = source.readFloat();
        mIsSelectedForRandom = source.readByte() != 0;
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

        return this.mId.equals(restaurant.getId());
    }

    @Override
    public int hashCode() {
        return 23 + (this.mId != null ? this.mId.hashCode() : 0);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    public String getReviewCount() {
        return mReviewCount;
    }

    public void setReviewCount(String reviewCount) {
        mReviewCount = reviewCount;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(String categoryId) {
        mCategoryId = categoryId;
    }

    public String getCityStateZip() {
        return mCityStateZip;
    }

    public void setCityStateZip(String cityStateZip) {
        mCityStateZip = cityStateZip;
    }

    public String getStreetAddress() {
        return mStreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        mStreetAddress = streetAddress;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getThoughtList() {
        return mThoughtList;
    }

    public void setThoughtList(String thoughtList) {
        mThoughtList = thoughtList;
    }

    public boolean isSelectedForRandom() {
        return mIsSelectedForRandom;
    }

    public void setIsSelectedForRandom(boolean isSelectedForRandom) {
        mIsSelectedForRandom = isSelectedForRandom;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.mName, this.mId, this.mDisplayPhone, this.mStreetAddress, this.mCityStateZip,
                this.mRating, this.mCategory, this.mReviewCount, this.mUrl, this.mImageUrl,
                this.mThoughtList});
        dest.writeFloat(mDistance);
        dest.writeByte((byte)(this.mIsSelectedForRandom ? 1 : 0));
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
