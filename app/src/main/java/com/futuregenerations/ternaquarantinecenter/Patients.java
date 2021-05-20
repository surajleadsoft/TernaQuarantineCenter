package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

public class Patients {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @SerializedName("contactno")
    String contactno;

    @SerializedName("adhar")
    String adhar;

    @SerializedName("address")
    String address;

    @SerializedName("gender")
    String gender;

    @SerializedName("age")
    String age;

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactno() {
        return contactno;
    }

    public String getAdhar() {
        return adhar;
    }
}
