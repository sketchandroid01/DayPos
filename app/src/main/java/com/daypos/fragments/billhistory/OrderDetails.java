package com.daypos.fragments.billhistory;

import java.io.Serializable;

public class OrderDetails implements Serializable {

    private String id;
    private String bill_no;
    private String date;
    private String time;
    private String is_returned;
    private String payment_type;
    private String total_amount;
    private String total_item;
    private String return_no;
    private String cashier;

    public String getId() {
        return id;
    }

    public OrderDetails setId(String id) {
        this.id = id;
        return this;
    }

    public String getBill_no() {
        return bill_no;
    }

    public OrderDetails setBill_no(String bill_no) {
        this.bill_no = bill_no;
        return this;
    }

    public String getDate() {
        return date;
    }

    public OrderDetails setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTime() {
        return time;
    }

    public OrderDetails setTime(String time) {
        this.time = time;
        return this;
    }

    public String getIs_returned() {
        return is_returned;
    }

    public OrderDetails setIs_returned(String is_returned) {
        this.is_returned = is_returned;
        return this;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public OrderDetails setPayment_type(String payment_type) {
        this.payment_type = payment_type;
        return this;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public OrderDetails setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
        return this;
    }

    public String getTotal_item() {
        return total_item;
    }

    public OrderDetails setTotal_item(String total_item) {
        this.total_item = total_item;
        return this;
    }

    public String getReturn_no() {
        return return_no;
    }

    public OrderDetails setReturn_no(String return_no) {
        this.return_no = return_no;
        return this;
    }

    public String getCashier() {
        return cashier;
    }

    public OrderDetails setCashier(String cashier) {
        this.cashier = cashier;
        return this;
    }
}
