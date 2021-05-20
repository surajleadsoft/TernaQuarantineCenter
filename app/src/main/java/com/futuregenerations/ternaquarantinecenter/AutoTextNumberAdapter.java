package com.futuregenerations.ternaquarantinecenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoTextNumberAdapter extends ArrayAdapter<Patients> {

    private Context context;
    private List<Patients> patientsDataList;
    List<Patients> suggestions;

    public AutoTextNumberAdapter(@NonNull Context context, @NonNull List<Patients> dataList) {
        super(context, 0, dataList);

        this.context = context;
        this.patientsDataList = new ArrayList<>(dataList);
        suggestions = new ArrayList<>(dataList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.textview_drop_down_item,parent,false);
        TextView textViewName = convertView.findViewById(R.id.setName);
        TextView textViewNumber = convertView.findViewById(R.id.setNumber);

        Patients data = suggestions.get(position);

        if (data!=null) {
            textViewName.setText(data.getName());
            textViewNumber.setText(data.getContactno());
        }

        return convertView;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            suggestions.clear();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(patientsDataList);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Patients data : patientsDataList) {
                    if (data.getContactno().toLowerCase().contains(filterPattern)) {
                        suggestions.add(data);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Patients) resultValue).getContactno();
        }
    };
}
