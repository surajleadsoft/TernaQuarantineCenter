package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressDetails {

    @SerializedName("error")
    boolean error;

    @SerializedName("message")
    String message;

    @SerializedName("address")
    List<Address> addresses;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<Address> getAddresses() {
        return addresses;
    }
}
