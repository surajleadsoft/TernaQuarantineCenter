package com.futuregenerations.ternaquarantinecenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.ProgressDialog;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RemarkHistoryActivity extends AppCompatActivity {

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL.BASE_URL)
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    TextView textViewId, textViewName, textViewAdhar;

    ListView listView;

    String name, adhaar, age, remarkCall="getRemarks";

    List<PatientsRemarks> patientsRemarksList;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark_history);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

        textViewId = findViewById(R.id.text_patient_id);
        textViewName = findViewById(R.id.text_patient_name);
        textViewAdhar = findViewById(R.id.text_patient_adhar);

        listView = findViewById(R.id.patient_remark_list);

        patientsRemarksList = new ArrayList<>();

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        age = intent.getStringExtra("age");
        adhaar = intent.getStringExtra("adhar");

        String text = "Adhar Card : ";
        String adharText = text+adhaar;
        String ageText = "Age : "+age;

        textViewName.setText(name);
        textViewAdhar.setText(adharText);
        textViewId.setText(ageText);

        if (isConnected()) {
            getListData();
        }
        else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.app_icon)
                    .setMessage("Please check your internet connection and try again...!")
                    .setTitle("No Internet Connection")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    }).show();
        }
    }

    private void getListData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("name",name);
        hashMap.put("adhar",adhaar);

        Call<GetPatientRemarks> patientRemarksCall = apiInterface.PATIENT_REMARKS_CALL(remarkCall,hashMap);

        patientRemarksCall.enqueue(new Callback<GetPatientRemarks>() {
            @Override
            public void onResponse(@NonNull Call<GetPatientRemarks> call,@NonNull Response<GetPatientRemarks> response) {
                if (response.isSuccessful()) {
                    GetPatientRemarks patientRemarks = response.body();
                    if (patientRemarks!=null) {
                        if (!patientRemarks.isError()) {
                            CustomRemarkAdapter adapter = new CustomRemarkAdapter(RemarkHistoryActivity.this,patientRemarks.getRemarks());
                            listView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(RemarkHistoryActivity.this, "Failed To Load Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(RemarkHistoryActivity.this, "Failed To Load Data", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RemarkHistoryActivity.this, "Failed To Load Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetPatientRemarks> call,@NonNull Throwable t) {
                progressDialog.dismiss();
//                Toast.makeText(RemarkHistoryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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