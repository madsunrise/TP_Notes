package com.rv150.notes.Models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class Note {
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
        this.mCreatedAt = new GregorianCalendar();
        this.mCreatedAt.setTimeInMillis(createdAt);
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
