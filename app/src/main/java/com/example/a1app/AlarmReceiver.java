package com.example.a1app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String activityName = intent.getStringExtra("activityName");
        String soundUriString = intent.getStringExtra("soundUri");

        Toast.makeText(context, "¡Es hora de: " + activityName + "!", Toast.LENGTH_LONG).show();

        if (soundUriString != null) {
            Intent serviceIntent = new Intent(context, AlarmSoundService.class);
            serviceIntent.putExtra("soundUri", soundUriString);
            context.startForegroundService(serviceIntent);
        }
    }

}
