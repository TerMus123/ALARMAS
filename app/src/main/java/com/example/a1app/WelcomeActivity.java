package com.example.a1app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private Button buttonOpenAlarmActivity;
    private Button buttonOpenAlarmListActivity; // Declarar el nuevo botón

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Vincular vistas con sus IDs
        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonOpenAlarmActivity = findViewById(R.id.buttonOpenAlarmActivity);
        buttonOpenAlarmListActivity = findViewById(R.id.buttonOpenAlarmListActivity);

        // Obtener el nombre de usuario
        String username = getIntent().getStringExtra("username");
        textViewWelcome.setText("Bienvenido, a la aplicación to xula de Alejandro " + username + "!");

        // Configurar el clic del botón para AlarmActivity
        buttonOpenAlarmActivity.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, AlarmActivity.class);
            startActivity(intent);
        });

        // Configurar el clic del nuevo botón para AlarmListActivity
        buttonOpenAlarmListActivity.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, AlarmListActivity.class);
            startActivity(intent);
        });
    }
}
