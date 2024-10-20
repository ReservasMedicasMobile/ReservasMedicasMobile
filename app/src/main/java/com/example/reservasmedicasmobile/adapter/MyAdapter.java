package com.example.reservasmedicasmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reservasmedicasmobile.ActualizarEspcialidadFragment;
import com.example.reservasmedicasmobile.R;
import com.example.reservasmedicasmobile.modelo.DataModel2;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private List<DataModel2> dataList;
    private ActualizarEspcialidadFragment fragment;


    public MyAdapter(List<DataModel2> dataList, ActualizarEspcialidadFragment fragment) {
        this.dataList = dataList;
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataModel2 data = dataList.get(position);
        holder.nameTextView.setText(data.getEspecialidad());



        holder.updateButton.setOnClickListener(v -> {
            String newName = holder.inputEditText.getText().toString();
            String newDescription = holder.inputEditTextDescipcion.getText().toString();

            if (!newName.isEmpty() || !newDescription.isEmpty()) {
                fragment.updateData(data.getId(), newName, newDescription);


                holder.inputEditText.setText("");
                holder.inputEditTextDescipcion.setText("");

            }

        });

        holder.deleteButton.setOnClickListener(v -> {
            fragment.deleteData(data.getId());
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public Object descripcionTextView;
        TextView nameTextView;
        TextView desdescripcionTextView;
        Button updateButton;
        Button deleteButton;
        EditText inputEditText;
        EditText inputEditTextDescipcion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            inputEditText = itemView.findViewById(R.id.inputEditText);
            inputEditTextDescipcion = itemView.findViewById(R.id.inputEditTextDescipcion);
        }
    }

}
