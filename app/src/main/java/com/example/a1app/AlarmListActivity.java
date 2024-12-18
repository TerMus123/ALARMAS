package com.example.a1app;

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
        alarmList = dbHelper.getAllAlarms(); // Obtén todas las alarmas de la base de datos

        adapter = new AlarmAdapter(alarmList, position -> {
            int alarmId = alarmList.get(position).getId();
            dbHelper.deleteAlarm(alarmId);
            alarmList.remove(position);
            adapter.notifyItemRemoved(position);
        });

        recyclerView.setAdapter(adapter);
    }
}
