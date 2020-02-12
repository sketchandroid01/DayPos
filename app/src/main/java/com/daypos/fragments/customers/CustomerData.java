package com.daypos.fragments.customers;

public class CustomerData {

    private String id;
    private String name;
    private String total_order;
    private String total_order_amt;
    private String balance;
    private String email;
    private String phone;
    private String customer_id;

    public String getId() {
        return id;
    }

    public CustomerData setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomerData setName(String name) {
        this.name = name;
        return this;
    }

    public String getTotal_order() {
        return total_order;
    }

    public CustomerData setTotal_order(String total_order) {
        this.total_order = total_order;
        return this;
    }

    public String getTotal_order_amt() {
        return total_order_amt;
    }

    public CustomerData setTotal_order_amt(String total_order_amt) {
        this.total_order_amt = total_order_amt;
        return this;
    }

    public String getBalance() {
        return balance;
    }

    public CustomerData setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
