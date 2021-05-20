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

class CustomRemarkAdapter extends ArrayAdapter<PatientsRemarks> {

    private Context context;
    private List<PatientsRemarks> patientsRemarksList;
    public CustomRemarkAdapter(@NonNull Context context, @NonNull List<PatientsRemarks> patientsRemarksList) {
        super(context, R.layout.custom_remark_list, patientsRemarksList);

        this.context = context;
        this.patientsRemarksList = patientsRemarksList;
    }

    @Override
    public int getCount() {
        return patientsRemarksList.size();
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.custom_remark_list,parent,false);

        TextView textViewDate = convertView.findViewById(R.id.text_remark_date);
        TextView textViewRemark = convertView.findViewById(R.id.text_remark);
        TextView textViewCenter = convertView.findViewById(R.id.text_remark_center);

        textViewDate.setText(patientsRemarksList.get(position).getDate());
        textViewRemark.setText(patientsRemarksList.get(position).getRemark());
        textViewCenter.setText(patientsRemarksList.get(position).getCenter());

        return convertView;
    }
}
