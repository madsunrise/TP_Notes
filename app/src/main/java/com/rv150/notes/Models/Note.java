package com.rv150.notes.Models;

import com.rv150.notes.Database.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class Note {
    private String name;
    private String content;
    private Calendar createdAt;
    private long id;
    private List<Category> categories = new ArrayList<>();

    public Note (String name, String content) {
        this(name, content, Calendar.getInstance());
    }

    public Note (String name, String content, Calendar createdAt) {
        this.name = name;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Note(String name, String content, long createdAt) {
        this.name = name;
        this.content = content;
        this.createdAt = new GregorianCalendar();
        this.createdAt.setTimeInMillis(createdAt);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public long getCreatedAtInMillis() {
        return createdAt.getTimeInMillis();
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }
}
