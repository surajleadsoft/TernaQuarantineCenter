package com.futuregenerations.ternaquarantinecenter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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

public class AdvancedSearchActivity extends AppCompatActivity {
    
    List<String> spinnerItemList,spinnerGenderList;
    List<String> name,centerNames;
    Spinner spinnerBY, spinnerCenterType,spinnerCenter, spinnerGender;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ProgressDialog progressDialog;
    List<SearchDetails> searchDetailsList;
    static final int PERMISSION_REQUEST_CODE = 121;

    String searchedBy;

    String searchByAgeCall = "searchPatientsByAge";

    String searchByDateCall = "searchPatientsByDate";

    String searchByGenderCall = "searchPatientsByGender";

    String searchByCenterCall = "searchPatientsByCenter";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);
        searchDetailsList = new ArrayList<>();

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

        name = new ArrayList<>();
        centerNames = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        recyclerView = findViewById(R.id.recyclerViewAdvancedSearch);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(null);

        spinnerGenderList = new ArrayList<>();
        spinnerGenderList.add("Select Gender");
        spinnerGenderList.add("Male");
        spinnerGenderList.add("Female");
        
        spinnerItemList = new ArrayList<>();
        addItemsToSpinner();
        spinnerBY = findViewById(R.id.spinner_by);
        SpinnerAdapter adapter = new ArrayAdapter<>(AdvancedSearchActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerItemList);
        spinnerBY.setAdapter(adapter);

        getQuarantineCenters();

        spinnerBY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0 :
                        recyclerView.setAdapter(null);
                        searchDetailsList.clear();
                        break;

                    case 1 :
                        searchDetailsList.clear();
                        openDialogForAge();
                        break;

                    case 2 :
                        searchDetailsList.clear();
                        openDialogForCenters();
                        break;

                    case 3 :
                        searchDetailsList.clear();
                        openDialogForDate();
                        break;

                    case 4 :
                        searchDetailsList.clear();
                        openDialogForGender();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void openDialogForGender() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View customView = LayoutInflater.from(this).inflate(R.layout.custom_gender_search_dialog,viewGroup,false);
        builder.setView(customView);
        spinnerGender = customView.findViewById(R.id.spinner_gender);
        SpinnerAdapter adapter = new ArrayAdapter<>(AdvancedSearchActivity.this,android.R.layout.simple_spinner_dropdown_item,spinnerGenderList);
        spinnerGender.setAdapter(adapter);

        builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int centerTypePosition = spinnerGender.getSelectedItemPosition();
                if (centerTypePosition == 0) {
                    Toast.makeText(AdvancedSearchActivity.this, "Please Provide Both Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    String gender = spinnerGender.getSelectedItem().toString();
                    searchByGender(gender);
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openDialogForDate() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View customView = LayoutInflater.from(this).inflate(R.layout.custom_date_search_dialog,viewGroup,false);
        builder.setView(customView);

        final EditText editTextFromDate = customView.findViewById(R.id.editTextFromDate);
        final EditText editTextToDate = customView.findViewById(R.id.editTextToDate);

        editTextFromDate.setText(date);
        editTextToDate.setText(date);

        builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fromDate = editTextFromDate.getText().toString();
                String toDate = editTextToDate.getText().toString();
                if (TextUtils.isEmpty(fromDate) || TextUtils.isEmpty(toDate)) {
                    Toast.makeText(AdvancedSearchActivity.this, "Please Provide Both Dates", Toast.LENGTH_SHORT).show();
                }
                else {
                    searchByDate(fromDate,toDate);
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openDialogForCenters() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View customView = LayoutInflater.from(this).inflate(R.layout.custom_center_search_dialog,viewGroup,false);
        builder.setView(customView);
        spinnerCenterType = customView.findViewById(R.id.spinner_center_type);
        spinnerCenter = customView.findViewById(R.id.spinner_center);
        SpinnerAdapter adapter = new ArrayAdapter<>(AdvancedSearchActivity.this,android.R.layout.simple_spinner_dropdown_item,name);
        spinnerCenterType.setAdapter(adapter);

        spinnerCenterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCenters(spinnerCenterType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int centerTypePosition = spinnerCenterType.getSelectedItemPosition();
                int centerPosition = spinnerCenter.getSelectedItemPosition();
                if (centerTypePosition == 0) {
                    Toast.makeText(AdvancedSearchActivity.this, "Please Provide Both Details", Toast.LENGTH_SHORT).show();
                }
                else if (centerPosition == 0) {
                    Toast.makeText(AdvancedSearchActivity.this, "Please Provide Both Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    String centerType = spinnerCenterType.getSelectedItem().toString();
                    String center = spinnerCenter.getSelectedItem().toString();
                    searchByCenter(centerType,center);
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getCenters(String centerType) {
        centerNames.clear();
        String centerCall = "getCenters";
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

                            SpinnerAdapter adapter = new ArrayAdapter<>(AdvancedSearchActivity.this,android.R.layout.simple_spinner_dropdown_item,centerNames);
                            spinnerCenter.setAdapter(adapter);

                            progressDialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(AdvancedSearchActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
//                        finish();
                    }
                }
                else {
                    Toast.makeText(AdvancedSearchActivity.this, "Please Try Again...", Toast.LENGTH_SHORT).show();
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

    private void openDialogForAge() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View customView = LayoutInflater.from(this).inflate(R.layout.custom_age_search_dialog,viewGroup,false);
        builder.setView(customView);
        final EditText editTextFromAge = customView.findViewById(R.id.editTextFromAge);
        final EditText editTextToAge = customView.findViewById(R.id.editTextToAge);
        builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fromAge = editTextFromAge.getText().toString();
                String toAge = editTextToAge.getText().toString();
                if (TextUtils.isEmpty(fromAge) || TextUtils.isEmpty(toAge)) {
                    Toast.makeText(AdvancedSearchActivity.this, "Please Provide Both Ages", Toast.LENGTH_SHORT).show();
                }
                else {
                    searchByAge(fromAge,toAge);
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addItemsToSpinner() {
        spinnerItemList.add("Search By");
        spinnerItemList.add("Age");
        spinnerItemList.add("Center");
        spinnerItemList.add("Date");
        spinnerItemList.add("Gender");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_advanced_search,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_save_to_pdf :
                if (searchDetailsList.isEmpty()) {
                    Toast.makeText(this, "Please Make a search first...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                    }
                    else {
                        saveToPDF();
                    }
                }
                break;
                
            case R.id.menu_save_and_email_pdf :
                if (searchDetailsList.isEmpty()) {
                    Toast.makeText(this, "Please Make a search first...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                    }
                    else {
                        saveAndEmailPDF();
                    }
                }
                break;
        }
        return true;
    }

    private void saveAndEmailPDF() {

        if (searchDetailsList.size()!=0) {
            searchedBy = spinnerBY.getSelectedItem().toString();
            PDFWriter writer = new PDFWriter(this);
            try {
                HashMap<String,Object> hashMap = writer.createManualPDF(searchDetailsList,searchedBy);
                boolean isGenerated = Boolean.parseBoolean(String.valueOf(hashMap.get("success")));
                if (isGenerated) {

                    String uri = String.valueOf(hashMap.get("path"));
                    String fileName = String.valueOf(hashMap.get("name"));
                    String userName = UserSharedPrefManager.getInstance(this).getData().getName();
                    RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"),userName);

                    File file = new File(uri);
                    RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("pdf",fileName,fileBody);
                    Call<GetResponseModel> call = apiInterface.PDF_UPLOAD_CALL("sendMail",part,nameBody);
                    call.enqueue(new Callback<GetResponseModel>() {
                        @Override
                        public void onResponse(@NonNull Call<GetResponseModel> call,@NonNull Response<GetResponseModel> response) {
                            if (response.isSuccessful()) {
                                GetResponseModel model = response.body();
                                if (model != null) {
                                    Toast.makeText(AdvancedSearchActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                                    searchDetailsList.clear();
                                }
                                else {
                                    searchDetailsList.clear();
                                    Toast.makeText(AdvancedSearchActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                searchDetailsList.clear();
                                Toast.makeText(AdvancedSearchActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<GetResponseModel> call,@NonNull Throwable t) {
                            searchDetailsList.clear();
                        }
                    });
                }
                else {
                    searchDetailsList.clear();
                }
            } catch (Exception e) {
                searchDetailsList.clear();
                Log.e("PDF FILE : ",e.toString());
            }
        }
        else {
            Toast.makeText(this, "No Data found to store in file...", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveToPDF() {
        if (searchDetailsList.size()!=0) {
            searchedBy = spinnerBY.getSelectedItem().toString();
            PDFWriter writer = new PDFWriter(this);
            try {
                HashMap<String,Object> hashMap = writer.createManualPDF(searchDetailsList,searchedBy);
                boolean isGenerated = Boolean.parseBoolean(String.valueOf(hashMap.get("success")));
                if (isGenerated) {
                    Toast.makeText(this, "SuccessFul", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
                searchDetailsList.clear();
            } catch (Exception e) {
                searchDetailsList.clear();
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "No Data found to store in file...", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchByGender(String gender) {
        searchDetailsList.clear();
        progressDialog.show();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("gender",gender);

        Call<AdvancedSearch> advancedSearchCall = apiInterface.ADVANCED_SEARCH_CALL(searchByGenderCall,hashMap);
        advancedSearchCall.enqueue(new Callback<AdvancedSearch>() {
            @Override
            public void onResponse(@NonNull Call<AdvancedSearch> call, @NonNull Response<AdvancedSearch> response) {
                if (response.isSuccessful()) {
                    AdvancedSearch advancedSearch = response.body();
                    if (advancedSearch!=null) {
                        if (!advancedSearch.isError()) {
                            if (advancedSearch.getDetails()!=null && advancedSearch.getDetails().size() != 0) {
                                progressDialog.dismiss();
                                searchDetailsList = advancedSearch.getDetails();
                                AdvancedSearchAdapter adapter = new AdvancedSearchAdapter(AdvancedSearchActivity.this, searchDetailsList);
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                            else {
                                recyclerView.setAdapter(null);
                                progressDialog.dismiss();
                                Toast.makeText(AdvancedSearchActivity.this, "No Data Found for provided details", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(AdvancedSearchActivity.this, advancedSearch.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(AdvancedSearchActivity.this, "Something Went Wrong. Please try again...!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AdvancedSearchActivity.this, "Server Error.. Please try again...!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdvancedSearch> call, @NonNull Throwable t) {
                Toast.makeText(AdvancedSearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void searchByDate(String fromDate, String toDate) {
        searchDetailsList.clear();
        progressDialog.show();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("date1",fromDate);
        hashMap.put("date2",toDate);

        Call<AdvancedSearch> ageCall = apiInterface.ADVANCED_SEARCH_CALL(searchByDateCall,hashMap);
        ageCall.enqueue(new Callback<AdvancedSearch>() {
            @Override
            public void onResponse(@NonNull Call<AdvancedSearch> call, @NonNull Response<AdvancedSearch> response) {
                if (response.isSuccessful()) {
                    AdvancedSearch advancedSearch = response.body();
                    if (advancedSearch!=null) {
                        if (!advancedSearch.isError()) {
                            if (advancedSearch.getDetails()!=null && advancedSearch.getDetails().size() != 0) {
                                progressDialog.dismiss();
                                searchDetailsList = advancedSearch.getDetails();
                                AdvancedSearchAdapter adapter = new AdvancedSearchAdapter(AdvancedSearchActivity.this, searchDetailsList);
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                            else {
                                recyclerView.setAdapter(null);
                                progressDialog.dismiss();
                                Toast.makeText(AdvancedSearchActivity.this, "No Data Found for provided details", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(AdvancedSearchActivity.this, advancedSearch.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(AdvancedSearchActivity.this, "Something Went Wrong. Please try again...!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AdvancedSearchActivity.this, "Server Error.. Please try again...!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdvancedSearch> call, @NonNull Throwable t) {
                Toast.makeText(AdvancedSearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void searchByCenter(String centerType, String center) {
        searchDetailsList.clear();
        progressDialog.show();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("centertype",centerType);
        hashMap.put("centername",center);

        Call<AdvancedSearch> centerCall = apiInterface.ADVANCED_SEARCH_CALL(searchByCenterCall,hashMap);
        centerCall.enqueue(new Callback<AdvancedSearch>() {
            @Override
            public void onResponse(@NonNull Call<AdvancedSearch> call, @NonNull Response<AdvancedSearch> response) {
                if (response.isSuccessful()) {
                    AdvancedSearch advancedSearch = response.body();
                    if (advancedSearch!=null) {
                        if (!advancedSearch.isError()) {
                            if (advancedSearch.getDetails()!=null && advancedSearch.getDetails().size() != 0) {
                                progressDialog.dismiss();
                                searchDetailsList = advancedSearch.getDetails();
                                AdvancedSearchAdapter adapter = new AdvancedSearchAdapter(AdvancedSearchActivity.this, searchDetailsList);
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                            else {
                                recyclerView.setAdapter(null);
                                progressDialog.dismiss();
                                Toast.makeText(AdvancedSearchActivity.this, "No Data Found for provided details", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(AdvancedSearchActivity.this, advancedSearch.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(AdvancedSearchActivity.this, "Something Went Wrong. Please try again...!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AdvancedSearchActivity.this, "Server Error.. Please try again...!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdvancedSearch> call, @NonNull Throwable t) {
                Toast.makeText(AdvancedSearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void searchByAge(String fromAge, String toAge) {
        searchDetailsList.clear();
        progressDialog.show();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("age1",fromAge);
        hashMap.put("age2",toAge);

        Call<AdvancedSearch> ageCall = apiInterface.ADVANCED_SEARCH_CALL(searchByAgeCall,hashMap);
        ageCall.enqueue(new Callback<AdvancedSearch>() {
            @Override
            public void onResponse(@NonNull Call<AdvancedSearch> call, @NonNull Response<AdvancedSearch> response) {
                if (response.isSuccessful()) {
                    AdvancedSearch advancedSearch = response.body();
                    if (advancedSearch!=null) {
                        if (!advancedSearch.isError()) {
                            if (advancedSearch.getDetails()!=null && advancedSearch.getDetails().size() != 0) {
                                progressDialog.dismiss();
                                searchDetailsList = advancedSearch.getDetails();
                                AdvancedSearchAdapter adapter = new AdvancedSearchAdapter(AdvancedSearchActivity.this, searchDetailsList);
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                            else {
                                recyclerView.setAdapter(null);
                                progressDialog.dismiss();
                                Toast.makeText(AdvancedSearchActivity.this, "No Data Found for provided details", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(AdvancedSearchActivity.this, advancedSearch.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(AdvancedSearchActivity.this, "Something Went Wrong. Please try again...!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AdvancedSearchActivity.this, "Server Error.. Please try again...!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdvancedSearch> call, @NonNull Throwable t) {
                Toast.makeText(AdvancedSearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getQuarantineCenters() {

        progressDialog.show();
        name.clear();

        String centersCall = "getCenterType";
        Call<GetQuarantineCenters> quarantineCenterCall = apiInterface.QUARANTINE_CENTER_TYPES_CALL(centersCall);

        quarantineCenterCall.enqueue(new Callback<GetQuarantineCenters>() {
            @Override
            public void onResponse(@NonNull Call<GetQuarantineCenters> call, @NonNull Response<GetQuarantineCenters> response) {
                if (response.isSuccessful()) {
                    GetQuarantineCenters quarantineCenter = response.body();

                    if (quarantineCenter != null) {

                        if (!quarantineCenter.isError()) {

                            name.add("Select Center");

                            for (int i = 0; i < quarantineCenter.getCenters().size(); i++) {
                                name.add(quarantineCenter.getCenters().get(i).getName());
                            }

                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(AdvancedSearchActivity.this, "API : "+quarantineCenter.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(AdvancedSearchActivity.this, "Something Went Wrong, Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AdvancedSearchActivity.this, "Response Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetQuarantineCenters> call, @NonNull Throwable t) {
                progressDialog.dismiss();
//                Toast.makeText(getContext(),"Throwable : "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void clearLayout() {
        recyclerView.setAdapter(null);
        spinnerBY.setSelection(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearLayout();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clearLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        clearLayout();
    }
}