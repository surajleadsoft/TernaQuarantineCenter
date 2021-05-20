package com.futuregenerations.ternaquarantinecenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RemarksFragment extends Fragment {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(URL.BASE_URL)
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    TextInputEditText editTextDate;
    MaterialAutoCompleteTextView editTextName, editTextContact, editTextAdhaarCard, editTextRemark;

    Spinner spinner,spinnerCenter;

    String name, contactno, adhar, date, remark, center,centerType, createdBy, createdAt,gender, address, age;
    String apicall = "updateRemark", centerCall = "getCenters";
    String remarksCall = "getRemarkList";
    List<Remarks> remarksList;

    List<String> centerNames;

    Context context;
    ProgressDialog progressDialog;

    ScrollView mainLayout;

    public RemarksFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_remarks,container,false);

        centerNames = new ArrayList<>();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        createdBy = UserSharedPrefManager.getInstance(context).getData().getName();

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        createdAt = timeFormat.format(calendar.getTime());

        findViews(rootView);

        editTextDate.setText(getDate());

        if (isConnected()) {
            getQuarantineCenters();
            getPatientsData();
            getRemarksList();
        }
        else {
            new AlertDialog.Builder(context)
                    .setIcon(R.drawable.app_icon)
                    .setMessage("Please check your internet connection and try again...!")
                    .setTitle("No Internet Connection")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Objects.requireNonNull(getActivity()).finish();
                        }
                    }).show();
        }

        return rootView;
    }

    private void getRemarksList() {
        progressDialog.show();
        Call<RemarksData> remarksDataCall = apiInterface.REMARKS_DATA_CALL(remarksCall);
        remarksDataCall.enqueue(new Callback<RemarksData>() {
            @Override
            public void onResponse(@NonNull Call<RemarksData> call,@NonNull Response<RemarksData> response) {
                if (response.isSuccessful()) {
                    RemarksData remarksData = response.body();
                    if (remarksData!=null && !remarksData.isError()) {
                        if (remarksData.getRemarks()!=null) {
                            remarksList.addAll(remarksData.getRemarks());
                            AutoTextRemarksAdapter adapter = new AutoTextRemarksAdapter(context,remarksList);
                            editTextRemark.setAdapter(adapter);
                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<RemarksData> call,@NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void getPatientsData() {
        progressDialog.show();
        String patientsCall = "allPatients";
        Call<GetPatientsData> patientsDataCall = apiInterface.PATIENTS_DATA_CALL(patientsCall);
        patientsDataCall.enqueue(new Callback<GetPatientsData>() {
            @Override
            public void onResponse(@NonNull Call<GetPatientsData> call,@NonNull Response<GetPatientsData> response) {
                if (response.isSuccessful()) {
                    GetPatientsData patientsData = response.body();
                    if (patientsData!=null) {
                        if (!patientsData.isError()) {
                            setEditTextAdapters(patientsData);
                            progressDialog.dismiss();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                    }
                }
                else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetPatientsData> call,@NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void setEditTextAdapters(final GetPatientsData patientsData) {
        AutoTextAdapter adapter = new AutoTextAdapter(context,patientsData.getPatients());
        editTextName.setAdapter(adapter);

        AutoTextNumberAdapter numberAdapter = new AutoTextNumberAdapter(context,patientsData.getPatients());
        editTextContact.setAdapter(numberAdapter);

        AutoTextAdharAdapter adharAdapter = new AutoTextAdharAdapter(context,patientsData.getPatients());
        editTextAdhaarCard.setAdapter(adharAdapter);

        editTextName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number = patientsData.getPatients().get(i).getContactno();
                editTextContact.setText(number);
                editTextAdhaarCard.setText(patientsData.getPatients().get(i).getAdhar());
                gender = patientsData.getPatients().get(i).getGender();
                address = patientsData.getPatients().get(i).getAddress();
                age = patientsData.getPatients().get(i).getAge();
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),0);
            }
        });

        editTextContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = patientsData.getPatients().get(i).getName();
                editTextName.setText(name);
                gender = patientsData.getPatients().get(i).getGender();
                address = patientsData.getPatients().get(i).getAddress();
                age = patientsData.getPatients().get(i).getAge();
                editTextAdhaarCard.setText(patientsData.getPatients().get(i).getAdhar());
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),0);
            }
        });

        editTextAdhaarCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number = patientsData.getPatients().get(i).getContactno();
                editTextContact.setText(number);
                gender = patientsData.getPatients().get(i).getGender();
                address = patientsData.getPatients().get(i).getAddress();
                age = patientsData.getPatients().get(i).getAge();
                editTextName.setText(patientsData.getPatients().get(i).getName());
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),0);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCenters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getQuarantineCenters() {

        progressDialog.show();

        String centersCall = "getCenterType";
        Call<GetQuarantineCenters> quarantineCenterCall = apiInterface.QUARANTINE_CENTER_TYPES_CALL(centersCall);

        quarantineCenterCall.enqueue(new Callback<GetQuarantineCenters>() {
            @Override
            public void onResponse(@NonNull Call<GetQuarantineCenters> call, @NonNull Response<GetQuarantineCenters> response) {
                if (response.isSuccessful()) {
                    GetQuarantineCenters quarantineCenter = response.body();

                    if (quarantineCenter != null) {

                        if (!quarantineCenter.isError()) {

                            List<String> name = new ArrayList<>();

                            name.add("Select Center");

                            for (int i = 0; i < quarantineCenter.getCenters().size(); i++) {
                                name.add(quarantineCenter.getCenters().get(i).getName());
                            }

                            SpinnerAdapter adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,name);
                            spinner.setAdapter(adapter);

                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(context, "API : "+quarantineCenter.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Something Went Wrong, Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Response Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetQuarantineCenters> call, @NonNull Throwable t) {
                progressDialog.dismiss();
//                Toast.makeText(getContext(),"Throwable : "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private void findViews(View rootView) {
        editTextName = rootView.findViewById(R.id.patient_name);
        editTextContact = rootView.findViewById(R.id.patient_number);
        editTextAdhaarCard = rootView.findViewById(R.id.patient_adhaar);
        editTextDate = rootView.findViewById(R.id.patient_date);
        editTextRemark = rootView.findViewById(R.id.patient_remark);

        spinner = rootView.findViewById(R.id.spinner_center_type);
        spinnerCenter = rootView.findViewById(R.id.spinner_center);

        remarksList = new ArrayList<>();

        mainLayout = rootView.findViewById(R.id.update_layout);
    }

    private void getCenters() {
        centerType = spinner.getSelectedItem().toString();
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

                            SpinnerAdapter adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,centerNames);
                            spinnerCenter.setAdapter(adapter);

                            progressDialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Please Try Again...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
//                        finish();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Please Try Again...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
//                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetQuarantineCenters> call,@NonNull Throwable t) {
//                Toast.makeText(getContext(), "Please Try Again...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
//                finish();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_remarks,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.save_remarks:
                checkIfEmpty();
                break;

            case R.id.menu_about:
                startActivity(new Intent(context,AboutActivity.class));
        }

        return true;
    }

    private void getDataToString() {

        name = Objects.requireNonNull(editTextName.getText()).toString();
        contactno = Objects.requireNonNull(editTextContact.getText()).toString();
        adhar = Objects.requireNonNull(editTextAdhaarCard.getText()).toString();
        date = Objects.requireNonNull(editTextDate.getText()).toString();
        remark = Objects.requireNonNull(editTextRemark.getText()).toString();

        center = spinnerCenter.getSelectedItem().toString();
        centerType = spinner.getSelectedItem().toString();

        storeDataToDatabase();
    }

    private void checkIfEmpty() {
        if (TextUtils.isEmpty(editTextName.getText())) {
            editTextName.setError("Please Enter Name");
            editTextName.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextContact.getText())) {
            editTextContact.setError("Please Enter Contact No.");
            editTextContact.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextAdhaarCard.getText())) {
            editTextAdhaarCard.setError("Please Enter Adhaar Card No.");
            editTextAdhaarCard.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextDate.getText())) {
            editTextDate.setError("Please Enter Date");
            editTextDate.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextRemark.getText())) {
            editTextRemark.setError("Please Enter Remark");
            editTextRemark.requestFocus();
        }
        else if (spinner.getSelectedItemPosition() == 0) {

            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle(context.getResources().getString(R.string.app_name))
                    .setMessage("Please Select a Center Type from above list.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }

        else if (spinnerCenter.getSelectedItemPosition() == 0) {

            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle(context.getResources().getString(R.string.app_name))
                    .setMessage("Please Select a Center from above list.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }

        else {
            getDataToString();
        }
    }

    private void storeDataToDatabase() {

        progressDialog.show();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("contactno",contactno);
        hashMap.put("gender",gender);
        hashMap.put("add",address);
        hashMap.put("age",age);
        hashMap.put("adhar",adhar);
        hashMap.put("date",date);
        hashMap.put("remark",remark);
        hashMap.put("centertype",centerType);
        hashMap.put("center",center);
        hashMap.put("CreatedBy",createdBy);
        hashMap.put("CreatedAt",createdAt);

        Call<GetResponseModel> updateRemarkCall = apiInterface.PATIENT_API_CALL(apicall,hashMap);

        updateRemarkCall.enqueue(new Callback<GetResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GetResponseModel> call,@NonNull Response<GetResponseModel> response) {
                if (response.isSuccessful()) {
                    GetResponseModel responseModel = response.body();
                    if (responseModel!=null) {
                        if (!responseModel.isError()) {
                            Toast.makeText(getContext(), responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            clearLayout();
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(getContext(), responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetResponseModel> call,@NonNull Throwable t) {
//                Toast.makeText(getContext(), "Throwable : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void clearLayout() {
        editTextName.setText("");
        editTextName.clearFocus();

        editTextContact.setText("");
        editTextContact.clearFocus();

        editTextAdhaarCard.setText("");
        editTextAdhaarCard.clearFocus();

        editTextDate.setText(getDate());
        editTextDate.clearFocus();

        editTextRemark.setText("");
        editTextRemark.clearFocus();

        spinner.setSelection(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        clearLayout();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearLayout();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPatientsData();
        clearLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        clearLayout();
        getPatientsData();
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
