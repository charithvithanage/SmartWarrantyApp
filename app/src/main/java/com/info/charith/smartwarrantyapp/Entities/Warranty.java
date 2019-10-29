package com.info.charith.smartwarrantyapp.Entities;

public class Warranty {
    private long id;

    private String brand;
    private String model;
    private String imei;
    private String serialNo;
    private String customerName;
    private String address;
    private String contactNo;
    private String email;
    private String dealerCode;
    private String referenceNo;
    private String country;
    private String warrantyActivatedDate;
    private String accessoryWarrantyStatus;
    private String deviceWarrantyStatus;
    private String serviceWarrantyStatus;
    private String activationStatus;
    private String dapStatus;
    private String dealerUserName;
    private String reference;
    private String uploadDate;
    private String district;
    private String activationDate;

    String timeStamp;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
    }

    public String getDapStatus() {
        return dapStatus;
    }

    public void setDapStatus(String dapStatus) {
        this.dapStatus = dapStatus;
    }

    public String getDealerUserName() {
        return dealerUserName;
    }

    public void setDealerUserName(String dealerUserName) {
        this.dealerUserName = dealerUserName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getWarrantyActivatedDate() {
        return warrantyActivatedDate;
    }

    public void setWarrantyActivatedDate(String warrantyActivatedDate) {
        this.warrantyActivatedDate = warrantyActivatedDate;
    }

    public String getAccessoryWarrantyStatus() {
        return accessoryWarrantyStatus;
    }

    public void setAccessoryWarrantyStatus(String accessoryWarrantyStatus) {
        this.accessoryWarrantyStatus = accessoryWarrantyStatus;
    }

    public String getDeviceWarrantyStatus() {
        return deviceWarrantyStatus;
    }

    public void setDeviceWarrantyStatus(String deviceWarrantyStatus) {
        this.deviceWarrantyStatus = deviceWarrantyStatus;
    }

    public String getServiceWarrantyStatus() {
        return serviceWarrantyStatus;
    }

    public void setServiceWarrantyStatus(String serviceWarrantyStatus) {
        this.serviceWarrantyStatus = serviceWarrantyStatus;
    }
}
