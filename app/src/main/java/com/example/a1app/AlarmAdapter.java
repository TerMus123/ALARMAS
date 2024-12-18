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

    interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    private ArrayList<Alarm> alarmList;
    private OnDeleteClickListener deleteClickListener;

    public AlarmAdapter(ArrayList<Alarm> alarmList, OnDeleteClickListener deleteClickListener) {
        this.alarmList = alarmList;
        this.deleteClickListener = deleteClickListener;
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

        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, messageTextView;
        Button deleteButton;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
