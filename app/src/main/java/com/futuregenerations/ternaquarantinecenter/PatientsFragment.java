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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class PatientsFragment extends Fragment {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(URL.BASE_URL)
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    private TextInputEditText editTextName, editTextContact, editTextAdhaarCard, editTextAddress,
            editTextAge, editTextDateAdmitted, editTextDiseases,editTextCustomAddress;

    MaterialAutoCompleteTextView editTextTests, editTextCustomTests, editTextRemark;

    TextInputLayout textInputLayoutTest;

    TextView textViewTestNames;

    RadioButton buttonMale, buttonFemale, buttonYes, buttonNo;

    Spinner spinnerCenterType, spinnerCenter,spinnerCustomAddress;

    String name, contactno, gender , adhar, add, age, date, remark, centerType, center,tests,diseases,testDone,
        createdBy, createdAt,addressArea,addressDistrict, area;
    String apicall = "insertPatient";
    String updateRemarkCall = "updateRemark";
    String addressCall = "getAddressList";
    String remarksCall = "getRemarkList";

    Context context;

    ProgressDialog progressDialog;

    String centerTypeCall = "getCenterType";
    String centerCall = "getCenters";
    String diseasesCall = "getDiseases";

    List<String> centerTypeNames, centerNames;

    List<Tests> testList;
    List<Diseases> diseasesList;
    List<Remarks> remarksList;
    List<Address> addressList;

    List<String> testSelectedList, diseasesSelectedList, addressesList;

    RelativeLayout layoutDialog;

    public PatientsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        context = getContext();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_patients,container,false);

        findViews(rootView);

        centerTypeNames.add("Select Center Type");

        createdBy = UserSharedPrefManager.getInstance(context).getData().getName();

        editTextDateAdmitted.setText(getDate());

        if (isConnected()) {
            getQuarantineCentersType();
            getCenters();
            getTestLists();
            getDiseasesList();
            getAddressList();
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

        buttonMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonMale.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.radio_background,null));
                }
                else {
                    buttonMale.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.radio_no_background,null));
                }
            }
        });

        buttonFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonFemale.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.radio_background,null));
                }
                else {
                    buttonFemale.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.radio_no_background,null));
                }
            }
        });

        buttonYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    textInputLayoutTest.setVisibility(View.VISIBLE);
                }
                else {
                    textInputLayoutTest.setVisibility(View.GONE);
                }
            }
        });

        editTextTests.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDialogForTest(rootView);
                }
            }
        });

        editTextTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForTest(rootView);
            }
        });

        editTextDiseases.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDialogForDiseases(rootView);
                }
            }
        });

        editTextDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForDiseases(rootView);
            }
        });

        editTextAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForAddress(rootView);
            }
        });

        editTextAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDialogForAddress(rootView);
                }
            }
        });

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

    private void getAddressList() {
        addressList.clear();
        addressesList.clear();
        progressDialog.show();
        addressesList.add("Select Address");
        Call<AddressDetails> addressDetailsCall = apiInterface.ADDRESS_DETAILS_CALL(addressCall);
        addressDetailsCall.enqueue(new Callback<AddressDetails>() {
            @Override
            public void onResponse(@NonNull Call<AddressDetails> call,@NonNull Response<AddressDetails> response) {
                if (response.isSuccessful()) {
                    AddressDetails details = response.body();
                    if (details!=null && !details.isError()) {
                        if (details.getAddresses()!=null) {
                            addressList = details.getAddresses();
                            for (int count = 0; count < addressList.size(); count++) {
                                addressesList.add(addressList.get(count).getName());
                            }
                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AddressDetails> call,@NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void showDialogForAddress(View rootView) {
        if (addressesList!=null) {
            ViewGroup viewGroup = rootView.findViewById(android.R.id.content);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.custom_dialog_for_address, viewGroup);

            layoutDialog = customView.findViewById(R.id.dialog_relative_layout);
            editTextCustomAddress = customView.findViewById(R.id.dialog_address_line);
            spinnerCustomAddress = customView.findViewById(R.id.spinner_address_area);
            SpinnerAdapter adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,addressesList);
            spinnerCustomAddress.setAdapter(adapter);
            builder.setView(customView);
            builder.setTitle("Select Address");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!TextUtils.isEmpty(editTextCustomAddress.getText())) {
                        String address_line = Objects.requireNonNull(editTextCustomAddress.getText()).toString();
                        if (spinnerCustomAddress.getSelectedItemPosition()!=0) {
                            if (spinnerCustomAddress.getCount()!=0) {
                                area = spinnerCustomAddress.getSelectedItem().toString();
                                String address = address_line+", "+area;
                                editTextAddress.setText(address);
                            }
                        }
                        else {
                            Toast.makeText(context, "Please Enter The Address Correctly", Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.cancel();
                    }
                    else {
                        Toast.makeText(context, "Please Enter The Address line 1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            Toast.makeText(context, "No Addresses Available, Please Add an Area First", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogForDiseases(View view) {
        if (testList!=null) {
            ViewGroup viewGroup = view.findViewById(android.R.id.content);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.custom_test_layout,viewGroup);

            layoutDialog = customView.findViewById(R.id.dialog_relative_layout);
            editTextCustomTests = customView.findViewById(R.id.text_custom_test);
            textViewTestNames = customView.findViewById(R.id.text_tests);

            AutoTextDiseasesAdapter adapter = new AutoTextDiseasesAdapter(context,diseasesList);
            editTextCustomTests.setAdapter(adapter);

            editTextCustomTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (diseasesSelectedList.size()==0) {
                        String putText = diseasesList.get(i).getName();
                        textViewTestNames.setText(putText);
                        diseasesSelectedList.add(diseasesList.get(i).getName());
                        editTextCustomTests.setText("");
                    }
                    else if (!diseasesSelectedList.contains(diseasesList.get(i).getName())) {
                        String text = textViewTestNames.getText().toString();
                        String putText = text + ", " + diseasesList.get(i).getName();
                        textViewTestNames.setText(putText);
                        diseasesSelectedList.add(diseasesList.get(i).getName());
                        editTextCustomTests.setText("");
                    }
                    else {
                        Toast.makeText(context, "Already Selected", Toast.LENGTH_SHORT).show();
                        editTextCustomTests.setText("");
                    }
                }
            });

            builder.setView(customView);
            builder.setTitle("Select Diseases");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    diseases = textViewTestNames.getText().toString();
                    editTextDiseases.setText(diseases);
                    editTextDiseases.clearFocus();
                    diseasesSelectedList.clear();
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void getDiseasesList() {
        progressDialog.show();
        Call<GetDiseasesData> diseasesDataCall = apiInterface.DISEASES_DATA_CALL(diseasesCall);

        diseasesDataCall.enqueue(new Callback<GetDiseasesData>() {
            @Override
            public void onResponse(@NonNull Call<GetDiseasesData> call,@NonNull Response<GetDiseasesData> response) {
                if (response.isSuccessful()) {
                    GetDiseasesData diseasesData = response.body();
                    if (diseasesData!=null) {
                        if (!diseasesData.isError()) {
                            diseasesList.addAll(diseasesData.getDiseases());
                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(context, diseasesData.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Please Try Again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Please Try Again...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetDiseasesData> call,@NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void showDialogForTest(View view) {
        if (testList!=null) {
            ViewGroup viewGroup = view.findViewById(android.R.id.content);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.custom_test_layout,viewGroup);
            layoutDialog = customView.findViewById(R.id.dialog_relative_layout);

            editTextCustomTests = customView.findViewById(R.id.text_custom_test);
            textViewTestNames = customView.findViewById(R.id.text_tests);

            AutoTextTestAdapter adapter = new AutoTextTestAdapter(context,testList);
            editTextCustomTests.setAdapter(adapter);

            editTextCustomTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (testSelectedList.size()==0) {
                        String putText = testList.get(i).getName();
                        textViewTestNames.setText(putText);
                        testSelectedList.add(testList.get(i).getName());
                        editTextCustomTests.setText("");
                    }
                    else if (!testSelectedList.contains(testList.get(i).getName())) {
                        String text = textViewTestNames.getText().toString();
                        String putText = text + ", " + testList.get(i).getName();
                        textViewTestNames.setText(putText);
                        testSelectedList.add(testList.get(i).getName());
                        editTextCustomTests.setText("");
                    }
                    else {
                        Toast.makeText(context, "Already Selected", Toast.LENGTH_SHORT).show();
                        editTextCustomTests.setText("");
                    }
                }
            });

            builder.setView(customView);
            builder.setTitle("Select Tests Done");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    tests = textViewTestNames.getText().toString();
                    editTextTests.setText(tests);
                    editTextTests.clearFocus();
                    testSelectedList.clear();
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void getTestLists() {
        String apicall = "getTestType";
        testList.clear();
        progressDialog.show();
        Call<GetTestData> centerTypesCall = apiInterface.TEST_DATA_CALL(apicall);

        centerTypesCall.enqueue(new Callback<GetTestData>() {
            @Override
            public void onResponse(@NonNull Call<GetTestData> call, @NonNull Response<GetTestData> response) {
                if (response.isSuccessful()) {
                    GetTestData testsData = response.body();
                    if (testsData!=null) {
                        if (!testsData.isError()) {

                            testList.addAll(testsData.getTests());
                            progressDialog.dismiss();

                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(context, testsData.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Please Try Again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please Try Again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetTestData> call, @NonNull Throwable t) {
            }
        });
    }

    private void getQuarantineCentersType() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<GetQuarantineCenters> quarantineCenterCall = apiInterface.QUARANTINE_CENTER_TYPES_CALL(centerTypeCall);

        quarantineCenterCall.enqueue(new Callback<GetQuarantineCenters>() {
            @Override
            public void onResponse(@NonNull Call<GetQuarantineCenters> call, @NonNull Response<GetQuarantineCenters> response) {
                if (response.isSuccessful()) {
                    GetQuarantineCenters quarantineCenter = response.body();

                    if (quarantineCenter != null) {

                        if (!quarantineCenter.isError()) {

                            for (int i = 0; i < quarantineCenter.getCenters().size(); i++) {
                                centerTypeNames.add(quarantineCenter.getCenters().get(i).getName());
                            }

                            SpinnerAdapter adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,centerTypeNames);
                            spinnerCenterType.setAdapter(adapter);
                            for (int count = 0; count < centerTypeNames.size(); count++) {
                                if (centerType.equals(centerTypeNames.get(count))) {
                                    spinnerCenterType.setSelection(count);
                                    break;
                                }
                            }

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
            }
        });

    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void findViews(View rootView) {
        editTextDateAdmitted = rootView.findViewById(R.id.patient_date);
        editTextName = rootView.findViewById(R.id.patient_name);
        editTextContact = rootView.findViewById(R.id.patient_number);
        editTextAdhaarCard = rootView.findViewById(R.id.patient_adhaar);
        editTextAddress = rootView.findViewById(R.id.patient_address);
        editTextAge = rootView.findViewById(R.id.patient_age);
        editTextRemark = rootView.findViewById(R.id.patient_remark);
        editTextTests = rootView.findViewById(R.id.patient_test);
        editTextDiseases = rootView.findViewById(R.id.patient_diseases);

        textInputLayoutTest = rootView.findViewById(R.id.patient_test_layout);

        buttonMale = rootView.findViewById(R.id.rb_male);
        buttonFemale = rootView.findViewById(R.id.rb_female);

        buttonYes = rootView.findViewById(R.id.rb_yes);
        buttonNo = rootView.findViewById(R.id.rb_no);

        centerNames = new ArrayList<>();
        centerTypeNames = new ArrayList<>();
        testList = new ArrayList<>();
        testSelectedList = new ArrayList<>();
        diseasesSelectedList = new ArrayList<>();
        diseasesList = new ArrayList<>();
        remarksList = new ArrayList<>();
        addressList = new ArrayList<>();
        addressesList = new ArrayList<>();

        spinnerCenterType = rootView.findViewById(R.id.spinner_centerType);
        spinnerCenter = rootView.findViewById(R.id.spinner_center);
        spinnerCenterType.setEnabled(false);
        spinnerCenter.setEnabled(false);

        centerType =  UserSharedPrefManager.getInstance(context).getData().getCenterType();
        center = UserSharedPrefManager.getInstance(context).getData().getCenter();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_patient,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.save_details:
                getGender();
                checkIfEmpty();
                break;

            case R.id.menu_about:
                startActivity(new Intent(context,AboutActivity.class));
                break;

            case R.id.menu_logout:
                UserSharedPrefManager.getInstance(context).clearData();
                Objects.requireNonNull(getActivity()).finish();
        }

        return true;
    }

    private void getGender() {
        if (buttonMale.isChecked()) {
            gender = buttonMale.getText().toString();
        }
        else if (buttonFemale.isChecked()) {
            gender = buttonFemale.getText().toString();
        }
        else {
            gender = null;
        }
    }

    private void saveDataToVariables() {
        name = Objects.requireNonNull(editTextName.getText()).toString();
        contactno = Objects.requireNonNull(editTextContact.getText()).toString();
        adhar = Objects.requireNonNull(editTextAdhaarCard.getText()).toString();
        add = Objects.requireNonNull(editTextAddress.getText()).toString();
        age = Objects.requireNonNull(editTextAge.getText()).toString();
        date = Objects.requireNonNull(editTextDateAdmitted.getText()).toString();
        remark = Objects.requireNonNull(editTextRemark.getText()).toString();
        tests = editTextTests.getText().toString();
        String[] location = area.split(",");
        addressArea = location[0];
        addressDistrict = location[1];

        centerType = spinnerCenterType.getSelectedItem().toString();
        center = spinnerCenter.getSelectedItem().toString();

        diseases = Objects.requireNonNull(editTextDiseases.getText()).toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a",Locale.getDefault());
        createdAt = timeFormat.format(calendar.getTime());

        if (buttonYes.isChecked()) {
            testDone = "Yes";
        }
        else if (buttonNo.isChecked()) {
            testDone = "No";
        }
        else {
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle(context.getResources().getString(R.string.app_name))
                    .setMessage("Please Select if tests done or not.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
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
        else if (Objects.requireNonNull(editTextContact.getText()).toString().length()<10) {
            editTextContact.setError("Please Enter Correct Contact No.");
            editTextContact.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextAdhaarCard.getText())) {
            editTextAdhaarCard.setError("Please Enter Adhaar Card No.");
            editTextAdhaarCard.requestFocus();
        }
        else if (Objects.requireNonNull(editTextAdhaarCard.getText()).toString().length()<12) {
            editTextAdhaarCard.setError("Please Enter Correct Adhaar Card No.");
            editTextAdhaarCard.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextAddress.getText())) {
            editTextAddress.setError("Please Enter Address");
            editTextAddress.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextAge.getText())) {
            editTextAge.setError("Please Enter Age");
            editTextAge.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextDateAdmitted.getText())) {
            editTextDateAdmitted.setError("Please Enter Date");
            editTextDateAdmitted.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextRemark.getText())) {
            editTextRemark.setError("Please Enter Remark");
            editTextRemark.requestFocus();
        }
        else if (TextUtils.isEmpty(gender)) {
            Toast.makeText(getContext(), "Please Mention Gender", Toast.LENGTH_SHORT).show();
        }
        else if (spinnerCenterType.getSelectedItemPosition() == 0) {
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
        else if (!buttonYes.isChecked() && !buttonNo.isChecked()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.app_name)
                    .setMessage("Please Choose Tests Done or Not!")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
        else if (TextUtils.isEmpty(editTextTests.getText()) && buttonYes.isChecked()) {
                editTextTests.setError("Please Enter Tests");
                editTextTests.requestFocus();
        }
        else if (TextUtils.isEmpty(editTextDiseases.getText())) {
            editTextDiseases.setError("Please Enter Diseases");
            editTextDiseases.requestFocus();
        }
        else {
            saveDataToVariables();
        }
    }

    private void storeDataToDatabase() {

        progressDialog.show();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("contactno",contactno);
        hashMap.put("gender",gender);
        hashMap.put("add",add);
        hashMap.put("age",age);
        hashMap.put("adhar",adhar);
        hashMap.put("date",date);
        hashMap.put("remark",remark);
        hashMap.put("center",center);
        hashMap.put("isTest",testDone);
        hashMap.put("TestName",tests);
        hashMap.put("centertype",centerType);
        hashMap.put("other",diseases);
        hashMap.put("CreatedBy",createdBy);
        hashMap.put("CreatedAt",createdAt);
        hashMap.put("area",addressArea);
        hashMap.put("city",addressDistrict);

        Call<GetResponseModel> saveDetailsCall = apiInterface.PATIENT_API_CALL(apicall,hashMap);
        saveDetailsCall.enqueue(new Callback<GetResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GetResponseModel> call,@NonNull Response<GetResponseModel> response) {
                if (response.isSuccessful()) {
                    GetResponseModel responseModel = response.body();
                    if (responseModel != null) {

                        if (!responseModel.isError()) {
                            Toast.makeText(getContext(), responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            clearLayout();
                            storeRemarkToDatabase();
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
//                Toast.makeText(getContext(),"Throwable Message : "+t.getMessage()+"\n"+call.request().toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void storeRemarkToDatabase() {

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("contactno",contactno);
        hashMap.put("gender",gender);
        hashMap.put("add",add);
        hashMap.put("age",age);
        hashMap.put("adhar",adhar);
        hashMap.put("date",date);
        hashMap.put("remark",remark);
        hashMap.put("center",center);
        hashMap.put("centertype",centerType);
        hashMap.put("CreatedBy",createdBy);
        hashMap.put("CreatedAt",createdAt);
        hashMap.put("area",addressArea);
        hashMap.put("city",addressDistrict);

        Call<GetResponseModel> updateRemarkCall = apiInterface.PATIENT_API_CALL(this.updateRemarkCall,hashMap);

        updateRemarkCall.enqueue(new Callback<GetResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GetResponseModel> call,@NonNull Response<GetResponseModel> response) {
                if (response.isSuccessful()) {
                    GetResponseModel responseModel = response.body();
                    if (responseModel!=null) {
                        if (!responseModel.isError()) {
                            clearLayout();
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

    private void getCenters() {
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

                            for (int count = 0; count < centerNames.size(); count++) {
                                if (center.equals(centerNames.get(count))) {
                                    spinnerCenter.setSelection(count);
                                    break;
                                }
                            }

                            progressDialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(context, "Please Try Again...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Objects.requireNonNull(getActivity()).finish();
                    }
                }
                else {
                    Toast.makeText(context, "Please Try Again...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Objects.requireNonNull(getActivity()).finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetQuarantineCenters> call,@NonNull Throwable t) {
//                Toast.makeText(context, "Please Try Again...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Objects.requireNonNull(getActivity()).finish();
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

        editTextAddress.setText("");
        editTextAddress.clearFocus();

        editTextAge.setText("");
        editTextAge.clearFocus();

        editTextDateAdmitted.setText(getDate());
        editTextDateAdmitted.clearFocus();

        editTextRemark.setText("");
        editTextRemark.clearFocus();

        buttonFemale.setChecked(false);
        buttonMale.setChecked(false);

        buttonYes.setChecked(false);
        buttonNo.setChecked(false);

        editTextTests.setText("");
        editTextTests.clearFocus();

        editTextDiseases.setText("");
        editTextDiseases.clearFocus();
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
        clearLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearLayout();
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
