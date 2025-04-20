package com.azprc.itemreserver.pojo;

public class Order {
    private String sessionId;
    private OrderDetails orderDetails;

    public String getSessionId() {
        return sessionId;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    @Override
    public String toString() {
        return "Order{" +
                "sessionId='" + sessionId + '\'' +
                ", orderDetails=" + orderDetails +
                '}';
    }
}
