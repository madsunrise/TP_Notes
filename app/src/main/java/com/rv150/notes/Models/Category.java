package com.rv150.notes.Models;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class Category {
    private long mID;
    private String mName;

    public Category(String name) {
        this.mName = name;
    }

    public Category(String name, long id) {
        this.mName = name;
        this.mID = id;
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
