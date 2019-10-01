package com.info.charith.smartwarrantyapp.Entities;

import org.json.JSONArray;

public class ServerResponse {
    boolean success;
    String message;
    JSONArray object;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONArray getObject() {
        return object;
    }

    public void setObject(JSONArray object) {
        this.object = object;
    }
}
