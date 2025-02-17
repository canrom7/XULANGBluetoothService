package com.canrom7.bmsmgr;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BleService extends Service {
    public static int MTU_MAX_VALUE = 188;
    private static final String TAG = "BleService";
    public static String UUID_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";
    public static String UUID_NOTIFY_CHARACTERISTIC = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String UUID_NOTIFY_SERVER = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String UUID_READ_CHARACTERISTIC = "0000ffe3-0000-1000-8000-00805f9b34fb";
    public static String UUID_WRITE_CHARACTERISTIC = "0000ffe2-0000-1000-8000-00805f9b34fb";
    public static String UUID_WRITE_SERVER = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private BluetoothGattCharacteristic mCharacteristicNotify;
    private BluetoothGattCharacteristic mCharacteristicWrite;
    private BluetoothGattCharacteristic mCharacteristicWriteFunc;
    private Bluetooth mBluetooth = null;
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGatt mBluetoothGatt = null;
    private int maxPkgCount = 20;
    public boolean mRequestMtuFlag = false;
    public boolean mMtuChanged = false;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() { // from class: com.smartsoft.ble.BleService.1
        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            String uuid = bluetoothGattCharacteristic.getUuid().toString();
            if (BleService.UUID_NOTIFY_CHARACTERISTIC.equals(uuid)) {
                BleService.this.mBluetooth.recvData(bluetoothGatt.getDevice().getAddress(), bluetoothGattCharacteristic.getValue());
            } else if (BleService.UUID_WRITE_CHARACTERISTIC.equals(uuid)) {
                BleService.this.mBluetooth.dataAvailableFunc(bluetoothGatt.getDevice().getAddress(), bluetoothGattCharacteristic.getValue());
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            String uuid = bluetoothGattCharacteristic.getUuid().toString();
            if (i != 0) {
                return;
            }
            if (BleService.UUID_NOTIFY_CHARACTERISTIC.equals(uuid)) {
                BleService.this.mBluetooth.recvData(bluetoothGatt.getDevice().getAddress(), bluetoothGattCharacteristic.getValue());
            } else if (BleService.UUID_WRITE_CHARACTERISTIC.equals(uuid)) {
                BleService.this.mBluetooth.dataAvailableFunc(bluetoothGatt.getDevice().getAddress(), bluetoothGattCharacteristic.getValue());
            }
        }


        @Override // android.bluetooth.BluetoothGattCallback
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            if (i2 != 2) {
                if (i2 == 0) {
                    BleService.this.mMtuChanged = false;
                    Log.w(BleService.TAG, "Disconnected from GATT server.");
                    BleService.this.mBluetooth.deviceDisconnected(bluetoothGatt.getDevice().getAddress());
                    BleService.this.refreshDeviceCache(bluetoothGatt);
                    bluetoothGatt.close();
                    BleService.this.mBluetoothGatt = null;
                    return;
                }
                return;
            }
            Log.w(BleService.TAG, "Connected to GATT server.");
            BleService bleService = BleService.this;
            int i3 = BleService.MTU_MAX_VALUE;
            if (i3 <= 23 || i3 >= 512) {
                if (bleService.mBluetoothGatt != null && !BleService.this.mBluetoothGatt.discoverServices()) {
                    Log.w(BleService.TAG, "Discovery services failure");
                }
            } else if (!bleService.mMtuChanged) {
                bleService.mRequestMtuFlag = true;
                bluetoothGatt.requestMtu(i3);
            }
            BleService.this.mBluetooth.deviceConnected(bluetoothGatt.getDevice().getAddress());
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
            String str;
            StringBuilder sb;
            super.onMtuChanged(bluetoothGatt, i, i2);
            BleService bleService = BleService.this;
            bleService.mMtuChanged = true;
            bleService.maxPkgCount = i - 3;
            if (i2 != 0 || (BleService.this.mRequestMtuFlag && BleService.MTU_MAX_VALUE != i)) {
                str = BleService.TAG;
                sb = new StringBuilder("MTU change fail! (");
            } else {
                str = BleService.TAG;
                sb = new StringBuilder("MTU change success! (");
            }
            sb.append(i);
            sb.append(")");
            Log.w(str, sb.toString());
            if (BleService.this.mBluetoothGatt == null || BleService.this.mBluetoothGatt.discoverServices()) {
                return;
            }
            Log.w(BleService.TAG, "Discovery services failure");
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            if (i != 0) {
                Log.w(BleService.TAG, "Services discovered: " + i);
                return;
            }
            BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(BleService.UUID_NOTIFY_SERVER));
            if (service == null) {
                Log.w(BleService.TAG, "services is invalid!");
                return;
            }
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (int i2 = 0; i2 < characteristics.size(); i2++) {
                BluetoothGattCharacteristic bluetoothGattCharacteristic = characteristics.get(i2);
                String uuid = bluetoothGattCharacteristic.getUuid().toString();
                int properties = bluetoothGattCharacteristic.getProperties();
                if (uuid.equals(BleService.UUID_WRITE_CHARACTERISTIC)) {
                    bluetoothGattCharacteristic.setWriteType(1);
                    BleService.this.mCharacteristicWriteFunc = bluetoothGattCharacteristic;
                } else if (uuid.equals(BleService.UUID_NOTIFY_CHARACTERISTIC)) {
                    if ((properties & 16) == 16) {
                        BleService.this.mCharacteristicNotify = bluetoothGattCharacteristic;
                    }
                    if ((properties & 8) == 8 || (properties & 4) == 4) {
                        bluetoothGattCharacteristic.setWriteType(1);
                        BleService.this.mCharacteristicWrite = bluetoothGattCharacteristic;
                    }
                }
            }
            super.onServicesDiscovered(bluetoothGatt, i);
            BleService.this.mBluetooth.serviceDiscovered(bluetoothGatt.getDevice().getAddress());
        }
    };
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BleService getService() {
            return BleService.this;
        }
    }

    public static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (int i = 0; i < bArr.length; i++) {
            String upperCase = Integer.toHexString(bArr[i] & 255).toUpperCase();
            if (upperCase.length() < 2) {
                sb.append(0);
            }
            sb.append(upperCase);
            if (i < bArr.length - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    public void close() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            this.mBluetoothGatt = null;
            delay(50);
        }
    }

    public boolean connect(BluetoothDevice bluetoothDevice) {
        String str;
        String str2;
        if (bluetoothDevice == null) {
            str = TAG;
            str2 = "Unspecified address!";
        } else {
            if (this.mBluetoothAdapter != null) {
                close();
                this.mBluetoothGatt = bluetoothDevice.connectGatt(this, false, this.mGattCallback);
                Log.w(TAG, "Trying to create a new connection.");
                return true;
            }
            str = TAG;
            str2 = "BluetoothAdapter is not initialized!";
        }
        Log.w(str, str2);
        return false;
    }

    public int connectState() {
        BluetoothDevice device;
        ConnectState connectState=null;
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null && (device = bluetoothGatt.getDevice()) != null) {
            int connectionState = this.mBluetoothManager.getConnectionState(device, 7);
            if (connectionState == 1) {
                connectState = ConnectState.ConnectStateConnecting;
            } else if (connectionState == 2) {
                connectState = ConnectState.ConnectStateConnected;
            } else if (connectionState == 3) {
                connectState = ConnectState.ConnectStateDisconnecting;
            }
            return connectState.ordinal();
        }
        connectState = ConnectState.ConnectStateDisconnected;
        return connectState.ordinal();
    }

    public void delay(int i) {
        try {
            Thread.currentThread();
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void enableBle(int i) {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return;
        }
        try {
            BluetoothGattCharacteristic bluetoothGattCharacteristic = i != 0 ? i != 1 ? i != 2 ? null : this.mCharacteristicWriteFunc : this.mCharacteristicWrite : this.mCharacteristicNotify;
            if (bluetoothGattCharacteristic == null) {
                return;
            }
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
            BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(UUID_DESCRIPTOR));
            if (descriptor == null) {
                return;
            }
            descriptor.setValue(new byte[]{1, 0});
            this.mBluetoothGatt.writeDescriptor(descriptor);
            delay(50);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public boolean initialize() {
        String str="";
        String str2="";
        if (this.mBluetoothAdapter == null && this.mBluetoothManager == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            this.mBluetoothManager = bluetoothManager;
            if (bluetoothManager == null) {
                str = TAG;
                str2 = "Unable to initialize BluetoothManager.";
            } else {
                BluetoothAdapter adapter = bluetoothManager.getAdapter();
                this.mBluetoothAdapter = adapter;
                if (adapter == null) {
                    str = TAG;
                    str2 = "Unable to obtain a BluetoothAdapter.";
                }
            }
            Log.w(str, str2);
            return false;
        }
        return true;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean refreshDeviceCache(BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt == null) {
            return false;
        }
        try {
            Method method = bluetoothGatt.getClass().getMethod("refresh", null);
            if (method != null) {
                return ((Boolean) method.invoke(bluetoothGatt, null)).booleanValue();
            }
            return false;
        } catch (Exception unused) {
            Log.i(TAG, "An exception occurred while refreshing device");
            return false;
        }
    }

    public void setBluetooth(Bluetooth bluetooth) {
        this.mBluetooth = bluetooth;
    }

    public int writeData(byte[] bArr) {
        int i;
        if (this.mBluetoothGatt == null || this.mCharacteristicWrite == null) {
            return 0;
        }
        int i2 = this.maxPkgCount;
        int length = bArr.length;
        int i3 = length / i2;
        int i4 = length % i2;
        byte[] bArr2 = new byte[i2];
        if (i3 > 0) {
            i = 0;
            for (int i5 = 0; i5 < i3; i5++) {
                System.arraycopy(bArr, i, bArr2, 0, i2);
                this.mCharacteristicWrite.setValue(bArr2);
                this.mBluetoothGatt.writeCharacteristic(this.mCharacteristicWrite);
                delay(20);
                i += i2;
            }
        } else {
            i = 0;
        }
        if (i4 <= 0) {
            return i;
        }
        byte[] bArr3 = new byte[i4];
        System.arraycopy(bArr, i, bArr3, 0, i4);
        this.mCharacteristicWrite.setValue(bArr3);
        this.mBluetoothGatt.writeCharacteristic(this.mCharacteristicWrite);
        delay(20);
        return i + i4;
    }

    public void writeFunc(byte[] bArr) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        if (this.mBluetoothGatt == null || (bluetoothGattCharacteristic = this.mCharacteristicWriteFunc) == null) {
            return;
        }
        bluetoothGattCharacteristic.setValue(bArr);
        this.mBluetoothGatt.writeCharacteristic(this.mCharacteristicWriteFunc);
        delay(20);
    }
}