package com.info.tellko.smartwarrantyapp.Entities;

public class AppObject {

    String appName;
    Integer imgRes;

    public AppObject(String appName, Integer imgRes) {
        this.appName = appName;
        this.imgRes = imgRes;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getImgRes() {
        return imgRes;
    }

    public void setImgRes(Integer imgRes) {
        this.imgRes = imgRes;
    }
}
