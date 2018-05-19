package com.example.macbook.bluetoothproject;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BluetoothAdapter mBluetoothAdapter;
    private ListView BTdevicesListView;
    private Set<String> BTdevicesList = new HashSet<>();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
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

                BTdevicesList.add("Name: " + deviceName + ", address: " + deviceHardwareAddress);
                ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, BTdevicesList.toArray());
                BTdevicesListView.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Asking for permission to location services if not granted.
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Views.
        BTdevicesListView = (ListView) findViewById(R.id.BTdevicesListView);
        Button btnSCAN = (Button) findViewById(R.id.btnSCAN);
        Button btnRESET = (Button) findViewById(R.id.btnRESET);

        // Click listeners for buttons.
        btnSCAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        btnRESET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetResult();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy called");
        super.onDestroy();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        if (mScanReceiver != null)
            unregisterReceiver(mScanReceiver);
    }

    public void enableDisable() {
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "No Bluetooth support.");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "enableDisable: enabling BT");
            mBluetoothAdapter.enable();
            mBluetoothAdapter.isDiscovering();
            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, intentFilter);
        } else {
            Log.i(TAG, "enableDisable: disabling BT");
            mBluetoothAdapter.disable();
            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, intentFilter);
        }
    }

    public void scan() {
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "No Bluetooth support.");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "enableDisable: enabling BT");
            mBluetoothAdapter.enable();
        }

        Log.i(TAG, "is started discovering: " + mBluetoothAdapter.startDiscovery());

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mScanReceiver, filter);
    }

    public void resetResult() {
        BTdevicesList.clear();
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, BTdevicesList.toArray());
        BTdevicesListView.setAdapter(adapter);
    }
}
