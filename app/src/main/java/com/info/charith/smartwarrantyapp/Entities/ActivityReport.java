package com.info.charith.smartwarrantyapp.Entities;

import org.joda.time.DateTime;

public class ActivityReport {
    DateTime date;
    String user;
    String brand;
    String model;
    String imei;

    public ActivityReport(DateTime date, String user, String brand, String model, String imei) {
        this.date = date;
        this.user = user;
        this.brand = brand;
        this.model = model;
        this.imei = imei;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
