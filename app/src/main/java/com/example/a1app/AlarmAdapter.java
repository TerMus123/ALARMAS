package com.example.a1app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    private ArrayList<Alarm> alarmList;
    private OnDeleteClickListener deleteClickListener;
    private OnEditClickListener editClickListener;

    public AlarmAdapter(ArrayList<Alarm> alarmList, OnDeleteClickListener deleteClickListener, OnEditClickListener editClickListener) {
        this.alarmList = alarmList;
        this.deleteClickListener = deleteClickListener;
        this.editClickListener = editClickListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.timeTextView.setText("Hora: " + alarm.getTime());
        holder.messageTextView.setText("Mensaje: " + alarm.getMessage());

        holder.editButton.setOnClickListener(v -> editClickListener.onEditClick(position));
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void updateAlarm(int position, Alarm updatedAlarm) {
        alarmList.set(position, updatedAlarm); // Actualiza la alarma en la lista
        notifyItemChanged(position); // Notifica el cambio en esa posición
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, messageTextView;
        Button editButton, deleteButton;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
