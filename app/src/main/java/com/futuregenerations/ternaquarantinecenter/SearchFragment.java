package com.futuregenerations.ternaquarantinecenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SearchFragment extends Fragment {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(URL.BASE_URL)
            .build();

    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    MaterialAutoCompleteTextView editTextContact, editTextAdhaarCard;

    ListView listView;

    Context context;
    ProgressDialog progressDialog;

    RelativeLayout mainLayout;

    List<Patients> patientsList;

    public SearchFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_search,container,false);

        findViews(rootView);

        if (isConnected()) {
            getPatientsData();
            onClickList();
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

    private void onClickList() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,RemarkHistoryActivity.class);
                intent.putExtra("name",patientsList.get(position).getName());
                intent.putExtra("adhar",patientsList.get(position).getAdhar());
                intent.putExtra("age",patientsList.get(position).getAge());
                startActivity(intent);
            }
        });
    }

    private void getPatientsData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
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
                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<GetPatientsData> call,@NonNull Throwable t) {
                progressDialog.dismiss();
//                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEditTextAdapters(final GetPatientsData patientsData) {

        AutoTextNumberAdapter numberAdapter = new AutoTextNumberAdapter(context,patientsData.getPatients());
        editTextContact.setAdapter(numberAdapter);

        AutoTextAdharAdapter adharAdapter = new AutoTextAdharAdapter(context,patientsData.getPatients());
        editTextAdhaarCard.setAdapter(adharAdapter);

        editTextContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                editTextAdhaarCard.setText(patientsData.getPatients().get(i).getAdhar());
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),0);

                patientsList.clear();
                patientsList.add(patientsData.getPatients().get(i));

                CustomListAdapter adapter = new CustomListAdapter(context,patientsList);
                listView.setAdapter(adapter);
            }
        });

        editTextAdhaarCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String number = patientsData.getPatients().get(i).getContactno();
                editTextContact.setText(number);
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),0);

                patientsList.clear();
                patientsList.add(patientsData.getPatients().get(i));

                CustomListAdapter adapter = new CustomListAdapter(context,patientsList);
                listView.setAdapter(adapter);
            }
        });
    }

    private void findViews(View rootView) {
        editTextContact = rootView.findViewById(R.id.patient_number);
        editTextAdhaarCard = rootView.findViewById(R.id.patient_adhaar);

        mainLayout = rootView.findViewById(R.id.search_layout);
        listView = rootView.findViewById(R.id.list_patients);

        patientsList = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void clearLayout() {

        editTextContact.setText("");
        editTextContact.clearFocus();

        editTextAdhaarCard.setText("");
        editTextAdhaarCard.clearFocus();

        patientsList.clear();

        listView.clearChoices();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(context,AboutActivity.class));
        }
        else if (item.getItemId() == R.id.menu_advanced_search) {
            startActivity(new Intent(context,AdvancedSearchActivity.class));
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        clearLayout();
    }

    @Override
    public void onStart() {
        super.onStart();
        clearLayout();
    }

    @Override
    public void onStop() {
        super.onStop();
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
