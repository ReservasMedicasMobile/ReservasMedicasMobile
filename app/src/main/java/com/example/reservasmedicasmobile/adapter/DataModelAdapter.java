package com.example.reservasmedicasmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reservasmedicasmobile.modelo.DataModel;

import java.util.List;

public class DataModelAdapter extends RecyclerView.Adapter<DataModelAdapter.DataModelViewHolder> {

    private List<DataModel> especialidadList;

    public DataModelAdapter(List<DataModel> especialidadList){
        this.especialidadList = especialidadList;
    }
    @NonNull
    @Override
    public DataModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new DataModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataModelAdapter.DataModelViewHolder holder, int position) {
        DataModel dataModel = especialidadList.get(position);
        holder.idTextView.setText(dataModel.getId());
        holder.especialidadTextView.setText(dataModel.getEspeciaslidad());
        holder.descipcionTextView.setText(dataModel.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return especialidadList.size();
    }

    static class DataModelViewHolder extends RecyclerView.ViewHolder{
        TextView idTextView;
        TextView especialidadTextView;
        TextView descipcionTextView;

        DataModelViewHolder(View itemView){
            super(itemView);
            idTextView = itemView.findViewById(android.R.id.text1);
            especialidadTextView = itemView.findViewById(android.R.id.text2);

        }
    }
}
