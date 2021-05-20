package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

public class GetEmp {

    @SerializedName("name")
    String name;

    @SerializedName("type")
    String userType;

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }
}
