package com.example.macbook.bluetoothproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: alarm worked.");
        mContext = context;
        scan();
    }

    public void scan() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "No Bluetooth support.");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "enableDisable: enabling BT");
            mBluetoothAdapter.enable();
        }

        Log.i(TAG, "scan: Scanning started.");
        Log.i(TAG, "is started discovering: " + mBluetoothAdapter.startDiscovery());

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        ScanReceiver receiver = new ScanReceiver();

        Log.i(TAG, "is discovering: " + mBluetoothAdapter.isDiscovering());
        mContext.getApplicationContext().registerReceiver(receiver, filter);
    }
}
