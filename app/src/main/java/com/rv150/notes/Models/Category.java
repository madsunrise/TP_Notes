package com.rv150.notes.Models;

/**
 * Created by Rudnev on 17.11.2016.
 */

public class Category {
    private long id;
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
