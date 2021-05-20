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

public class AutoTextRemarksAdapter extends ArrayAdapter<Remarks> {

    private Context context;
    private List<Remarks> remarksList;
    List<Remarks> suggestions;

    public AutoTextRemarksAdapter(@NonNull Context context, @NonNull List<Remarks> dataList) {
        super(context, 0, dataList);

        this.context = context;
        this.remarksList = new ArrayList<>(dataList);
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

        convertView = LayoutInflater.from(context).inflate(R.layout.single_line_drop_down_item,parent,false);
        TextView textViewName = convertView.findViewById(R.id.setName);

        Remarks data = suggestions.get(position);

        if (data!=null) {
            textViewName.setText(data.getName());
        }

        return convertView;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            suggestions.clear();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(remarksList);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Remarks data : remarksList) {
                    if (data.getName().toLowerCase().contains(filterPattern)) {
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
            return ((Remarks) resultValue).getName();
        }
    };
}
