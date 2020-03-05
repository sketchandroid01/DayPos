package com.daypos.fragments.billhistory;

public class ChildItem extends ListItem {

    private OrderDetails orderDetails;

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public ChildItem setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
        return this;
    }

    @Override
    public int getType() {
        return TYPE_CHILDS;
    }

}