package com.futuregenerations.ternaquarantinecenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AutoEmailReceiver extends BroadcastReceiver {

    String date;
    String createdBy;

    String dataCall = "getRemarkReportDetails";

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnected(context)&&UserSharedPrefManager.getInstance(context).isLoggedIn()) {
            getReportDetails(context);
            Log.d("RECEIVER","Alarm Started");
        }
        else {
            Toast.makeText(context, "Please Turn Your Internet Connection ON", Toast.LENGTH_SHORT).show();
        }
    }

    private void getReportDetails(final Context context) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        date = dateFormat.format(calendar.getTime());
        createdBy = UserSharedPrefManager.getInstance(context).getData().getName();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("date",date);
        hashMap.put("createdby",createdBy);

        Call<AutoMailData> mailDataCall = apiInterface.MAIL_DATA_CALL(dataCall,hashMap);
        mailDataCall.enqueue(new Callback<AutoMailData>() {
            @Override
            public void onResponse(@NonNull Call<AutoMailData> call,@NonNull Response<AutoMailData> response) {
                if (response.isSuccessful()) {
                    AutoMailData mailData = response.body();
                    if (mailData!=null) {
                        if (!mailData.isError()) {
                            if (!mailData.getDetails().isEmpty()) {
                                sendDataToPDF(context,mailData);
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(context, "Invalid Response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AutoMailData> call,@NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataToPDF(final Context context, final AutoMailData mailData) {
        PDFWriter writer = new PDFWriter(context);
        try {

            HashMap<String,Object> hashMap = writer.createAutoPDF(mailData.getDetails());
            boolean isGenerated = Boolean.parseBoolean(String.valueOf(hashMap.get("success")));

            if (isGenerated) {
                String pdfName = String.valueOf(hashMap.get("name"));
                String pdfFile = String.valueOf(hashMap.get("path"));

                RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"),createdBy);

                File file = new File(pdfFile);
                RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("pdf",pdfName,fileBody);
                Call<GetResponseModel> call = apiInterface.PDF_UPLOAD_CALL("sendMail",part,nameBody);
                call.enqueue(new Callback<GetResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<GetResponseModel> call,@NonNull Response<GetResponseModel> response) {
                        if (response.isSuccessful()) {
                            GetResponseModel model = response.body();
                            if (model != null) {
                                Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                mailData.getDetails().clear();
                                Log.d("EmailJobService","Mail Sent");
                            }
                            else {
                                mailData.getDetails().clear();
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            mailData.getDetails().clear();
                            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GetResponseModel> call,@NonNull Throwable t) {
                        Toast.makeText(context, "Throwable API : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                        mailData.getDetails().clear();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null!=activeNetwork) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
            else return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        else {
            return false;
        }
    }
}
