package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class GetDiseasesData {
    @SerializedName("error")
    boolean error;

    @SerializedName("message")
    String message;

    @SerializedName("diseases")
    List<Diseases> diseases;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<Diseases> getDiseases() {
        return diseases;
    }
}
