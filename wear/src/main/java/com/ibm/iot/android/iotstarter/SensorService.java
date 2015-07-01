package com.ibm.iot.android.iotstarter;

/**
 * Created by Ondrej Plevka on 6.4.2015.
 */

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Log;



public class SensorService extends Service {
    private static final String TAG = "SensorDashboard/SensorService";


    private DeviceClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        client = DeviceClient.getInstance(this);

        Intent notificationIntent = new Intent(this, SensorSelectorActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_icon,
                        "Select Sensors", actionPendingIntent)
                        .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("IoT Watch");
        builder.setContentText("Collecting sensor data..");
        builder.setSmallIcon(R.drawable.ic_icon);
        builder.extend(new WearableExtender().addAction(action));

        startForeground(1, builder.build());

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

