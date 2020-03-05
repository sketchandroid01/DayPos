package com.daypos.fragments.billhistory;

import java.util.ArrayList;

public class Orders {

    private String date;
    private ArrayList<OrderDetails> orderDetailsArrayList;

    public String getDate() {
        return date;
    }

    public Orders setDate(String date) {
        this.date = date;
        return this;
    }

    public ArrayList<OrderDetails> getOrderDetailsArrayList() {
        return orderDetailsArrayList;
    }

    public Orders setOrderDetailsArrayList(ArrayList<OrderDetails> orderDetailsArrayList) {
        this.orderDetailsArrayList = orderDetailsArrayList;
        return this;
    }
}
