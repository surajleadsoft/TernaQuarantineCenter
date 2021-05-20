package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

public class PatientsRemarks {

    @SerializedName("date")
    String date;

    @SerializedName("remark")
    String remark;

    @SerializedName("center")
    String center;

    public String getDate() {
        return date;
    }

    public String getRemark() {
        return remark;
    }

    public String getCenter() {
        return center;
    }
}
