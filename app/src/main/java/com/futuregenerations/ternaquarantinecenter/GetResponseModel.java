package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

public class GetResponseModel {
    @SerializedName("message")
    String message;
    @SerializedName("error")
    boolean error;

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }
}
