package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

class Centers {

    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
