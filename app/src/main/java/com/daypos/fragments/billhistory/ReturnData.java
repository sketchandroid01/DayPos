package com.daypos.fragments.billhistory;

public class ReturnData {

    private String item_id;
    private String item_qty;
    private String item_price;
    private String refund_price;

    public String getItem_id() {
        return item_id;
    }

    public ReturnData setItem_id(String item_id) {
        this.item_id = item_id;
        return this;
    }

    public String getItem_qty() {
        return item_qty;
    }

    public ReturnData setItem_qty(String item_qty) {
        this.item_qty = item_qty;
        return this;
    }

    public String getRefund_price() {
        return refund_price;
    }

    public ReturnData setRefund_price(String refund_price) {
        this.refund_price = refund_price;
        return this;
    }

    public String getItem_price() {
        return item_price;
    }

    public ReturnData setItem_price(String item_price) {
        this.item_price = item_price;
        return this;
    }
}
