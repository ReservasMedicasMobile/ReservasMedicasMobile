package com.example.reservasmedicasmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.reservasmedicasmobile.R;

import java.util.List;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorariosViewHolder> {

    private List<String> horarios;

    public HorariosAdapter(List<String> horarios) {
        this.horarios = horarios;
    }

    @Override
    public HorariosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
        return new HorariosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorariosViewHolder holder, int position) {
        String horario = horarios.get(position);
        holder.horarioTextView.setText(horario);
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    public void actualizarHorarios(List<String> nuevosHorarios) {
        horarios.clear();
        horarios.addAll(nuevosHorarios);
        notifyDataSetChanged();
    }

    static class HorariosViewHolder extends RecyclerView.ViewHolder {
        TextView horarioTextView;

        HorariosViewHolder(View itemView) {
            super(itemView);
            horarioTextView = itemView.findViewById(R.id.text_view_horario);
        }
    }
}


