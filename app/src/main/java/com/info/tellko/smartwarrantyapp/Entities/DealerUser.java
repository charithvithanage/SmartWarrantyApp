package com.info.tellko.smartwarrantyapp.Entities;

public class DealerUser {
    private long id;

    private String dealerCode;
    private String nic;
    private String username;
    private String password;
    private boolean dealerUserStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDealerUserStatus() {
        return dealerUserStatus;
    }

    public void setDealerUserStatus(boolean dealerUserStatus) {
        this.dealerUserStatus = dealerUserStatus;
    }
}
