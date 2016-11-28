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
    private int color;

    public Category(String name) {
        this(name, -1, -1);
    }

    public Category(String name, long id) {
        this(name, id, -1);
    }

    public Category(String name, long id, int color) {
        this.mName = name;
        this.mID = id;
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeLong(mID);
        parcel.writeInt(color);
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
        color = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return mID == category.mID;

    }

    @Override
    public int hashCode() {
        return (int) (mID ^ (mID >>> 32));
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
