package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdvancedSearch {

    @SerializedName("error")
    boolean error;

    @SerializedName("message")
    String message;

    @SerializedName("details")
    List<SearchDetails> details;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<SearchDetails> getDetails() {
        return details;
    }
}
