package com.canrom7.bmsmgr.charge;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.canrom7.bmsmgr.R;

import java.util.Set;

/* loaded from: classes.dex */
public class DeviceListActivity extends Activity {
    private static final boolean DEBUG = true;
    public static String DEVICE_ADDRESS = "device address";
    private static final String TAG = "DeviceList";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevices;
    private ArrayAdapter<String> mPairedDevices;
    private Button scanButton;
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() { // from class: com.example.bluetooth.DeviceListActivity.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            DeviceListActivity.this.mBtAdapter.cancelDiscovery();
            String str = ((TextView) v).getText().toString();
            String address = str.substring(str.length() - 17);
            Intent intent = new Intent();
            intent.putExtra(DeviceListActivity.DEVICE_ADDRESS, address);
            DeviceListActivity.this.setResult(-1, intent);
            DeviceListActivity.this.finish();
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.example.bluetooth.DeviceListActivity.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (device.getBondState() != 12) {
                    DeviceListActivity.this.mNewDevices.add(device.getName() + "\n" + device.getAddress());
                    return;
                }
                return;
            }
            if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                DeviceListActivity.this.setProgressBarIndeterminateVisibility(false);
                DeviceListActivity.this.scanButton.setText("搜索");
                if (DeviceListActivity.this.mNewDevices.getCount() == 0) {
                    DeviceListActivity.this.mNewDevices.add("未搜到任何设备");
                }
            }
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(5);
        setContentView(R.layout.device_list);
        setResult(0);
        this.scanButton = (Button) findViewById(R.id.button_scan);
        this.scanButton.setOnClickListener(new View.OnClickListener() { // from class: com.example.bluetooth.DeviceListActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                DeviceListActivity.this.BtDiscovery();
            }
        });
        this.mPairedDevices = new ArrayAdapter<>(this, R.layout.device_name);
        this.mNewDevices = new ArrayAdapter<>(this, R.layout.device_name);
        ListView pairedList = (ListView) findViewById(R.id.paired_devices);
        ListView newDevicesList = (ListView) findViewById(R.id.new_devices);
        pairedList.setAdapter((ListAdapter) this.mPairedDevices);
        newDevicesList.setAdapter((ListAdapter) this.mNewDevices);
        pairedList.setOnItemClickListener(this.mDeviceClickListener);
        newDevicesList.setOnItemClickListener(this.mDeviceClickListener);
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        registerReceiver(this.mReceiver, filter);
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = this.mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                this.mPairedDevices.add(device.getName() + "\n" + device.getAddress());
            }
            return;
        }
        this.mPairedDevices.add("未搜到任何设备");
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBtAdapter != null) {
            this.mBtAdapter.cancelDiscovery();
        }
        unregisterReceiver(this.mReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void BtDiscovery() {
        Log.d(TAG, "doDiscovery()");
        setProgressBarIndeterminateVisibility(DEBUG);
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        if (this.mBtAdapter.isDiscovering()) {
            this.mBtAdapter.cancelDiscovery();
            this.scanButton.setText("搜索");
        } else {
            this.scanButton.setText("正在搜索(点击停止)");
            this.mNewDevices.clear();
            this.mBtAdapter.startDiscovery();
        }
    }
}