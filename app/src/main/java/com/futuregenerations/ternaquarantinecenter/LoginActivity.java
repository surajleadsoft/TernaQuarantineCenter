package com.futuregenerations.ternaquarantinecenter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(URL.BASE_URL)
            .build();

    static final int PERMISSION_REQUEST_CODE = 121;

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    TextInputEditText editTextUserName, editTextPassword;

    Spinner spinnerCenterType, spinnerCenter;

    String userName, password, centerType, center;

    ProgressDialog progressDialog;

    String centerTypeCall = "getCenterType";
    String centerCall = "getCenters";
    String loginCall = "Emplogin";
    String login_success = "Login Successfull !!";
    String loginDetailCall = "getEmpDetail";

    List<String> centerTypeNames, centerNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

        checkPermissions();

        centerTypeNames = new ArrayList<>();
        centerNames = new ArrayList<>();

        centerTypeNames.add("Select Center Type");

        editTextUserName = findViewById(R.id.login_user);
        editTextPassword = findViewById(R.id.login_pass);

        spinnerCenterType = findViewById(R.id.login_spinner_centerType);
        spinnerCenter = findViewById(R.id.login_spinner_center);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        if (isConnected()) {
            getCenterTypes();
        }

        spinnerCenterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCenters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void getCenters() {
        centerType = spinnerCenterType.getSelectedItem().toString();
        centerNames.clear();
        progressDialog.show();
        Call<GetQuarantineCenters> centersCall = apiInterface.QUARANTINE_CENTERS_CALL(centerCall,centerType);
        centersCall.enqueue(new Callback<GetQuarantineCenters>() {
            @Override
            public void onResponse(@NonNull Call<GetQuarantineCenters> call,@NonNull Response<GetQuarantineCenters> response) {
                if (response.isSuccessful()) {
                    GetQuarantineCenters quarantineCenters = response.body();
                    if (quarantineCenters!=null) {
                        if (!quarantineCenters.isError()) {

                            if (quarantineCenters.getCenters()!=null) {

                                centerNames.add("Select Center");

                                for (int i = 0; i < quarantineCenters.getCenters().size(); i++) {
                                    centerNames.add(quarantineCenters.getCenters().get(i).getName());
                                }
                            }

                            SpinnerAdapter adapter = new ArrayAdapter<>(LoginActivity.this,android.R.layout.simple_spinner_dropdown_item,centerNames);
                            spinnerCenter.setAdapter(adapter);

                            progressDialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetQuarantineCenters> call,@NonNull Throwable t) {
//                Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        });
    }

    private void getCenterTypes() {
        progressDialog.show();
        Call<GetQuarantineCenters> centerTypesCall = apiInterface.QUARANTINE_CENTER_TYPES_CALL(centerTypeCall);

        centerTypesCall.enqueue(new Callback<GetQuarantineCenters>() {
            @Override
            public void onResponse(@NonNull Call<GetQuarantineCenters> call, @NonNull Response<GetQuarantineCenters> response) {
                if (response.isSuccessful()) {
                    GetQuarantineCenters centerTypes = response.body();
                    if (centerTypes!=null) {
                        if (!centerTypes.isError()) {

                            if (centerTypes.getCenters()!=null) {
                                for (int i = 0; i < centerTypes.getCenters().size(); i++) {
                                    centerTypeNames.add(centerTypes.getCenters().get(i).getName());
                                }
                            }

                            SpinnerAdapter adapter = new ArrayAdapter<>(LoginActivity.this,android.R.layout.simple_spinner_dropdown_item,centerTypeNames);
                            spinnerCenterType.setAdapter(adapter);

                            progressDialog.dismiss();

                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please Try Again later", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetQuarantineCenters> call, @NonNull Throwable t) {
//                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginUser(View view) {
        progressDialog.show();

        if (TextUtils.isEmpty(editTextUserName.getText())) {
            editTextUserName.setError("Please Enter your Email ID");
            editTextUserName.requestFocus();
            progressDialog.dismiss();
        }
        else if (TextUtils.isEmpty(editTextPassword.getText())) {
            editTextPassword.setError("Please Enter your Password");
            editTextPassword.requestFocus();
            progressDialog.dismiss();
        }
        else if (spinnerCenterType.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setTitle(R.string.app_name)
                    .setMessage("Please select your Center Type before Signing in !")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
            progressDialog.dismiss();
        }
        else if (spinnerCenter.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.app_name)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setMessage("Please select your Center before Signing in !")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
            progressDialog.dismiss();
        }
        else {
            processLogin();
        }

    }

    private void processLogin() {
        userName = Objects.requireNonNull(editTextUserName.getText()).toString();
        password = Objects.requireNonNull(editTextPassword.getText()).toString();
        centerType = spinnerCenterType.getSelectedItem().toString();
        center = spinnerCenter.getSelectedItem().toString();

        progressDialog.show();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("username",userName);
        hashMap.put("password",password);
        hashMap.put("centertype",centerType);
        hashMap.put("centername",center);

        Call<GetLoginData> loginDataCall = apiInterface.LOGIN_DATA_CALL(loginCall,hashMap);
        loginDataCall.enqueue(new Callback<GetLoginData>() {
            @Override
            public void onResponse(@NonNull Call<GetLoginData> call,@NonNull Response<GetLoginData> response) {
                if (response.isSuccessful()) {
                    GetLoginData loginData = response.body();
                    if (loginData!=null) {
                        if (!loginData.isError()) {
                            if (TextUtils.equals(login_success,loginData.getMessage())) {

                                processLoginDetails();

                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, loginData.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Something Went Wrong...!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetLoginData> call,@NonNull Throwable t) {
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processLoginDetails() {
        Call<UserInfoModel> loginDetailsCall = apiInterface.LOGIN_DETAILS_CALL(loginDetailCall,userName);
        loginDetailsCall.enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoModel> call,@NonNull Response<UserInfoModel> response) {
                if (response.isSuccessful()) {
                    UserInfoModel loginData = response.body();
                    if (loginData!=null) {
                        if (!loginData.isError()) {
                            String name = loginData.getEmp().get(0).getName();
                            String userType = loginData.getEmp().get(0).getUserType();
                            Intent intent = new Intent(LoginActivity.this,PasswordActivity.class);
                            intent.putExtra("name",name);
                            intent.putExtra("userName",userName);
                            intent.putExtra("centerType",centerType);
                            intent.putExtra("center",center);
                            intent.putExtra("password",password);
                            intent.putExtra("userType",userType);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoModel> call,@NonNull Throwable t) {
                progressDialog.dismiss();
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnected()) {
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
}