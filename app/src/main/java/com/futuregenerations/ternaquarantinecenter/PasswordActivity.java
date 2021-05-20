package com.futuregenerations.ternaquarantinecenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PasswordActivity extends AppCompatActivity {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(URL.BASE_URL)
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    String oldPassword,name,centerType,center,userType;
    String newPassword, userName;
    String passwordCall = "changePassword";
    ProgressDialog progressDialog;

    TextInputEditText editTextOldPass, editTextNewPass, editTextConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        userName = intent.getStringExtra("userName");
        centerType = intent.getStringExtra("centerType");
        center = intent.getStringExtra("center");
        oldPassword = intent.getStringExtra("password");
        userType = intent.getStringExtra("userType");
        UserSharedPrefData sharedPrefData = new UserSharedPrefData(name,userName,centerType,center,userType);
        UserSharedPrefManager.getInstance(this).userData(sharedPrefData);

        if (!TextUtils.equals(oldPassword,"12345")) {
            Intent newIntent = new Intent(PasswordActivity.this,HomeActivity.class);
            startActivity(newIntent);
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        editTextOldPass = findViewById(R.id.oldPass);
        editTextNewPass = findViewById(R.id.newPass);
        editTextConfirmPass = findViewById(R.id.confirmPass);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_password,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menu_save_password) {
            checkIfEmpty();
        }
        return true;
    }

    private void checkIfEmpty() {
        if (TextUtils.isEmpty(editTextOldPass.getText())) {
            editTextOldPass.setError("Please enter your old password");
            editTextOldPass.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextNewPass.getText())) {
            editTextNewPass.setError("Please enter a new password");
            editTextNewPass.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextConfirmPass.getText())) {
            editTextConfirmPass.setError("Please re-enter your new password");
            editTextConfirmPass.requestFocus();
        }
        else if (!TextUtils.equals(editTextNewPass.getText(),editTextConfirmPass.getText())) {
            editTextNewPass.setError("Password Mismatched");
            editTextConfirmPass.setError("Password Mismatched");
            editTextConfirmPass.requestFocus();
        }
        else {

            oldPassword = Objects.requireNonNull(editTextOldPass.getText()).toString();
            newPassword = Objects.requireNonNull(editTextNewPass.getText()).toString();

            saveChangedPassword();
        }
    }

    private void saveChangedPassword() {
        progressDialog.show();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("username",userName);
        hashMap.put("password",newPassword);

        Call<GetResponseModel> changePasswordCall = apiInterface.CHANGE_PASSWORD_CALL(passwordCall,hashMap);
        changePasswordCall.enqueue(new Callback<GetResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GetResponseModel> call,@NonNull Response<GetResponseModel> response) {
                if (response.isSuccessful()) {
                    GetResponseModel responseModel = response.body();
                    if (responseModel!=null) {
                        if (!responseModel.isError()) {

                            progressDialog.dismiss();
                            UserSharedPrefData sharedPrefData = new UserSharedPrefData(name,userName,centerType,center,userType);
                            UserSharedPrefManager.getInstance(PasswordActivity.this).userData(sharedPrefData);
                            Toast.makeText(PasswordActivity.this, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(PasswordActivity.this, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(PasswordActivity.this, "Something Went Wrong, Please Try Again Later...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(PasswordActivity.this, "Please Try Again Later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetResponseModel> call,@NonNull Throwable t) {
                Toast.makeText(PasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}