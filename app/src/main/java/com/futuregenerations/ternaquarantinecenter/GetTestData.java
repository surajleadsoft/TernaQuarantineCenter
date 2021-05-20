package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class GetTestData {
    @SerializedName("message")
    String message;
    @SerializedName("error")
    boolean error;
    @SerializedName("tests")
    List<Tests> tests;

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }

    public List<Tests> getTests() {
        return tests;
    }
}
