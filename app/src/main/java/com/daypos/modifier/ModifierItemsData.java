package com.daypos.modifier;

import java.io.Serializable;

public class ModifierItemsData implements Serializable {

    private String id;
    private String name;
    private String price;


    public String getId() {
        return id;
    }

    public ModifierItemsData setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ModifierItemsData setName(String name) {
        this.name = name;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public ModifierItemsData setPrice(String price) {
        this.price = price;
        return this;
    }
}
