package com.rv150.notes.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class Category implements Parcelable {
    private long mID;
    private String mName;

    public Category(String name) {
        this.mName = name;
    }

    public Category(String name, long id) {
        this.mName = name;
        this.mID = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeLong(mID);
    }

    public static final Parcelable.Creator<Category> CREATOR
            = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private Category(Parcel in) {
        mName = in.readString();
        mID = in.readLong();
    }

    @Override
    public int hashCode() {
        return Long.valueOf(mID).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Category)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        return this.mID == ((Category) other).mID;
    }



    public void setId(long id) {
        this.mID = id;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public long getId() {
        return mID;
    }

    public String getName() {
        return mName;
    }
}
