package com.main.chatModel;

public class Order {

    public boolean orderConfirmed ;
    public User sellerUser , buyerUser;
    public Service service;
    public String orderID;
    public Payment payment;

    public Order() {}

    public Order(boolean orderConfirmed, User sellerUser, User buyerUser, Service service , Payment payment) {
        this.orderConfirmed = orderConfirmed;
        this.sellerUser = sellerUser;
        this.buyerUser = buyerUser;
        this.service = service;
        this.orderID = "";
        this.payment = payment;
    }
}
