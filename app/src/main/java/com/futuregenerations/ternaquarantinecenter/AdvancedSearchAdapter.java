package com.futuregenerations.ternaquarantinecenter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class AdvancedSearchAdapter extends RecyclerView.Adapter<AdvancedSearchAdapter.ViewHolder> {

    private Context context;
    private List<SearchDetails> searchDetails;

    public AdvancedSearchAdapter(Context context, List<SearchDetails> searchDetails) {
        this.context = context;
        this.searchDetails = searchDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_patients_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String age = "Age : " + searchDetails.get(position).getAge();
        holder.textViewAge.setText(age);
        final String name = searchDetails.get(position).getName();
        holder.textViewName.setText(name);
        final String adhar = "Adhar Card : " + searchDetails.get(position).getAdhar();
        holder.textViewAdhar.setText(adhar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,RemarkHistoryActivity.class);
                intent.putExtra("age",searchDetails.get(position).getAge());
                intent.putExtra("name",searchDetails.get(position).getName());
                intent.putExtra("adhar",searchDetails.get(position).getAdhar());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAge, textViewName, textViewAdhar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAge = itemView.findViewById(R.id.text_patient_id);
            textViewName = itemView.findViewById(R.id.text_patient_name);
            textViewAdhar = itemView.findViewById(R.id.text_patient_adhar);
        }
    }
}
