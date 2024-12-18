package com.example.a1app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class AlarmSoundService extends Service {
    private MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "AlarmChannel";
    public static final String ACTION_STOP_ALARM = "com.example.a1app.ACTION_STOP_ALARM";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (ACTION_STOP_ALARM.equals(action)) {
            stopSelf(); // Detener el servicio si se selecciona la acción
            return START_NOT_STICKY;
        }

        String soundUriString = intent.getStringExtra("soundUri");

        // Configurar notificación para el servicio en primer plano
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Cambia por un ícono válido
                .setContentTitle("Reproduciendo alarma")
                .setContentText("Tu alarma está sonando")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true) // Hace que la notificación sea persistente
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_stop, // Ícono para el botón de detener
                        "Apagar", // Texto del botón
                        getStopAlarmPendingIntent()
                ));

        startForeground(1, notificationBuilder.build());

        if (soundUriString != null) {
            try {
                Uri soundUri = Uri.parse(soundUriString);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, soundUri);
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(mp -> stopSelf()); // Detener el servicio cuando termine
            } catch (IOException e) {
                e.printStackTrace();
                stopSelf(); // Detener el servicio si ocurre un error
            }
        }

        return START_NOT_STICKY;
    }

    private PendingIntent getStopAlarmPendingIntent() {
        Intent stopIntent = new Intent(this, AlarmSoundService.class);
        stopIntent.setAction(ACTION_STOP_ALARM);

        return PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE // Asegúrate de usar el flag adecuado para tu API
        );
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal de Alarma",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
