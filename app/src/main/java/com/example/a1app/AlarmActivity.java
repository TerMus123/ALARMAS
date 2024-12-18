package com.example.a1app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    private EditText editTextActivityName;
    private TimePicker timePicker;
    private Button buttonSetAlarm, buttonSelectSound;
    private Uri selectedSoundUri; // Guarda el sonido seleccionado
    private DatabaseHelper dbHelper; // Base de datos

    private int alarmId = -1; // ID de la alarma (para editar)
    private int position = -1; // Posición en la lista de AlarmListActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Inicializar vistas
        editTextActivityName = findViewById(R.id.editTextActivityName);
        timePicker = findViewById(R.id.timePicker);
        buttonSetAlarm = findViewById(R.id.buttonSetAlarm);
        buttonSelectSound = findViewById(R.id.buttonSelectSound);

        dbHelper = new DatabaseHelper(this); // Base de datos

        // Verificar si estamos editando una alarma
        if (getIntent().hasExtra("alarmId")) {
            alarmId = getIntent().getIntExtra("alarmId", -1);
            position = getIntent().getIntExtra("position", -1);
            String time = getIntent().getStringExtra("alarmTime");
            String message = getIntent().getStringExtra("alarmMessage");

            editTextActivityName.setText(message);
            int hour = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }

        // Botón para seleccionar el sonido
        buttonSelectSound.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            selectSoundLauncher.launch(intent);
        });

        // Botón para configurar la alarma
        buttonSetAlarm.setOnClickListener(v -> {
            String activityName = editTextActivityName.getText().toString().trim();
            if (activityName.isEmpty()) {
                Toast.makeText(this, "Introduce el nombre de la actividad", Toast.LENGTH_SHORT).show();
                return;
            }

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            setAlarm(activityName, hour, minute);
        });
    }

    private void setAlarm(String activityName, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("activityName", activityName);

        if (selectedSoundUri != null) {
            intent.putExtra("soundUri", selectedSoundUri.toString());
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId == -1 ? 0 : alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        String formattedTime = String.format("%02d:%02d", hour, minute);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            if (alarmId != -1) {
                // Actualizar alarma existente
                dbHelper.updateAlarm(alarmId, formattedTime, activityName);
                Toast.makeText(this, "Alarma actualizada", Toast.LENGTH_SHORT).show();
            } else {
                // Insertar nueva alarma
                dbHelper.insertAlarm(formattedTime, activityName);
                Toast.makeText(this, "Alarma guardada", Toast.LENGTH_SHORT).show();
            }

            // Devolver resultado a AlarmListActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("alarmTime", formattedTime);
            resultIntent.putExtra("alarmMessage", activityName);
            resultIntent.putExtra("position", position);
            setResult(RESULT_OK, resultIntent);

            finish(); // Cerrar la actividad
        }
    }

    // Launcher para seleccionar sonido
    private final ActivityResultLauncher<Intent> selectSoundLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedSoundUri = result.getData().getData();
                    Toast.makeText(this, "Sonido seleccionado correctamente", Toast.LENGTH_SHORT).show();
                }
            }
    );
}
