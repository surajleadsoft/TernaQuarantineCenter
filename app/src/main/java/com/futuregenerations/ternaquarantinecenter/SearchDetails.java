package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

class SearchDetails {
    @SerializedName("name")
    String name;

    @SerializedName("date")
    String date;

    @SerializedName("remark")
    String remark;

    @SerializedName("type")
    String centerType;

    @SerializedName("center")
    String center;

    @SerializedName("adhar")
    String adhar;

    @SerializedName("age")
    String age;

    public String getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getRemark() {
        return remark;
    }

    public String getCenterType() {
        return centerType;
    }

    public String getCenter() {
        return center;
    }

    public String getAdhar() {
        return adhar;
    }
}
