package com.example.macbook.bluetoothproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class ScanReceiver extends BroadcastReceiver {
    private final static String TAG = "ScanReceiver";
    private int notificationId = 1;

    private Set<String> BTdevicesList = new HashSet<>();

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address

            Log.i(TAG, "Device name: " + deviceName);
            Log.i(TAG, "Device address: " + deviceHardwareAddress);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), NotificationChannel.DEFAULT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Found a new BT device")
                    .setContentText("Name: " + deviceName + ", address: " + deviceHardwareAddress)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Name: " + deviceName + ", address: " + deviceHardwareAddress))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

            String deviceStr = "Name: " + deviceName + ", address: " + deviceHardwareAddress;

            if (!BTdevicesList.contains(deviceStr)) {
                notificationId += 1;
                BTdevicesList.add(deviceStr);
                notificationManager.notify(notificationId, mBuilder.build());
            }
        }
    }
}
