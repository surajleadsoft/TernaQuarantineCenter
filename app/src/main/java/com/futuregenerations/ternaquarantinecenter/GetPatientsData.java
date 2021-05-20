package com.futuregenerations.ternaquarantinecenter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPatientsData {

    @SerializedName("error")
    boolean error;

    @SerializedName("patients")
    List<Patients> patients;

    public boolean isError() {
        return error;
    }

    public List<Patients> getPatients() {
        return patients;
    }
}
