package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("Name")
    String name;

    public String getName() {
        return name;
    }
}
