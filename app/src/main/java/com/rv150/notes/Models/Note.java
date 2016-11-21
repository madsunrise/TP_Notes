package com.rv150.notes.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class Note implements Parcelable {
    private String mName;
    private String mContent;
    private Calendar mCreatedAt;
    private long mID;
    private List<Category> mCategories = new ArrayList<>();

    public Note (String name, String content) {
        this(name, content, Calendar.getInstance());
    }

    public Note (String name, String content, Calendar createdAt) {
        this.mName = name;
        this.mContent = content;
        this.mCreatedAt = createdAt;
    }

    public Note(String name, String content, long createdAt) {
        this.mName = name;
        this.mContent = content;
        this.mCreatedAt = Calendar.getInstance();
        this.mCreatedAt.setTimeInMillis(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mContent);
        parcel.writeLong(mCreatedAt.getTimeInMillis());
        parcel.writeLong(mID);
        parcel.writeTypedList(mCategories);
    }

    public static final Parcelable.Creator<Note> CREATOR
            = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private Note(Parcel in) {
        mName = in.readString();
        mContent = in.readString();
        long createdAt = in.readLong();
        mCreatedAt = Calendar.getInstance();
        mCreatedAt.setTimeInMillis(createdAt);
        mID = in.readLong();
        mCategories = new ArrayList<>();
        in.readTypedList(mCategories, Category.CREATOR);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public Calendar getCreatedAt() {
        return mCreatedAt;
    }

    public long getCreatedAtInMillis() {
        return mCreatedAt.getTimeInMillis();
    }

    public void setCreatedAt(Calendar createdAt) {
        this.mCreatedAt = createdAt;
    }

    public long getId() {
        return mID;
    }

    public void setId(long id) {
        this.mID = id;
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(List<Category> categories) {
        this.mCategories = categories;
    }

    public void addCategory(Category category) {
        this.mCategories.add(category);
    }
}
