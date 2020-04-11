package com.daypos.cart;

import com.daypos.modifier.ModifierItemsData;

import java.io.Serializable;
import java.util.ArrayList;

public class CartData implements Serializable {

    private String id;
    private String product_id;
    private String attr_id;
    private String product_name;
    private String attr_name;
    private String color_code;
    private String qty;
    private String mrp;
    private String price;
    private String special_price;
    private String modifiers;
    private String sold_option;
    private ArrayList<ModifierItemsData> modifierItemsList;


    public String getId() {
        return id;
    }

    public CartData setId(String id) {
        this.id = id;
        return this;
    }

    public String getProduct_id() {
        return product_id;
    }

    public CartData setProduct_id(String product_id) {
        this.product_id = product_id;
        return this;
    }

    public String getAttr_id() {
        return attr_id;
    }

    public CartData setAttr_id(String attr_id) {
        this.attr_id = attr_id;
        return this;
    }

    public String getProduct_name() {
        return product_name;
    }

    public CartData setProduct_name(String product_name) {
        this.product_name = product_name;
        return this;
    }

    public String getAttr_name() {
        return attr_name;
    }

    public CartData setAttr_name(String attr_name) {
        this.attr_name = attr_name;
        return this;
    }

    public String getColor_code() {
        return color_code;
    }

    public CartData setColor_code(String color_code) {
        this.color_code = color_code;
        return this;
    }

    public String getQty() {
        return qty;
    }

    public CartData setQty(String qty) {
        this.qty = qty;
        return this;
    }

    public String getMrp() {
        return mrp;
    }

    public CartData setMrp(String mrp) {
        this.mrp = mrp;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public CartData setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getSpecial_price() {
        return special_price;
    }

    public CartData setSpecial_price(String special_price) {
        this.special_price = special_price;
        return this;
    }

    public String getModifiers() {
        return modifiers;
    }

    public CartData setModifiers(String modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public ArrayList<ModifierItemsData> getModifierItemsList() {
        return modifierItemsList;
    }

    public CartData setModifierItemsList(ArrayList<ModifierItemsData> modifierItemsList) {
        this.modifierItemsList = modifierItemsList;
        return this;
    }

    public String getSold_option() {
        return sold_option;
    }

    public CartData setSold_option(String sold_option) {
        this.sold_option = sold_option;
        return this;
    }
}
