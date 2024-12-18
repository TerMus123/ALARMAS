package com.example.a1app;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private ArrayList<Alarm> alarmList;
    private DatabaseHelper dbHelper;

    private ActivityResultLauncher<Intent> editAlarmLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        recyclerView = findViewById(R.id.recyclerViewAlarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        alarmList = dbHelper.getAllAlarms();

        adapter = new AlarmAdapter(alarmList,
                position -> {
                    int alarmId = alarmList.get(position).getId();
                    dbHelper.deleteAlarm(alarmId);
                    alarmList.remove(position);
                    adapter.notifyItemRemoved(position);
                },
                position -> {
                    Alarm alarm = alarmList.get(position);
                    Intent intent = new Intent(AlarmListActivity.this, AlarmActivity.class);
                    intent.putExtra("alarmId", alarm.getId());
                    intent.putExtra("alarmTime", alarm.getTime());
                    intent.putExtra("alarmMessage", alarm.getMessage());
                    intent.putExtra("position", position); // Guardar posición
                    editAlarmLauncher.launch(intent);
                });

        recyclerView.setAdapter(adapter);

        editAlarmLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int position = result.getData().getIntExtra("position", -1);
                        String updatedTime = result.getData().getStringExtra("alarmTime");
                        String updatedMessage = result.getData().getStringExtra("alarmMessage");

                        if (position != -1) {
                            Alarm updatedAlarm = new Alarm(alarmList.get(position).getId(), updatedTime, updatedMessage);
                            adapter.updateAlarm(position, updatedAlarm);
                        }
                    }
                });
    }
}
