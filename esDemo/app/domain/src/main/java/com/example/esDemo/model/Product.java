package com.example.esDemo.model;

import java.util.Date;

public class Product {
    private String name;
    private double price;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Product(){

    }

    public Product(String name, double price, Date date) {
        this.name = name;
        this.price = price;
        this.date = date;
    }
}
