package com.info.charith.smartwarrantyapp.Entities;

import android.content.Intent;

import org.joda.time.DateTime;

public class SummaryReport {

    DateTime date;
    String brand;
    String model;
    Integer qty;

    public SummaryReport(DateTime date, String brand, String model, Integer qty) {
        this.date = date;
        this.brand = brand;
        this.model = model;
        this.qty = qty;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
