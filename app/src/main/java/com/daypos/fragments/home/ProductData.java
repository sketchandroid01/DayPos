package com.daypos.fragments.home;

import java.io.Serializable;

public class ProductData implements Serializable {

    private String id;
    private String name;
    private String image;
    private String sku;
    private String price;
    private String taxes;
    private String bar_code;
    private String item_color;
    private String item_shape;
    private String is_attribute;
    private String category_id;
    private String sold_option;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTaxes() {
        return taxes;
    }

    public ProductData setTaxes(String taxes) {
        this.taxes = taxes;
        return this;
    }

    public String getBar_code() {
        return bar_code;
    }

    public ProductData setBar_code(String bar_code) {
        this.bar_code = bar_code;
        return this;
    }

    public String getItem_color() {
        return item_color;
    }

    public ProductData setItem_color(String item_color) {
        this.item_color = item_color;
        return this;
    }

    public String getItem_shape() {
        return item_shape;
    }

    public ProductData setItem_shape(String item_shape) {
        this.item_shape = item_shape;
        return this;
    }

    public String getIs_attribute() {
        return is_attribute;
    }

    public ProductData setIs_attribute(String is_attribute) {
        this.is_attribute = is_attribute;
        return this;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSold_option() {
        return sold_option;
    }

    public void setSold_option(String sold_option) {
        this.sold_option = sold_option;
    }
}


/*
  {
          "id": "14",
          "user_id": "1",
          "name": "Test",
          "category_id": "35",
          "availability_status": "0",
          "sold_option": "1",
          "price": "100.00",
          "cost": "0.00",
          "sku": "10001",
          "bar_code": "",
          "item_image": "",
          "modifiers": "",
          "taxes": "3",
          "item_color": "rgb(233, 30, 99)",
          "item_shape": "square",
          "is_composite": "0",
          "is_track": "0",
          "store_availability": "1",
          "is_attribute": "1"
          },*/
