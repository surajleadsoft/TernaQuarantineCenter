package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

class GetLoginData {
    @SerializedName("error")
    boolean error;

    @SerializedName("message")
    String message;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
