package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

public class AutoMailDetails {

    @SerializedName("Name")
    String name;

    @SerializedName("ContactNo")
    String contactNo;

    @SerializedName("Gender")
    String gender;

    @SerializedName("Address")
    String address;

    @SerializedName("Age")
    String age;

    @SerializedName("AdharCard")
    String adharCard;

    @SerializedName("Remark")
    String remark;

    @SerializedName("CenterType")
    String centerType;

    @SerializedName("CenterName")
    String centerName;

    @SerializedName("CreatedAt")
    String createdAt;

    public String getName() {
        return name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getAge() {
        return age;
    }

    public String getAdharCard() {
        return adharCard;
    }

    public String getRemark() {
        return remark;
    }

    public String getCenterType() {
        return centerType;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
