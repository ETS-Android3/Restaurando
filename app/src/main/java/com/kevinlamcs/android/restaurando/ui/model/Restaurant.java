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
    private String mDisplayPhone;
    private String mStreetAddress;
    private String mCityStateZip;
    private String mRating;
    private String mReviewCount;
    private float mDistance;
    private String mUrl;
    private String mImageUrl;

    private List<String> mThoughtsList = new ArrayList<>();

    public Restaurant() {
    }

    public Restaurant(Parcel source) {
        String[] stringMembers = new String[8];

        source.readStringArray(stringMembers);
        mName = stringMembers[0];
        mDisplayPhone = stringMembers[1];
        mStreetAddress = stringMembers[2];
        mCityStateZip = stringMembers[3];
        mRating = stringMembers[4];
        mReviewCount = stringMembers[5];
        mUrl = stringMembers[6];
        mImageUrl = stringMembers[7];
        mDistance = source.readFloat();
        source.readStringList(mThoughtsList);
    }

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

    public List<String> getThoughtsList() {
        return mThoughtsList;
    }

    public void setThoughtsList(List<String> thoughtsList) {
        mThoughtsList = thoughtsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.mName, this.mDisplayPhone, this.mStreetAddress,
                this.mCityStateZip, this.mRating, this.mReviewCount, this.mUrl, this.mImageUrl});
        dest.writeFloat(mDistance);
        dest.writeStringList(mThoughtsList);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
