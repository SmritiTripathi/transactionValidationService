package com.afterpay.model;

public class Transaction {

    private String creditCardHash;
    private String timeStamp;
    private String price;

    public Transaction(String creditCardHash, String timeStamp, String price) {
        this.creditCardHash = creditCardHash;
        this.timeStamp = timeStamp;
        this.price = price;
    }

    public String getCreditCardHash() {
        return creditCardHash;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getPrice() {
        return price;
    }
}
