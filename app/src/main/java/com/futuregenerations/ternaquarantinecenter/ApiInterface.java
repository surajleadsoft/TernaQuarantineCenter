package com.futuregenerations.ternaquarantinecenter;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("api.php")
    @FormUrlEncoded
    Call<GetResponseModel> PATIENT_API_CALL(@Query("apicall") String apiCall, @FieldMap HashMap<String,String> hashMap);

    @GET("api.php")
    Call<GetQuarantineCenters> QUARANTINE_CENTER_TYPES_CALL(@Query("apicall") String centerTypeCall);

    @GET("api.php")
    Call<GetPatientsData> PATIENTS_DATA_CALL(@Query("apicall") String patientsCall);

    @POST("api.php")
    @FormUrlEncoded
    Call<GetPatientRemarks> PATIENT_REMARKS_CALL(@Query("apicall") String remarkCall, @FieldMap HashMap<String,String> hashMap);

    @POST("api.php")
    @FormUrlEncoded
    Call<GetQuarantineCenters> QUARANTINE_CENTERS_CALL(@Query("apicall") String centerCall, @Field("type") String centerType);

    @GET("api.php")
    Call<GetTestData> TEST_DATA_CALL(@Query("apicall") String testCall);

    @POST("api.php")
    @FormUrlEncoded
    Call<GetLoginData> LOGIN_DATA_CALL(@Query("apicall") String loginCall, @FieldMap HashMap<String,String> hashMap);

    @GET("api.php")
    Call<GetDiseasesData> DISEASES_DATA_CALL(@Query("apicall") String diseasesCall);

    @POST("api.php")
    @FormUrlEncoded
    Call<UserInfoModel> LOGIN_DETAILS_CALL(@Query("apicall") String loginDetailsCall, @Field("email") String email);

    @POST("api.php")
    @FormUrlEncoded
    Call<AdvancedSearch> ADVANCED_SEARCH_CALL(@Query("apicall") String byAgeCall, @FieldMap HashMap<String,String> hashMap);

    @POST("api.php")
    @FormUrlEncoded
    Call<GetResponseModel> CHANGE_PASSWORD_CALL(@Query("apicall") String passwordCall, @FieldMap HashMap<String,String> hashMap);

    @Multipart
    @POST("api.php")
    Call<GetResponseModel> PDF_UPLOAD_CALL(@Query("apicall")String uploadCall,@Part MultipartBody.Part pdf, @Part("name") RequestBody name);

    @POST("api.php")
    @FormUrlEncoded
    Call<AutoMailData> MAIL_DATA_CALL(@Query("apicall") String autoMailCall, @FieldMap HashMap<String,String> hashMap);

    @GET("api.php")
    Call<RemarksData> REMARKS_DATA_CALL(@Query("apicall") String remarksCall);

    @GET("api.php")
    Call<AddressDetails> ADDRESS_DETAILS_CALL(@Query("apicall") String addressCall);
}
