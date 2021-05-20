package com.futuregenerations.ternaquarantinecenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

class CustomListAdapter extends ArrayAdapter<Patients> {

    private Context context;
    private List<Patients> patientsList;
    public CustomListAdapter(@NonNull Context context, @NonNull List<Patients> patientsList) {
        super(context, R.layout.custom_patients_list, patientsList);

        this.context = context;
        this.patientsList = patientsList;
    }

    @Override
    public int getCount() {
        return patientsList.size();
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String text = "Adhar Card : ";

        convertView = LayoutInflater.from(context).inflate(R.layout.custom_patients_list,parent,false);

        TextView textViewId = convertView.findViewById(R.id.text_patient_id);
        TextView textViewName = convertView.findViewById(R.id.text_patient_name);
        TextView textViewAdhar = convertView.findViewById(R.id.text_patient_adhar);

        textViewId.setText(patientsList.get(position).getId());
        textViewName.setText(patientsList.get(position).getName());
        String adharText = text+patientsList.get(position).getAdhar();
        textViewAdhar.setText(adharText);

        return convertView;
    }
}
