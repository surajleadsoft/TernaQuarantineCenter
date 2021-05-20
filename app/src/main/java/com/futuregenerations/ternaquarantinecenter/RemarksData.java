package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RemarksData {
    @SerializedName("error")
    boolean error;

    @SerializedName("message")
    String message;

    @SerializedName("remarks")
    List<Remarks> remarks;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<Remarks> getRemarks() {
        return remarks;
    }
}
