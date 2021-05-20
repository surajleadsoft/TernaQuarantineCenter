package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class GetPatientRemarks {

    @SerializedName("error")
    boolean error;

    @SerializedName("remarks")
    List<PatientsRemarks> remarks;

    public boolean isError() {
        return error;
    }

    public List<PatientsRemarks> getRemarks() {
        return remarks;
    }
}
