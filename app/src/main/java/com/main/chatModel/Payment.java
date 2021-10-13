package com.main.chatModel;

public class Payment {

    public Integer amount;
    public String referenceID;
    public String paymentLink;
    public String paymentLinkID;

    public Payment() {}

    public Payment(Integer amount, String referenceID, String paymentLink, String paymentLinkID) {
        this.amount = amount;
        this.referenceID = referenceID;
        this.paymentLink = paymentLink;
        this.paymentLinkID = paymentLinkID;
    }
}
