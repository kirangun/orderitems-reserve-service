package com.azprc.itemreserver.pojo;

import java.util.List;

public class OrderDetails {
    private String customerId;
    private List<Item> items;
    private double totalAmount;

    public String getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
