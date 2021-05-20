package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class GetQuarantineCenters {

    @SerializedName("error")
    boolean error;

    @SerializedName("centers")
    List<Centers> centers;

    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }

    public List<Centers> getCenters() {
        return centers;
    }
}
