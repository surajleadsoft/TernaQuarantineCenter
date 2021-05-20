package com.futuregenerations.ternaquarantinecenter;

public class UserSharedPrefData {
    String name, email, centerType, center, userType;

    public UserSharedPrefData(String name, String email, String centerType, String center, String userType) {
        this.name = name;
        this.email = email;
        this.centerType = centerType;
        this.center = center;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCenterType() {
        return centerType;
    }

    public String getCenter() {
        return center;
    }

    public String getUserType() {
        return userType;
    }
}
