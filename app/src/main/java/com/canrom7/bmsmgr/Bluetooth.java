package com.canrom7.bmsmgr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import androidx.core.view.MotionEventCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint("MissingPermission")
public class Bluetooth {
    private static final String TAG="Bluetooth";
    private static final String[] deviceIds={"F000", "F00088A03CA54AE421E0", "650B", "650B88A03CA539DB5BEE", "88A03CA539DB5BEE", "C1EA", "C1EA88A03CA55080C7CF", "0B2D", "0B2D88A0191229110E19", "4458", "44585A4E424C45", "4A4B", "4A4B0001"};
    protected static final char[] hexArray="0123456789ABCDEF".toCharArray();
    public MainActivity mActivity;
    public long mCppThis;
    private Object mScanCallback;
    private BleService mBleService=null;
    private BluetoothInfo mActiveDevice=null;
    private BluetoothAdapter mBluetoothAdapter=null;
    private List< BluetoothInfo > mDevices=new ArrayList();
    private boolean mPermissionBleAllowed=false;
    private boolean mPermissionLocationAllowed=false;
    private String mPermissionErrorString="";
    private boolean mConnected=false;
    private boolean mScanning=false;
    private byte devVid=-120;
    private final ServiceConnection mServiceConnection=new ServiceConnection() { // from class: com.smartsoft.ble.Bluetooth.3
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            String str;
            String str2;
            Log.d(Bluetooth.TAG, "Service connected");
            Bluetooth.this.mBleService=((BleService.LocalBinder) iBinder).getService();
            if (Bluetooth.this.mBleService != null) {
                Bluetooth.this.mBleService.setBluetooth(Bluetooth.this);
            }
            if (!Bluetooth.this.mBleService.initialize()) {
                str=Bluetooth.TAG;
                str2="Unable to initialize Bluetooth!";
            } else {
                if (Bluetooth.this.mActiveDevice == null || Bluetooth.this.mBleService.connect(Bluetooth.this.mActiveDevice.device)) {
                    return;
                }
                str=Bluetooth.TAG;
                str2="Connect failure!";
            }
            Log.w(str, str2);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(Bluetooth.TAG, "Service disconnected");
            Bluetooth.this.mBleService=null;
        }
    };
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() { // from class: com.smartsoft.ble.Bluetooth.4
//        @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
//        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
//            JScanRecord parseFromBytes = JScanRecord.parseFromBytes(bArr);
//            BleType deviceType = Bluetooth.this.deviceType(bArr);
//            if (deviceType != BleType.Unknown && deviceType != null) {
//                Bluetooth.this.addDevice(bluetoothDevice, bArr, deviceType.ordinal(), i, parseFromBytes);
//                return;
//            }
//            SparseArray<byte[]> manufacturerSpecificData = parseFromBytes.getManufacturerSpecificData();
//            if (manufacturerSpecificData.size() != 0 && Bluetooth.isValidDevice(Bluetooth.bytesToHex(manufacturerSpecificData.valueAt(0)))) {
//                Bluetooth.this.addDevice(bluetoothDevice, bArr, BleType.JDY_Other.ordinal(), i, parseFromBytes);
//            }
//        }
//    };

    public Bluetooth(Activity activity, long j) {
        this.mScanCallback=null;
        MainActivity mainActivity=(MainActivity) activity;
        this.mActivity=mainActivity;
        this.mCppThis=j;
        if (mainActivity != null) {
            mainActivity.mBluetooth=this;
        }
        this.mScanCallback=new ScanCallback() {
            private void addDevice(ScanResult scanResult) {
                byte[] bytes=scanResult.getScanRecord().getBytes();
                JScanRecord parseFromBytes=JScanRecord.parseFromBytes(bytes);
                if (parseFromBytes.getDeviceName() == null) {
                    return;
                }
                BleType deviceType=Bluetooth.this.deviceType(bytes);
                if (deviceType != BleType.Unknown && deviceType != null) {
                    Bluetooth.this.addDevice(scanResult.getDevice(), bytes, deviceType.ordinal(), scanResult.getRssi(), parseFromBytes);
                    return;
                }
                SparseArray< byte[] > manufacturerSpecificData=parseFromBytes.getManufacturerSpecificData();
                if (manufacturerSpecificData.size() != 0 && Bluetooth.isValidDevice(Bluetooth.bytesToHex(manufacturerSpecificData.valueAt(0)).substring(0, 4))) {
                    Bluetooth.this.addDevice(scanResult.getDevice(), bytes, BleType.JDY_Other.ordinal(), scanResult.getRssi(), parseFromBytes);
                }
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onBatchScanResults(List< ScanResult > list) {
                super.onBatchScanResults(list);
                Iterator< ScanResult > it=list.iterator();
                while (it.hasNext()) {
                    addDevice(it.next());
                }
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onScanFailed(int i) {
                super.onScanFailed(i);
                Log.e(Bluetooth.TAG, "***** Scan failed: " + i);
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onScanResult(int i, ScanResult scanResult) {
                super.onScanResult(i, scanResult);
                addDevice(scanResult);
            }
        };

    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addDevice(BluetoothDevice bluetoothDevice, byte[] bArr, int i, int i2, JScanRecord jScanRecord) {
        String address=bluetoothDevice.getAddress();
        boolean z=false;
        BluetoothInfo bluetoothInfo=null;
        for (BluetoothInfo bluetoothInfo2 : this.mDevices) {
            if (bluetoothInfo2.device.equals(bluetoothDevice)) {
                if (bluetoothInfo2.device.getAddress().equals(address)) {
                    bluetoothInfo2.device=bluetoothDevice;
                    bluetoothInfo2.scanRecord=bArr;
                    bluetoothInfo2.type=i;
                    String deviceName=jScanRecord.getDeviceName();
                    if (deviceName == null) {
                        deviceName=bluetoothDevice.getName();
                    }
                    if (deviceName != null && !deviceName.equals(bluetoothInfo2.name)) {
                        bluetoothInfo2.name=deviceName;
                        NativeClass.deviceUpdateName(this.mCppThis, address, deviceName);
                    }
                    if (bluetoothInfo2.rssi != i2) {
                        bluetoothInfo2.rssi=i2;
                        NativeClass.deviceUpdateRSSI(this.mCppThis, address, i2);
                    }
                    bluetoothInfo=bluetoothInfo2;
                }
                z=true;
            }
        }
        if (!z) {
            bluetoothInfo=new BluetoothInfo();
            bluetoothInfo.device=bluetoothDevice;
            bluetoothInfo.scanRecord=bArr;
            bluetoothInfo.type=i;
            bluetoothInfo.rssi=i2;
            String deviceName2=jScanRecord.getDeviceName();
            bluetoothInfo.name=deviceName2;
            if (deviceName2 == null) {
                bluetoothInfo.name=bluetoothDevice.getName();
            }
            this.mDevices.add(bluetoothInfo);
        }
        if (bluetoothInfo != null) {
            String address2=bluetoothInfo.device.getAddress();
            NativeClass.deviceAdded(this.mCppThis, bluetoothInfo.name, address2, address2, bluetoothInfo.type, bluetoothInfo.rssi);
        }
    }

    private List< ScanFilter > buildScanFilters() {
        ArrayList arrayList=new ArrayList();
        arrayList.add(new ScanFilter.Builder().build());
        return arrayList;
    }

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder=new ScanSettings.Builder();
        builder.setScanMode(2);
        builder.setCallbackType(1);
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String bytesToHex(byte[] bArr) {
        if (bArr == null) {
            return "<null>";
        }
        char[] cArr=new char[bArr.length * 2];
        for (int i=0; i < bArr.length; i++) {
            byte b=bArr[i];
            int i2=i * 2;
            char[] cArr2=hexArray;
            cArr[i2]=cArr2[(b & 255) >>> 4];
            cArr[i2 + 1]=cArr2[b & 15];
        }
        return new String(cArr);
    }

    public static Bluetooth createBluetooth(Activity activity, long j) {
        return new Bluetooth(activity, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BleType deviceType(byte[] bArr) {
        if (bArr.length != 62) {
            return BleType.Unknown;
        }
        if (bArr[5] != -32 || bArr[6] != -1 || this.devVid != bArr[13]) {
            return BleType.Unknown;
        }
        byte b=bArr[14];
        return (b == -91 || b == -79 || b == -78 || b == -60 || b == -59) ? BleType.JDY_Other : BleType.JDY;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isValidDevice(String str) {
        int i=0;
        while (true) {
            String[] strArr=deviceIds;
            if (i >= strArr.length) {
                return false;
            }
            if (strArr[i].equals(str)) {
                return true;
            }
            i++;
        }
    }

    private String nameByUuid(String str) {
        BluetoothInfo itemByUuid=itemByUuid(str);
        return itemByUuid == null ? "" : itemByUuid.name;
    }

    private String nameOfBluetoothInfo(BluetoothInfo bluetoothInfo) {
        if (bluetoothInfo == null || bluetoothInfo.device == null) {
            return null;
        }
        for (BluetoothInfo bluetoothInfo2 : this.mDevices) {
            if (bluetoothInfo2.device.getUuids().equals(bluetoothInfo.device.getUuids())) {
                return bluetoothInfo2.name;
            }
        }
        return bluetoothInfo.device.getName();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0072 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x000e A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static String parseDeviceName(byte[] r5) {
        /*
            r0 = 0
            if (r5 != 0) goto L4
            return r0
        L4:
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.wrap(r5)
            java.nio.ByteOrder r1 = java.nio.ByteOrder.LITTLE_ENDIAN
            java.nio.ByteBuffer r5 = r5.order(r1)
        Le:
            int r1 = r5.remaining()
            r2 = 2
            if (r1 <= r2) goto L7b
            byte r1 = r5.get()
            if (r1 != 0) goto L1c
            goto L7b
        L1c:
            byte r3 = r5.get()
            int r1 = r1 + (-1)
            byte r1 = (byte) r1
            r4 = -1
            if (r3 == r4) goto L6a
            r4 = 20
            if (r3 == r4) goto L61
            r4 = 21
            if (r3 == r4) goto L53
            switch(r3) {
                case 1: goto L4c;
                case 2: goto L61;
                case 3: goto L61;
                case 4: goto L42;
                case 5: goto L42;
                case 6: goto L53;
                case 7: goto L53;
                case 8: goto L32;
                case 9: goto L32;
                default: goto L31;
            }
        L31:
            goto L70
        L32:
            byte[] r0 = new byte[r1]
            r2 = 0
            r5.get(r0, r2, r1)
            java.lang.String r5 = new java.lang.String
            r5.<init>(r0)
            java.lang.String r5 = r5.trim()
            return r5
        L42:
            r2 = 4
            if (r1 < r2) goto L70
            r5.getInt()
            int r1 = r1 + (-4)
            byte r1 = (byte) r1
            goto L42
        L4c:
            r5.get()
            int r1 = r1 + (-1)
        L51:
            byte r1 = (byte) r1
            goto L70
        L53:
            r2 = 16
            if (r1 < r2) goto L70
            r5.getLong()
            r5.getLong()
            int r1 = r1 + (-16)
            byte r1 = (byte) r1
            goto L53
        L61:
            if (r1 < r2) goto L70
            r5.getShort()
            int r1 = r1 + (-2)
            byte r1 = (byte) r1
            goto L61
        L6a:
            r5.getShort()
            int r1 = r1 + (-2)
            goto L51
        L70:
            if (r1 <= 0) goto Le
            int r2 = r5.position()
            int r2 = r2 + r1
            r5.position(r2)
            goto Le
        L7b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.smartsoft.ble.Bluetooth.parseDeviceName(byte[]):java.lang.String");
    }

    public void clearDevice() {
        BleService bleService=this.mBleService;
        if (bleService != null) {
            bleService.close();
        }
        if (this.mActiveDevice != null) {
            this.mActiveDevice=null;
        }
        this.mDevices.clear();
    }

    public int connectState() {
        BleService bleService=this.mBleService;
        return (bleService == null || this.mActiveDevice == null) ? ConnectState.ConnectStateDisconnected.ordinal() : bleService.connectState();
    }

    public boolean connectTo(String str) {
        stopScan();
        BluetoothInfo itemByUuid=itemByUuid(str);
        if (itemByUuid == null) {
            return false;
        }
        BluetoothInfo bluetoothInfo=this.mActiveDevice;
        if (bluetoothInfo == itemByUuid && this.mConnected) {
            return true;
        }
        if (bluetoothInfo != null) {
            disconnectFrom(bluetoothInfo.device.getAddress());
        }
        this.mActiveDevice=itemByUuid;
        BleService bleService=this.mBleService;
        if (bleService == null) {
            return false;
        }
        return bleService.connect(itemByUuid.device);
    }

    public String currentUuid() {
        BluetoothInfo bluetoothInfo=this.mActiveDevice;
        return bluetoothInfo == null ? "" : bluetoothInfo.device.getAddress();
    }

    public void dataAvailableFunc(String str, byte[] bArr) {
        if (str == null || bArr == null || bArr.length == 0) {
            return;
        }
        NativeClass.dataReceived(this.mCppThis, str, bArr);
    }

    public void deviceConnected(String str) {
        if (str == null) {
            return;
        }
        this.mConnected=true;
        NativeClass.deviceConnected(this.mCppThis, str, nameByUuid(str));
    }

    public void deviceDisconnected(String str) {
        if (str == null) {
            return;
        }
        this.mConnected=false;
        NativeClass.deviceDisconnected(this.mCppThis, str, nameByUuid(str));
    }

    public int deviceState() {
        DeviceState deviceState;
        BluetoothAdapter bluetoothAdapter=this.mBluetoothAdapter;
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            switch (this.mBluetoothAdapter.getState()) {
                case 10:
                case 11:
                case MotionEventCompat.AXIS_RY /* 13 */:
                    deviceState=DeviceState.StatePoweredOff;
                    break;
                case MotionEventCompat.AXIS_RX /* 12 */:
                    deviceState=DeviceState.StatePoweredOn;
                    break;
                default:
                    deviceState=DeviceState.StateUnauthorized;
                    break;
            }
        } else {
            deviceState=DeviceState.StateUnsupported;
        }
        return deviceState.ordinal();
    }

    public void disconnectFrom(String str) {
        BleService bleService=this.mBleService;
        if (bleService != null) {
            bleService.close();
        }
        if (this.mActiveDevice != null) {
            this.mActiveDevice=null;
            this.mConnected=false;
        }
    }


    public ServiceConnection getServiceConnection() {
        return this.mServiceConnection;
    }

    public boolean isConnected(String str) {
        return itemByUuid(str) != null && this.mConnected && this.mActiveDevice != null && connectState() == ConnectState.ConnectStateConnected.ordinal();
    }

    public boolean isPermissionAllowed() {
        return this.mPermissionBleAllowed && this.mPermissionLocationAllowed;
    }

    public boolean isScanning() {
        if (this.mBluetoothAdapter == null) {
            return false;
        }
        return this.mScanning;
    }

    public BluetoothInfo itemByUuid(String str) {
        if (str != null && str.length() != 0) {
            for (BluetoothInfo bluetoothInfo : this.mDevices) {
                if (bluetoothInfo.device.getAddress().equals(str)) {
                    return bluetoothInfo;
                }
            }
        }
        return null;
    }

    public String notifyCharacteristicUuid() {
        return BleService.UUID_NOTIFY_CHARACTERISTIC;
    }

    public String notifyServerUuid() {
        return BleService.UUID_NOTIFY_SERVER;
    }

    public String permissionErrorString() {
        return this.mPermissionErrorString;
    }

    public String readCharacteristicUuid() {
        return BleService.UUID_READ_CHARACTERISTIC;
    }

    public void recvData(String str, byte[] bArr) {
        if (str != null) {
            NativeClass.dataReceived(this.mCppThis, str, bArr);
        }
    }

    public boolean requestPermission() {
        this.mActivity.requestScanPermission();
        return true;
    }

    public void serviceDiscovered(String str) {
        if (str == null) {
            return;
        }
        this.mBleService.enableBle(0);
        this.mBleService.delay(50);
        this.mBleService.enableBle(1);
        this.mBleService.delay(50);
        this.mBleService.enableBle(2);
        NativeClass.readyToWrite(this.mCppThis, str);
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.mBluetoothAdapter=bluetoothAdapter;
    }

    public void setNotifyCharacteristicUuid(String str) {
        BleService.UUID_NOTIFY_CHARACTERISTIC=str;
    }

    public void setNotifyServerUuid(String str) {
        BleService.UUID_NOTIFY_SERVER=str;
    }

    public void setPermissionState(boolean z, String str, String str2) {
        if (str2.equals("ble")) {
            this.mPermissionBleAllowed=z;
        } else if (str2.equals("location")) {
            this.mPermissionLocationAllowed=z;
        }
        this.mPermissionErrorString=str;
        NativeClass.permissionStateChanged(this.mCppThis, isPermissionAllowed(), str);
    }

    public void setReadCharacteristicUuid(String str) {
        BleService.UUID_READ_CHARACTERISTIC=str;
    }

    public void setWriteCharacteristicUuid(String str) {
        BleService.UUID_WRITE_CHARACTERISTIC=str;
    }

    public void setWriteServeUuid(String str) {
        BleService.UUID_WRITE_SERVER=str;
    }

    public boolean startScan() {
        if (!this.mPermissionBleAllowed && this.mBluetoothAdapter == null) {
            return false;
        }
        if (this.mScanCallback == null) {
            this.mScanning=false;
            return false;
        }
        Log.i(TAG, "startScan - use new version");
        BluetoothLeScanner bluetoothLeScanner=this.mBluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            this.mScanning=false;
            return false;
        }
        bluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), (ScanCallback) this.mScanCallback);
        this.mScanning=true;
        return true;
    }

    public void stopScan() {
        BluetoothLeScanner bluetoothLeScanner;
        BluetoothAdapter bluetoothAdapter=this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            this.mScanning=false;
        } else {
            if (!this.mScanning || this.mScanCallback == null || (bluetoothLeScanner=bluetoothAdapter.getBluetoothLeScanner()) == null) {
                return;
            }
            bluetoothLeScanner.stopScan((ScanCallback) this.mScanCallback);
            this.mScanning=false;
        }
    }

    public String writeCharacteristicUuid() {
        return BleService.UUID_WRITE_CHARACTERISTIC;
    }

    public int writeData(byte[] bArr) {
        BleService bleService=this.mBleService;
        if (bleService == null) {
            return 0;
        }
        return bleService.writeData(bArr);
    }

    public String writeServeUuid() {
        return BleService.UUID_WRITE_SERVER;
    }
}