package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutoMailData {
    @SerializedName("error")
    boolean error;

    @SerializedName("message")
    String message;

    @SerializedName("details")
    List<AutoMailDetails> details;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<AutoMailDetails> getDetails() {
        return details;
    }
}
