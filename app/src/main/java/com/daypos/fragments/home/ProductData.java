package com.daypos.fragments.home;

public class ProductData {

    private String id;
    private String name;
    private String image;
    private String sku;
    private String price;
    private String taxes;
    private String item_color;
    private String item_shape;
    private String is_attribute;


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
