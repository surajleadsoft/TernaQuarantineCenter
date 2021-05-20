package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserInfoModel {

    @SerializedName("message")
    String message;

    @SerializedName("error")
    boolean error;

    @SerializedName("emp")
    List<GetEmp> emp;

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }

    public List<GetEmp> getEmp() {
        return emp;
    }
}
