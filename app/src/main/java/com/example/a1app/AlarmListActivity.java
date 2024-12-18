package com.example.a1app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private ArrayList<Alarm> alarmList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        recyclerView = findViewById(R.id.recyclerViewAlarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        loadAlarms();

        adapter = new AlarmAdapter(alarmList,
                position -> {
                    // Eliminar alarma
                    int alarmId = alarmList.get(position).getId();
                    dbHelper.deleteAlarm(alarmId);
                    alarmList.remove(position);
                    adapter.notifyItemRemoved(position);
                },
                position -> {
                    // Editar alarma
                    Alarm alarm = alarmList.get(position);
                    Intent intent = new Intent(AlarmListActivity.this, AlarmActivity.class);
                    intent.putExtra("alarmId", alarm.getId());
                    intent.putExtra("alarmTime", alarm.getTime());
                    intent.putExtra("alarmMessage", alarm.getMessage());
                    startActivity(intent);
                }
        );

        recyclerView.setAdapter(adapter);
    }

    private void loadAlarms() {
        alarmList = dbHelper.getAllAlarms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlarms();
        adapter.notifyDataSetChanged();
    }
}
