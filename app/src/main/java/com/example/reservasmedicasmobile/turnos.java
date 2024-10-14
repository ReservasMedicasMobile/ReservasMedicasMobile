package com.example.reservasmedicasmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reservasmedicasmobile.R;

import java.util.Calendar;

public class turnos extends AppCompatActivity {

    private Spinner specialtySpinner;
    private Spinner professionalSpinner;
    private Button openDatePickerButton;
    private Button openTimePickerButton;
    private Button timeSlotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);

        specialtySpinner = findViewById(R.id.spinner_specialty);
        professionalSpinner = findViewById(R.id.spinner_professional);
        openDatePickerButton = findViewById(R.id.button_open_date_picker);
        openTimePickerButton = findViewById(R.id.button_open_time_picker);
        timeSlotButton = findViewById(R.id.button_time_slot);

        String[] especialidades = { "ESPECIALIDAD MÉDICA", "Cardiología", "Dermatología", "Pediatría", "Ginecología", "Obstetricia", "Urología", "Oftalmología", "Clínica Médica"};
        ArrayAdapter<String> specialtyAdapter = new ArrayAdapter<String>(this, R.layout.sppiner_item_turnos, especialidades);
        specialtySpinner.setAdapter(specialtyAdapter);

        String[] profesionales = {"PROFESIONALES", "Dr. Juan Pedro García", "Dra. María Gómez", "Dr. Carlos Fernández", "Dr. Juan Ignacio Medina", "Dr. Ricardo Cáceres", "Dra. Melina Uhlig", "Dr. Cesar Terán", "Dra. Eugenia Alvarado"};
        ArrayAdapter<String> professionalAdapter = new ArrayAdapter<>(this, R.layout.sppiner_item_turnos, profesionales);
        professionalSpinner.setAdapter(professionalAdapter);

        openDatePickerButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                Toast.makeText(this, "Fecha seleccionada: " + date, Toast.LENGTH_SHORT).show();
            }, year, month, day);

            datePickerDialog.show();
        });

        openTimePickerButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfDay) -> {
                String time = String.format("%02d:%02d", hourOfDay, minuteOfDay);
                Toast.makeText(this, "Hora seleccionada: " + time, Toast.LENGTH_SHORT).show();
            }, hour, minute, true);

            timePickerDialog.show();
        });

        timeSlotButton.setOnClickListener(v -> {
            // Lógica para seleccionar el horario aquí
            Toast.makeText(this, "Selecciona un horario", Toast.LENGTH_SHORT).show();
        });
        // Inicializar y configurar el botón back dentro de onCreate
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Cierra la actividad actual y vuelve a la anterior
        });
    }
}

