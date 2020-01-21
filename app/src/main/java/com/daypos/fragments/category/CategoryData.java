package com.daypos.fragments.category;

public class CategoryData {

    private String id;
    private String name;
    private String color;
    private String item_no;

    public String getId() {
        return id;
    }

    public CategoryData setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CategoryData setName(String name) {
        this.name = name;
        return this;
    }

    public String getColor() {
        return color;
    }

    public CategoryData setColor(String color) {
        this.color = color;
        return this;
    }

    public String getItem_no() {
        return item_no;
    }

    public CategoryData setItem_no(String item_no) {
        this.item_no = item_no;
        return this;
    }
}
