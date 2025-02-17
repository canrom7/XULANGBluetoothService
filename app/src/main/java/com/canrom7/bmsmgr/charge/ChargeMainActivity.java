package com.canrom7.bmsmgr.charge;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.canrom7.bmsmgr.MainActivity;
import com.canrom7.bmsmgr.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChargeMainActivity extends Activity {

    public static final int BT_TOAST = 5;
    public static final int CONNECTED_DEVICE_NAME = 4;
    private static final boolean DEBUG = false;
    public static final String DEVICE_NAME = "device name";
    public static final int MAIN_TOAST = 6;
    public static final int REC_DATA = 2;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "MainActivity";
    public static final String TOAST = "toast";
    private Button Button01;
    private Button Button02;
    private Button Button03;
    private Button Button04;
    private Button Button05;
    private Button ClearWindow;
    private TextView RecDataView;
    private Button pauseButton;
    private EditText period;
    private RadioGroup rgRec;
    private RadioGroup rgSend;
    private Button sendButton;
    private EditText sendContent;
    private CheckBox setPeriod;
    static boolean isHEXsend = false;
    static boolean isHEXrec = false;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private StringBuffer Str2Send1 = new StringBuffer("1");
    private StringBuffer Str2Send2 = new StringBuffer("2");
    private StringBuffer Str2Send3 = new StringBuffer("3");
    private StringBuffer Str2Send4 = new StringBuffer("4");
    private StringBuffer Str2Send5 = new StringBuffer("5");
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService mConnectService = null;
    private View.OnLongClickListener ButtonLongClickListener = new View.OnLongClickListener() { // from class: com.example.main.MainActivity.1
        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.Button01 /* 2130903042 */:
                    new defineButtonDialog(ChargeMainActivity.this, ChargeMainActivity.this.Button01, ChargeMainActivity.this.Str2Send1).show();
                    break;
                case R.id.Button02 /* 2130903043 */:
                    new defineButtonDialog(ChargeMainActivity.this, ChargeMainActivity.this.Button02, ChargeMainActivity.this.Str2Send2).show();
                    break;
                case R.id.Button03 /* 2130903044 */:
                    new defineButtonDialog(ChargeMainActivity.this, ChargeMainActivity.this.Button03, ChargeMainActivity.this.Str2Send3).show();
                    break;
                case R.id.Button04 /* 2130903045 */:
                    new defineButtonDialog(ChargeMainActivity.this, ChargeMainActivity.this.Button04, ChargeMainActivity.this.Str2Send4).show();
                    break;
                case R.id.Button05 /* 2130903046 */:
                    new defineButtonDialog(ChargeMainActivity.this, ChargeMainActivity.this.Button05, ChargeMainActivity.this.Str2Send5).show();
                    break;
            }
            return false;
        }
    };
    private View.OnClickListener ButtonClickListener = new View.OnClickListener() { // from class: com.example.main.MainActivity.2
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.ClearWindow) {
                ChargeMainActivity.this.RecDataView.setText("");
                return;
            }
            if (id == R.id.pauseButton) {
                if (BluetoothService.allowRec) {
                    ChargeMainActivity.this.pauseButton.setText("继续");
                } else {
                    ChargeMainActivity.this.pauseButton.setText("暂停");
                }
                BluetoothService.allowRec = !BluetoothService.allowRec;
                return;
            }
            if (id != R.id.sendButton) {
                switch (id) {
                    case R.id.Button01 /* 2130903042 */:
                        ChargeMainActivity.this.sendMessage(ChargeMainActivity.this.Button01, ChargeMainActivity.this.Str2Send1.toString());
                        break;
                    case R.id.Button02 /* 2130903043 */:
                        ChargeMainActivity.this.sendMessage(ChargeMainActivity.this.Button02, ChargeMainActivity.this.Str2Send2.toString());
                        break;
                    case R.id.Button03 /* 2130903044 */:
                        ChargeMainActivity.this.sendMessage(ChargeMainActivity.this.Button03, ChargeMainActivity.this.Str2Send3.toString());
                        break;
                    case R.id.Button04 /* 2130903045 */:
                        ChargeMainActivity.this.sendMessage(ChargeMainActivity.this.Button04, ChargeMainActivity.this.Str2Send4.toString());
                        break;
                    case R.id.Button05 /* 2130903046 */:
                        ChargeMainActivity.this.sendMessage(ChargeMainActivity.this.Button05, ChargeMainActivity.this.Str2Send5.toString());
                        break;
                }
                return;
            }
            ChargeMainActivity.this.sendMessage(ChargeMainActivity.this.sendButton, ChargeMainActivity.this.sendContent.getText().toString());
        }
    };
    private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.example.main.MainActivity.3
        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                String s = ChargeMainActivity.this.period.getText().toString();
                if (s.length() != 0) {
                    ChargeMainActivity.this.timeTask = ChargeMainActivity.this.new timeThread(Integer.valueOf(s).intValue());
                } else {
                    ChargeMainActivity.this.timeTask = ChargeMainActivity.this.new timeThread(1000);
                }
                ChargeMainActivity.this.timeTask.start();
                return;
            }
            ChargeMainActivity.this.timeTask.interrupt();
        }
    };
    private RadioGroup.OnCheckedChangeListener rgListener = new RadioGroup.OnCheckedChangeListener() { // from class: com.example.main.MainActivity.4
        @Override // android.widget.RadioGroup.OnCheckedChangeListener
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.receiveASCII /* 2130903094 */:
                    ChargeMainActivity.isHEXrec = false;
                    break;
                case R.id.receiveHEX /* 2130903095 */:
                    ChargeMainActivity.isHEXrec = true;
                    break;
                case R.id.sendASCII /* 2130903099 */:
                    ChargeMainActivity.isHEXsend = false;
                    break;
                case R.id.sendHEX /* 2130903102 */:
                    ChargeMainActivity.isHEXsend = true;
                    break;
            }
        }
    };
    timeThread timeTask = null;
    String[] hex_string_table = new String[256];
    private int align_num = 0;
    private final Handler mHandler = new Handler(Looper. myLooper()) { // from class: com.example.main.MainActivity.5

        /* renamed from: b */
        int f9b;

        /* renamed from: bs */
        byte[] f10bs;

        /* renamed from: i */
        int f11i;
        float sWidth;

        /* renamed from: sb */
        StringBuffer f12sb = new StringBuffer();
        int lineWidth = 0;
        int align_i = 0;

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 2) {
                this.f12sb.setLength(0);
                if (ChargeMainActivity.isHEXrec) {
                    this.f10bs = (byte[]) msg.obj;
                    this.f11i = 0;
                    while (this.f11i < msg.arg1) {
                        this.f9b = this.f10bs[this.f11i] & 255;
                        this.f12sb.append(ChargeMainActivity.this.hex_string_table[this.f9b]);
                        this.sWidth = ChargeMainActivity.this.RecDataView.getPaint().measureText(ChargeMainActivity.this.hex_string_table[this.f9b]);
                        this.lineWidth = (int) (this.lineWidth + this.sWidth);
                        if (this.lineWidth > ChargeMainActivity.this.RecDataView.getWidth() || (ChargeMainActivity.this.align_num != 0 && ChargeMainActivity.this.align_num == this.align_i)) {
                            this.lineWidth = (int) this.sWidth;
                            this.align_i = 0;
                            this.f12sb.insert(this.f12sb.length() - 3, '\n');
                        }
                        this.align_i++;
                        this.f11i++;
                    }
                } else {
                    this.f10bs = (byte[]) msg.obj;
                    char[] c = new char[msg.arg1];
                    this.f11i = 0;
                    while (this.f11i < msg.arg1) {
                        c[this.f11i] = (char) (this.f10bs[this.f11i] & 255);
                        this.sWidth = ChargeMainActivity.this.RecDataView.getPaint().measureText(c, this.f11i, 1);
                        this.lineWidth = (int) (this.lineWidth + this.sWidth);
                        if (this.lineWidth > ChargeMainActivity.this.RecDataView.getWidth()) {
                            this.lineWidth = (int) this.sWidth;
                            this.f12sb.append('\n');
                        }
                        if (c[this.f11i] == '\n') {
                            this.lineWidth = 0;
                        }
                        this.f12sb.append(c[this.f11i]);
                        this.f11i++;
                    }
                }
                ChargeMainActivity.this.RecDataView.append(this.f12sb);
            }
            switch (i) {
                case 4:
                    ChargeMainActivity.this.mConnectedDeviceName = msg.getData().getString("device name");
                    Toast.makeText(ChargeMainActivity.this.getApplicationContext(), "已连接到" + ChargeMainActivity.this.mConnectedDeviceName, 0).show();
                    ChargeMainActivity.this.setTitle("蓝牙串口助手(已连接)");
                    break;
                case 5:
                    if (ChargeMainActivity.this.mConnectedDeviceName != null) {
                        Toast.makeText(ChargeMainActivity.this.getApplicationContext(), "与" + ChargeMainActivity.this.mConnectedDeviceName + msg.getData().getString("toast"), 0).show();
                    } else {
                        Toast.makeText(ChargeMainActivity.this.getApplicationContext(), "与" + ChargeMainActivity.this.target_device_name + msg.getData().getString("toast"), 0).show();
                    }
                    ChargeMainActivity.this.setTitle("蓝牙串口助手(未连接)");
                    ChargeMainActivity.this.mConnectedDeviceName = null;
                    break;
                case 6:
                    Toast.makeText(ChargeMainActivity.this.getApplicationContext(), "", 0).show();
                    break;
            }
        }
    };
    private String target_device_name = null;

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != 0) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }
    }

    private void saveConfig() {
        String filename = Environment.getExternalStorageDirectory().getPath() + "/蓝牙串口助手.config";
        File f = new File(filename);
        try {
            f.createNewFile();
            FileOutputStream fOut = new FileOutputStream(f);
            fOut.write(this.Button01.getText().toString().getBytes());
            fOut.write(0);
            fOut.write(this.Str2Send1.toString().getBytes());
            fOut.write(0);
            fOut.write(this.Button02.getText().toString().getBytes());
            fOut.write(0);
            fOut.write(this.Str2Send2.toString().getBytes());
            fOut.write(0);
            fOut.write(this.Button03.getText().toString().getBytes());
            fOut.write(0);
            fOut.write(this.Str2Send3.toString().getBytes());
            fOut.write(0);
            fOut.write(this.Button04.getText().toString().getBytes());
            fOut.write(0);
            fOut.write(this.Str2Send4.toString().getBytes());
            fOut.write(0);
            fOut.write(this.Button05.getText().toString().getBytes());
            fOut.write(0);
            fOut.write(this.Str2Send5.toString().getBytes());
            fOut.write(0);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreConfig() {
        String filename = Environment.getExternalStorageDirectory().getPath() + "/蓝牙串口助手.config";
        File f = new File(filename);
        try {
            FileInputStream fIn = new FileInputStream(f);
            byte[] bs = new byte[1024];
            fIn.read(bs);
            String s = new String(bs);
            String[] ss = s.split("\u0000");
            this.Button01.setText(ss[0]);
            this.Str2Send1.append(ss[1]);
            this.Button02.setText(ss[2]);
            this.Str2Send2.append(ss[3]);
            this.Button03.setText(ss[4]);
            this.Str2Send3.append(ss[5]);
            this.Button04.setText(ss[6]);
            this.Str2Send4.append(ss[7]);
            this.Button05.setText(ss[8]);
            this.Str2Send5.append(ss[9]);
            fIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chongdianji);
        this.Button01 = (Button) findViewById(R.id.Button01);
        this.Button02 = (Button) findViewById(R.id.Button02);
        this.Button03 = (Button) findViewById(R.id.Button03);
        this.Button04 = (Button) findViewById(R.id.Button04);
        this.Button05 = (Button) findViewById(R.id.Button05);
        this.RecDataView = (TextView) findViewById(R.id.Rec_Text_show);
        this.ClearWindow = (Button) findViewById(R.id.ClearWindow);
        this.pauseButton = (Button) findViewById(R.id.pauseButton);
        this.sendContent = (EditText) findViewById(R.id.sendContent);
        this.period = (EditText) findViewById(R.id.period);
        this.sendButton = (Button) findViewById(R.id.sendButton);
        this.setPeriod = (CheckBox) findViewById(R.id.setPeriod);
        this.rgRec = (RadioGroup) findViewById(R.id.rgRec);
        this.rgSend = (RadioGroup) findViewById(R.id.rgSend);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        } else {
            setTitle("蓝牙串口助手(未连接)");
            init_hex_string_table();
            verifyStoragePermissions(this);
        }
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        if (!this.mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            startActivityForResult(enableIntent, 2);
        } else if (this.mConnectService == null) {
            this.mConnectService = new BluetoothService(this.mHandler);
        }
    }

    @Override // android.app.Activity
    public synchronized void onResume() {
        super.onResume();
        if (this.mConnectService != null && this.mConnectService.getState() == 0) {
            this.mConnectService.acceptWait();
        }
    }

    private void setupListener() {
        this.Button01.setOnClickListener(this.ButtonClickListener);
        this.Button02.setOnClickListener(this.ButtonClickListener);
        this.Button03.setOnClickListener(this.ButtonClickListener);
        this.Button04.setOnClickListener(this.ButtonClickListener);
        this.Button05.setOnClickListener(this.ButtonClickListener);
        this.Button01.setOnLongClickListener(this.ButtonLongClickListener);
        this.Button02.setOnLongClickListener(this.ButtonLongClickListener);
        this.Button03.setOnLongClickListener(this.ButtonLongClickListener);
        this.Button04.setOnLongClickListener(this.ButtonLongClickListener);
        this.Button05.setOnLongClickListener(this.ButtonLongClickListener);
        this.ClearWindow.setOnClickListener(this.ButtonClickListener);
        this.pauseButton.setOnClickListener(this.ButtonClickListener);
        this.sendButton.setOnClickListener(this.ButtonClickListener);
        this.setPeriod.setOnCheckedChangeListener(this.checkBoxListener);
        this.rgRec.setOnCheckedChangeListener(this.rgListener);
        this.rgSend.setOnCheckedChangeListener(this.rgListener);
    }

    @Override // android.app.Activity
    public synchronized void onPause() {
        super.onPause();
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.mConnectService != null) {
            this.mConnectService.cancelAllBtThread();
        }
        if (this.timeTask != null) {
            this.timeTask.interrupt();
        }
        saveConfig();

    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(Button callButton, String Str2Send) {
        if (callButton != null) {
            if (Str2Send.length() == 0) {
                if (callButton != this.sendButton) {
                    Toast.makeText(this, "请先长按配置按键", Toast.LENGTH_SHORT).show();
                    return;
                }
                return;
            } else if (this.mConnectService == null || this.mConnectService.getState() != 3) {
                Toast.makeText(this, "未连接到任何蓝牙设备", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (Str2Send == null || this.mConnectService == null || Str2Send.equals("")) {
            return;
        }
        if (!isHEXsend) {
            this.mConnectService.write(Str2Send.getBytes());
            return;
        }
        for (char c : Str2Send.toCharArray()) {
            if ((c < '0' || c > '9') && ((c < 'a' || c > 'f') && ((c < 'A' || c > 'F') && c != ' '))) {
                Toast.makeText(this, "发送内容含非法字符", 0).show();
                return;
            }
        }
        String[] ss = Str2Send.split(" ");
        byte[] bs = new byte[1];
        for (String s : ss) {
            if (s.length() != 0) {
                bs[0] = (byte) Integer.valueOf(s, 16).intValue();
                this.mConnectService.write(bs);
            }
        }
    }

    private class timeThread extends Thread {
        private int sleeptime;

        timeThread(int militime) {
            this.sleeptime = militime;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (!isInterrupted()) {
                ChargeMainActivity.this.sendMessage(null, ChargeMainActivity.this.sendContent.getText().toString());
                try {
                    Thread.sleep(this.sleeptime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void init_hex_string_table() {
        for (int i = 0; i < 256; i++) {
            if (i < 16) {
                this.hex_string_table[i] = " 0" + Integer.toHexString(i).toUpperCase();
            } else {
                this.hex_string_table[i] = " " + Integer.toHexString(i).toUpperCase();
            }
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    String address = data.getExtras().getString(DeviceListActivity.DEVICE_ADDRESS);
                    BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
                    this.target_device_name = device.getName();
                    if (this.target_device_name.equals(this.mConnectedDeviceName)) {
                        Toast.makeText(this, "已连接" + this.mConnectedDeviceName, 0).show();
                        break;
                    } else {
                        Toast.makeText(this, "正在连接" + this.target_device_name, 0).show();
                        this.mConnectService.connect(device);
                        break;
                    }
                }
                break;
            case 2:
                if (resultCode == -1) {
                    this.mConnectService = new BluetoothService(this.mHandler);
                    break;
                } else {
                    Toast.makeText(this, "拒绝打开蓝牙", 0).show();
                    break;
                }
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mian, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.Connect) {
            if (!this.mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
                startActivityForResult(enableIntent, 2);
                return true;
            }
            Intent serverIntent = new Intent(this, (Class<?>) DeviceListActivity.class);
            startActivityForResult(serverIntent, 1);
            return true;
        }
        if (itemId == R.id.Upgrate_FW) {
            new setAlignDialog(this, new setAlignDialog.DialogCallback() { // from class: com.example.main.MainActivity.6
                @Override // com.example.main.setAlignDialog.DialogCallback
                public void DialogReturn(int i) {
                    ChargeMainActivity.this.align_num = i;
                }
            }).show();
            return false;
        }
        if (itemId == R.id.autoRead_menu) {
            if (this.mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
                discoverableIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 300);
                startActivity(discoverableIntent);
            }
            return true;
        }
        return false;
    }
}