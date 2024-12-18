package com.example.a1app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
    private Uri selectedSoundUri; // Guardar el sonido seleccionado
    private DatabaseHelper dbHelper; // Base de datos
    private static final String PREFS_NAME = "AlarmPrefs";
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        editTextActivityName = findViewById(R.id.editTextActivityName);
        timePicker = findViewById(R.id.timePicker);
        buttonSetAlarm = findViewById(R.id.buttonSetAlarm);
        buttonSelectSound = findViewById(R.id.buttonSelectSound);

        dbHelper = new DatabaseHelper(this); // Inicializar la base de datos

        // Inicializar seleccionador de sonidos
        ActivityResultLauncher<Intent> selectSoundLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedSoundUri = result.getData().getData();

                        // Tomar persistencia del URI
                        getContentResolver().takePersistableUriPermission(
                                selectedSoundUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        saveSoundUri(selectedSoundUri);
                        Toast.makeText(this, "Sonido seleccionado: " + selectedSoundUri.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

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

        // Verificar y solicitar permisos
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El permiso es necesario para acceder al archivo de sonido", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setAlarm(String activityName, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Verificar el permiso para alarmas exactas (Android 12 o superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Debes habilitar el permiso para alarmas exactas en la configuración.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("activityName", activityName);

        if (selectedSoundUri != null) {
            intent.putExtra("soundUri", selectedSoundUri.toString());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        try {
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                String formattedTime = String.format("%02d:%02d", hour, minute);

                // Guardar la alarma en la base de datos
                boolean isInserted = dbHelper.insertAlarm(formattedTime, activityName);
                if (isInserted) {
                    Toast.makeText(this, "Alarma guardada en la base de datos para " + formattedTime, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al guardar la alarma en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "No se pudo programar la alarma exacta. Verifica los permisos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void saveSoundUri(Uri uri) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("customSound", uri.toString());
        editor.apply();
    }
}
