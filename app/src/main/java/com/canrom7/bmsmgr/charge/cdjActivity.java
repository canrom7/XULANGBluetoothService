package com.canrom7.bmsmgr.charge;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.canrom7.bmsmgr.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class cdjActivity extends Activity {


    public static final int FLAG_KEY_MEDIA_FAST_FORWARD=64;
    public static final int FLAG_KEY_MEDIA_NEXT=128;
    public static final int FLAG_KEY_MEDIA_PAUSE=16;
    public static final int FLAG_KEY_MEDIA_PLAY=4;
    public static final int FLAG_KEY_MEDIA_PLAY_PAUSE=8;
    public static final int FLAG_KEY_MEDIA_PREVIOUS=1;
    public static final int FLAG_KEY_MEDIA_REWIND=2;
    public static final int FLAG_KEY_MEDIA_STOP=32;
    public static final int KEYCODE_MEDIA_PAUSE=127;
    public static final int KEYCODE_MEDIA_PLAY=126;
    public static final int KEYCODE_MEDIA_RECORD=130;


    public static final int BT_TOAST=5;
    public static final int CONNECTED_DEVICE_NAME=4;
    private static final boolean DEBUG=false;
    public static final String DEVICE_NAME="device name";
    public static final int MAIN_TOAST=6;
    private static String[] PERMISSIONS_STORAGE={"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public static final int REC_DATA=2;
    private static final int REQUEST_CONNECT_DEVICE=1;
    private static final int REQUEST_ENABLE_BT=2;
    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private static final int REQUEST_GET_FIRMWARE_FILE=3;
    public static final int SETPERCENRAGE=10;
    private static final String TAG="cdjActivity";
    public static final String TOAST="toast";
    public static final int UPGRAUPSTATUS=9;
    private TextView AH_Text;
    private Button AHclear_Button;
    private Button ChargerClose_I_set_Button;
    private TextView ChargerClose_I_view;
    private Button CurrCalibration_button;
    private int Curr_I;
    private int Curr_U;
    private int Iout_ALL;
    private TextView Iout_ALL_TextView;
    private TextView Iout_S_TextView;
    private int Iout_lim_s;
    private int Iout_meas_s;
    private Button ONOFF_Button;
    private int ONOFF_statue;
    private TextView Out_statue_TextView;
    private byte PM_num;
    private TextView PM_num_TextView;
    private int Pout_ALL;
    private TextView Pout_ALL_TextView;
    private TextView Pout_S_TextView;
    private int Pout_meas_s;
    private int Rated_I;
    private TextView RealI_View;
    private TextView RealU_View;
    private int RunTime;
    private TextView RunTime_TextView;
    private int RunTime_s;
    private TextView Start_ONOFF_View;
    private Button VoltCalibration_button;
    private TextView Vout_S_TextView;
    private int Vout_meas_s;
    private TextView WH_Text;
    private Button WHclear_Button;
    private MenuItem autoRead_menu;
    Button buttonUpgrateFirmware;
    Button buttonselectFirmwareFile;
    private byte cdj_State;
    private TextView chargingInfo_View;
    private int charging_AH;
    private int charging_WH;
    private int charging_cutoff_I;
    private int charging_cutoff_Time;
    private int currCharging_Time;
    private TextView currCurrCalibration_View;
    private int currCurrCalibration_value;
    int currFW_Type;
    private int currRunModeType;
    private TextView currVoltCalibration_View;
    private int currVoltCalibration_value;
    private int curr_STARTONOFF;
    private Button defultSet_Button;
    EditText firmwareFile;
    private TextView label3;
    ProgressBar mProgressBar;
    private TextView outInfo_View;
    //接收数据次数
    private int recDataNum;
    private TextView recDataNumView;
    private Button save_Button;
    private Button setBleName_Button;
    private TextView setBleName_Text;
    private TextView setChargerClose_I_Text;
    private TextView setCharger_Time_Text;
    private TextView setCharger_Time_view;
    private TextView setCurrCalibration_Text;
    private int setCurrCalibration_value;
    private int setCurr_I;
    private int setCurr_U;
    private Button setKey_Button;
    private TextView setKey_Text;
    private TextView setRealI_Text;
    private Button setRealI_button;
    private TextView setRealU_Text;
    private Button setRealU_button;
    private TextView setStart_ONOFF_Text;
    private Button setStart_ONOFF_button;
    private TextView setVoltCalibration_Text;
    private int setVoltCalibration_value;
    private Button set_Charger_Time_Button;
    private int set_STARTONOFF;
    TextView upgrateStatus;
    public boolean BT_connet=false;
    public boolean doIAP=false;
    public boolean autoRead=false;
    int[] crc16tab={0, 4129, 8258, 12387, 16516, 20645, 24774, 28903, 33032, 37161, 41290, 45419, 49548, 53677, 57806, 61935, 4657, 528, 12915, 8786, 21173, 17044, 29431, 25302, 37689, 33560, 45947, 41818, 54205, 50076, 62463, 58334, 9314, 13379, 1056, 5121, 25830, 29895, 17572, 21637, 42346, 46411, 34088, 38153, 58862, 62927, 50604, 54669, 13907, 9842, 5649, 1584, 30423, 26358, 22165, 18100, 46939, 42874, 38681, 34616, 63455, 59390, 55197, 51132, 18628, 22757, 26758, 30887, 2112, 6241, 10242, 14371, 51660, 55789, 59790, 63919, 35144, 39273, 43274, 47403, 23285, 19156, 31415, 27286, 6769, 2640, 14899, 10770, 56317, 52188, 64447, 60318, 39801, 35672, 47931, 43802, 27814, 31879, 19684, 23749, 11298, 15363, 3168, 7233, 60846, 64911, 52716, 56781, 44330, 48395, 36200, 40265, 32407, 28342, 24277, 20212, 15891, 11826, 7761, 3696, 65439, 61374, 57309, 53244, 48923, 44858, 40793, 36728, 37256, 33193, 45514, 41451, 53516, 49453, 61774, 57711, 4224, 161, 12482, 8419, 20484, 16421, 28742, 24679, 33721, 37784, 41979, 46042, 49981, 54044, 58239, 62302, 689, 4752, 8947, 13010, 16949, 21012, 25207, 29270, 46570, 42443, 38312, 34185, 62830, 58703, 54572, 50445, 13538, 9411, 5280, 1153, 29798, 25671, 21540, 17413, 42971, 47098, 34713, 38840, 59231, 63358, 50973, 55100, 9939, 14066, 1681, 5808, 26199, 30326, 17941, 22068, 55628, 51565, 63758, 59695, 39368, 35305, 47498, 43435, 22596, 18533, 30726, 26663, 6336, 2273, 14466, 10403, 52093, 56156, 60223, 64286, 35833, 39896, 43963, 48026, 19061, 23124, 27191, 31254, 2801, 6864, 10931, 14994, 64814, 60687, 56684, 52557, 48554, 44427, 40424, 36297, 31782, 27655, 23652, 19525, 15522, 11395, 7392, 3265, 61215, 65342, 53085, 57212, 44955, 49082, 36825, 40952, 28183, 32310, 20053, 24180, 11923, 16050, 3793, 7920};
    boolean CheckOk=false;
    boolean EraseOk=false;
    boolean WriteDataOk=false;
    boolean WriteInfoOk=false;
    boolean ExcuteOk=false;
    private boolean S_reply=false;
    byte[] cmd1={126, 6, 10, -56, -27, 13};
    byte[] cmd2={126, 6, 20, 59, 26, 13};
    byte[] cmd4={126, 6, 26, -38, -44, 13};
    byte[] cmd11={126, 6, 11, -40, -60, 13};
    byte[] cmd0={126, 6, 16, 123, -98, 13};
    byte[] cmd5={126, 6, 27, -54, -11, 13};
    byte[] cmd3={126, 7, 14, 1, 86, -111, 13};
    ArrayList< Integer > recDatalist=new ArrayList<>();
    // 查询结果集合
    LinkedList< Integer > Q_list=new LinkedList<>();
    // 命令集合
    LinkedList< Integer[] > cmd_list=new LinkedList<>();
    private BluetoothAdapter mBluetoothAdapter=null;
    private BluetoothService mConnectService=null;
    private String mConnectedDeviceName=null;
    private long exitTime=0;
    private RadioGroup.OnCheckedChangeListener rgListener=new RadioGroup.OnCheckedChangeListener() { // from class: com.example.main.cdjActivity.1
        @Override // android.widget.RadioGroup.OnCheckedChangeListener
        public void onCheckedChanged(RadioGroup group, int checkedId) {
        }
    };
    IAPThread iapTask=null;
    private View.OnClickListener ButtonClickListener=new View.OnClickListener() { // from class: com.example.main.cdjActivity.2
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.AHclear_Button /* 2130903041 */:
                    Integer[] ahc={Integer.valueOf(KEYCODE_MEDIA_PLAY), 7, 22, 0, 204, 106, 13};
                    cdjActivity.this.cmd_list.addLast(ahc);
                    cdjActivity.this.AHclear_Button.setEnabled(false);
                    break;
                case R.id.ChargerClose_I_set_Button /* 2130903047 */:
                    byte[] data=new byte[10];
                    int c1=(int) (Double.parseDouble(cdjActivity.this.setChargerClose_I_Text.getText().toString()) * 100.0d);
                    if (c1 > 1000) {
                        Toast.makeText(cdjActivity.this.getApplicationContext(), "范围0-10", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        data[0]=126;
                        data[1]=10;
                        data[2]=23;
                        data[3]=(byte) ((c1 >> 24) & 255);
                        data[4]=(byte) ((c1 >> 16) & 255);
                        data[5]=(byte) ((c1 >> 8) & 255);
                        data[6]=(byte) ((c1 >> 0) & 255);
                        int crc2=cdjActivity.this.crc16_ccitt(data, data.length - 3);
                        data[7]=(byte) ((65280 & crc2) >> 8);
                        data[8]=(byte) (crc2 & 255);
                        data[9]=13;
                        Integer[] int_data=new Integer[data.length];
                        for (int i=0; i < data.length; i++) {
                            int_data[i]=Integer.valueOf(data[i] & 255);
                        }
                        cdjActivity.this.cmd_list.addLast(int_data);
                        cdjActivity.this.ChargerClose_I_set_Button.setEnabled(false);
                        break;
                    }
                case R.id.CurrCalibration_button /* 2130903051 */:
                    cdjActivity.this.setCurrCalibration_value=(int) Double.parseDouble(cdjActivity.this.setCurrCalibration_Text.getText().toString());
                    int crc22=cdjActivity.this.crc16_ccitt(data, data.length - 3);
                    byte[] data2={126, 10, 18, (byte) ((cdjActivity.this.setCurrCalibration_value >> 24) & 255), (byte) ((cdjActivity.this.setCurrCalibration_value >> 16) & 255), (byte) ((cdjActivity.this.setCurrCalibration_value >> 8) & 255), (byte) ((cdjActivity.this.setCurrCalibration_value >> 0) & 255), (byte) ((65280 & crc22) >> 8), (byte) (crc22 & 255), 13};
                    Integer[] int_data2=new Integer[data2.length];
                    for (int i2=0; i2 < data2.length; i2++) {
                        int_data2[i2]=Integer.valueOf(data2[i2] & 255);
                    }
                    cdjActivity.this.cmd_list.addLast(int_data2);
                    cdjActivity.this.CurrCalibration_button.setEnabled(false);
                    break;
                case R.id.ONOFF_Button /* 2130903054 */:
                    if (cdjActivity.this.ONOFF_statue == 0) {
                        Toast.makeText(cdjActivity.this.getApplicationContext(), "正在打开输出", Toast.LENGTH_LONG).show();
                        Integer[] data1={Integer.valueOf(KEYCODE_MEDIA_PLAY), 7, 14, 1, 86, 145, 13};
                        cdjActivity.this.cmd_list.addLast(data1);
                    } else {
                        Toast.makeText(cdjActivity.this.getApplicationContext(), "正在关闭输出", Toast.LENGTH_LONG).show();
                        Integer[] data22={Integer.valueOf(KEYCODE_MEDIA_PLAY), 7, 14, 0, 70, 176, 13};
                        cdjActivity.this.cmd_list.addLast(data22);
                    }
                    cdjActivity.this.ONOFF_Button.setEnabled(false);
                    break;
                case R.id.VoltCalibration_button /* 2130903066 */:
                    cdjActivity.this.setVoltCalibration_value=(int) Double.parseDouble(cdjActivity.this.setVoltCalibration_Text.getText().toString());
                    int crc23=cdjActivity.this.crc16_ccitt(data, data.length - 3);
                    byte[] data3={126, 10, 19, (byte) ((cdjActivity.this.setVoltCalibration_value >> 24) & 255), (byte) ((cdjActivity.this.setVoltCalibration_value >> 16) & 255), (byte) ((cdjActivity.this.setVoltCalibration_value >> 8) & 255), (byte) ((cdjActivity.this.setVoltCalibration_value >> 0) & 255), (byte) ((65280 & crc23) >> 8), (byte) (crc23 & 255), 13};
                    Integer[] int_data3=new Integer[data3.length];
                    for (int i3=0; i3 < data3.length; i3++) {
                        int_data3[i3]=Integer.valueOf(data3[i3] & 255);
                    }
                    cdjActivity.this.cmd_list.addLast(int_data3);
                    cdjActivity.this.VoltCalibration_button.setEnabled(false);
                    break;
                case R.id.WHclear_Button /* 2130903069 */:
                    Integer[] whc={Integer.valueOf(KEYCODE_MEDIA_PLAY), 7, 22, 1, 220, 75, 13};
                    cdjActivity.this.cmd_list.addLast(whc);
                    cdjActivity.this.WHclear_Button.setEnabled(false);
                    break;
                case R.id.buttonSelectFirmwareFile /* 2130903075 */:
                    cdjActivity.this.showFileChooser();
                    break;
                case R.id.buttonUpgrateFirmware /* 2130903076 */:
                    cdjActivity.this.doIAP=true;
                    cdjActivity.this.iapTask=cdjActivity.this.new IAPThread(200);
                    cdjActivity.this.iapTask.start();
                    cdjActivity.this.buttonselectFirmwareFile.setEnabled(false);
                    cdjActivity.this.buttonUpgrateFirmware.setEnabled(false);
                    break;
                case R.id.defultSet_Button /* 2130903083 */:
                    Toast.makeText(cdjActivity.this.getApplicationContext(), "正在恢复出厂设置", Toast.LENGTH_LONG).show();
                    Integer[] data23={Integer.valueOf(KEYCODE_MEDIA_PLAY), 6, 31, 138, 113, 13};
                    cdjActivity.this.cmd_list.addLast(data23);
                    cdjActivity.this.defultSet_Button.setEnabled(false);
                    break;
                case R.id.save_Button /* 2130903098 */:
                    Toast.makeText(cdjActivity.this.getApplicationContext(), "正在保存设置", Toast.LENGTH_LONG).show();
                    if (cdjActivity.this.set_STARTONOFF == 0) {
                        Integer[] data24={Integer.valueOf(KEYCODE_MEDIA_PLAY), 7, 15, 0, 117, 129, 13};
                        cdjActivity.this.cmd_list.addLast(data24);
                    } else {
                        Integer[] data25={Integer.valueOf(KEYCODE_MEDIA_PLAY), 7, 15, 1, 101, 160, 13};
                        cdjActivity.this.cmd_list.addLast(data25);
                    }
                    cdjActivity.this.save_Button.setEnabled(false);
                    break;
                case R.id.setBleName_Button /* 2130903103 */:
                    if (cdjActivity.this.setBleName_Text.getText().toString().length() >= 4 && cdjActivity.this.setBleName_Text.getText().toString().length() <= 10) {
                        String str=cdjActivity.this.setBleName_Text.getText().toString();
                        byte[] nbs=str.getBytes();
                        byte[] data4=new byte[6 + nbs.length];
                        data4[0]=126;
                        data4[1]=(byte) (6 + nbs.length);
                        data4[2]=27;
                        for (int i4=0; i4 < nbs.length; i4++) {
                            data4[3 + i4]=(byte) (nbs[i4] & 255);
                        }
                        int crc24=cdjActivity.this.crc16_ccitt(data4, data4.length - 3);
                        data4[data4.length - 3]=(byte) ((65280 & crc24) >> 8);
                        data4[data4.length - 2]=(byte) (crc24 & 255);
                        data4[data4.length - 1]=13;
                        Integer[] int_data4=new Integer[data4.length];
                        for (int i5=0; i5 < data4.length; i5++) {
                            int_data4[i5]=Integer.valueOf(data4[i5] & 255);
                        }
                        cdjActivity.this.cmd_list.addLast(int_data4);
                        cdjActivity.this.setBleName_Button.setEnabled(false);
                        break;
                    } else {
                        Toast.makeText(cdjActivity.this.getApplicationContext(), "数字和字母，长度5-10", Toast.LENGTH_LONG).show();
                        break;
                    }
                    break;
                case R.id.setKey_Button /* 2130903109 */:
                    if (cdjActivity.this.setKey_Button.getText().toString().length() == 4) {
                        cdjActivity.this.setKey_Text.getText().toString();
                        byte[] key="".getBytes();
                        byte[] data5=new byte[10];
                        data5[0]=126;
                        data5[1]=10;
                        data5[2]=28;
                        for (int i6=0; i6 < key.length; i6++) {
                            data5[3 + i6]=(byte) (key[i6] & 255);
                        }
                        int crc25=cdjActivity.this.crc16_ccitt(data5, data5.length - 3);
                        data5[data5.length - 3]=(byte) ((65280 & crc25) >> 8);
                        data5[data5.length - 2]=(byte) (crc25 & 255);
                        data5[data5.length - 1]=13;
                        Integer[] int_data5=new Integer[data5.length];
                        for (int i7=0; i7 < data5.length; i7++) {
                            int_data5[i7]=Integer.valueOf(data5[i7] & 255);
                        }
                        cdjActivity.this.cmd_list.addLast(int_data5);
                        cdjActivity.this.setKey_Button.setEnabled(false);
                        break;
                    } else {
                        Toast.makeText(cdjActivity.this.getApplicationContext(), "4位数字密码", Toast.LENGTH_LONG).show();
                        break;
                    }
                case R.id.setRealI_button /* 2130903113 */:
                    cdjActivity.this.setCurr_I=(int) (Double.parseDouble(cdjActivity.this.setRealI_Text.getText().toString()) * 1000.0d);
                    int crc26=cdjActivity.this.crc16_ccitt(data, data.length - 3);
                    byte[] data6={126, 10, 13, (byte) ((cdjActivity.this.setCurr_I >> 24) & 255), (byte) ((cdjActivity.this.setCurr_I >> 16) & 255), (byte) ((cdjActivity.this.setCurr_I >> 8) & 255), (byte) ((cdjActivity.this.setCurr_I >> 0) & 255), (byte) ((65280 & crc26) >> 8), (byte) (crc26 & 255), 13};
                    Integer[] int_data6=new Integer[data6.length];
                    for (int i8=0; i8 < data6.length; i8++) {
                        int_data6[i8]=Integer.valueOf(data6[i8] & 255);
                    }
                    cdjActivity.this.cmd_list.addLast(int_data6);
                    cdjActivity.this.setRealI_button.setEnabled(false);
                    break;
                case R.id.setRealU_button /* 2130903115 */:
                    cdjActivity.this.setCurr_U=(int) (Double.parseDouble(cdjActivity.this.setRealU_Text.getText().toString()) * 1000.0d);
                    int crc27=cdjActivity.this.crc16_ccitt(data, data.length - 3);
                    byte[] data7={126, 10, 12, (byte) ((cdjActivity.this.setCurr_U >> 24) & 255), (byte) ((cdjActivity.this.setCurr_U >> 16) & 255), (byte) ((cdjActivity.this.setCurr_U >> 8) & 255), (byte) ((cdjActivity.this.setCurr_U >> 0) & 255), (byte) ((65280 & crc27) >> 8), (byte) (crc27 & 255), 13};
                    Integer[] int_data7=new Integer[data7.length];
                    for (int i9=0; i9 < data7.length; i9++) {
                        int_data7[i9]=Integer.valueOf(data7[i9] & 255);
                    }
                    cdjActivity.this.cmd_list.addLast(int_data7);
                    cdjActivity.this.setRealU_button.setEnabled(false);
                    break;
                case R.id.setStart_ONOFF_button /* 2130903117 */:
                    byte[] data8=new byte[7];
                    int tim1=(int) Double.parseDouble(cdjActivity.this.setStart_ONOFF_Text.getText().toString());
                    if (tim1 > 2) {
                        Toast.makeText(cdjActivity.this.getApplicationContext(), "0-关闭，1-打开，2-充电", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        data8[0]=126;
                        data8[1]=7;
                        data8[2]=21;
                        data8[3]=(byte) (tim1 & 255);
                        int crc28=cdjActivity.this.crc16_ccitt(data8, data8.length - 3);
                        data8[4]=(byte) ((65280 & crc28) >> 8);
                        data8[5]=(byte) (crc28 & 255);
                        data8[6]=13;
                        Integer[] int_data8=new Integer[data8.length];
                        for (int i10=0; i10 < data8.length; i10++) {
                            int_data8[i10]=Integer.valueOf(data8[i10] & 255);
                        }
                        cdjActivity.this.cmd_list.addLast(int_data8);
                        cdjActivity.this.setStart_ONOFF_button.setEnabled(false);
                        break;
                    }
                case R.id.set_Charger_Time_Button /* 2130903119 */:
                    byte[] data9=new byte[10];
                    int tim12=(int) (Double.parseDouble(cdjActivity.this.setCharger_Time_Text.getText().toString()) * 60.0d);
                    if (tim12 > 60000) {
                        Toast.makeText(cdjActivity.this.getApplicationContext(), "范围0-1000", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        data9[0]=126;
                        data9[1]=10;
                        data9[2]=24;
                        data9[3]=(byte) ((tim12 >> 24) & 255);
                        data9[4]=(byte) ((tim12 >> 16) & 255);
                        data9[5]=(byte) ((tim12 >> 8) & 255);
                        data9[6]=(byte) ((tim12 >> 0) & 255);
                        int crc29=cdjActivity.this.crc16_ccitt(data9, data9.length - 3);
                        data9[7]=(byte) ((65280 & crc29) >> 8);
                        data9[8]=(byte) (crc29 & 255);
                        data9[9]=13;
                        Integer[] int_data9=new Integer[data9.length];
                        for (int i11=0; i11 < data9.length; i11++) {
                            int_data9[i11]=Integer.valueOf(data9[i11] & 255);
                        }
                        cdjActivity.this.cmd_list.addLast(int_data9);
                        cdjActivity.this.set_Charger_Time_Button.setEnabled(false);
                        break;
                    }
            }
        }
    };
    timeThread timeTask=null;
    String[] hex_string_table=new String[256];
    private final Handler mHandler=new Handler() { // from class: com.example.main.cdjActivity.3
        /**
         * 处理消息的主要方法
         *
         * @param msg 要处理的消息
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    handleCase2(msg);
                    break;
                case 4:
                    handleCase4(msg);
                    break;
                case 5:
                    handleCase5(msg);
                    break;
                case 9:
                    handleCase9(msg);
                    break;
                case 10:
                    handleCase10(msg);
                    break;
                default:
                    // 处理未知的消息类型
                    break;
            }
        }

        /**
         * 处理case 2的消息
         *
         * @param msg 包含数据的消息
         */
        private void handleCase2(Message msg) {
            byte[] data=(byte[]) msg.obj;
            int[] s_data=null;
            for (int i=0; i < msg.arg1; i++) {
                cdjActivity.updateReadCount(cdjActivity.this);
                cdjActivity.this.Q_list.addLast(Integer.valueOf(data[i] & 255));
                if (cdjActivity.this.Q_list.getFirst().intValue() == 126 && cdjActivity.this.Q_list.getLast().intValue() == 13) {
                    if (cdjActivity.this.Q_list.get(1).intValue() == cdjActivity.this.Q_list.size()) {
                        s_data=new int[cdjActivity.this.Q_list.size()];
                        for (int j=0; j < s_data.length; j++) {
                            s_data[j]=cdjActivity.this.Q_list.removeFirst().intValue();
                        }
                        cdjActivity.this.read_CDJData(s_data);
                        cdjActivity.this.S_reply=true;
                    }
                } else if (cdjActivity.this.Q_list.getFirst().intValue() != 126) {
                    cdjActivity.this.Q_list.clear();
                    cdjActivity.this.S_reply=true;
                } else if (cdjActivity.this.Q_list.size() > 45) {
                    cdjActivity.this.Q_list.clear();
                    cdjActivity.this.S_reply=true;
                }
            }
            cdjActivity.this.recDataNumView.setText("输出电压电流设置   " + String.valueOf(cdjActivity.this.recDataNum));
        }

        /**
         * 处理case 4的消息
         *
         * @param msg 包含设备名称的消息
         */
        private void handleCase4(Message msg) {
            cdjActivity.this.mConnectedDeviceName=msg.getData().getString("device name");
            Toast.makeText(cdjActivity.this.getApplicationContext(), "已连接到" + cdjActivity.this.mConnectedDeviceName, 0).show();
            cdjActivity.this.setTitle("蓝牙充电机(已连接)");
            cdjActivity.this.cmd_list.clear();
            cdjActivity.this.Q_list.clear();
            cdjActivity.this.cdj_State=(byte) 0;
            cdjActivity.this.BT_connet=true;
            if (cdjActivity.this.timeTask == null) {
                cdjActivity.this.timeTask=cdjActivity.this.new timeThread(200);
                cdjActivity.this.timeTask.start();
            }
        }

        /**
         * 处理case 5的消息
         *
         * @param msg 包含连接状态的消息
         */
        private void handleCase5(Message msg) {
            if (cdjActivity.this.mConnectedDeviceName != null) {
                Toast.makeText(cdjActivity.this.getApplicationContext(), "与" + cdjActivity.this.mConnectedDeviceName + msg.getData().getString("toast")
                        , Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(cdjActivity.this.getApplicationContext(), "与" + cdjActivity.this.target_device_name + msg.getData().getString("toast")
                        , Toast.LENGTH_SHORT).show();
            }
            cdjActivity.this.setTitle("蓝牙充电机(未连接)");
            cdjActivity.this.BT_connet=false;
            cdjActivity.this.mConnectedDeviceName=null;
            disableButtons();
        }

        /**
         * 禁用所有相关按钮
         */
        private void disableButtons() {
            cdjActivity.this.WHclear_Button.setEnabled(false);
            cdjActivity.this.AHclear_Button.setEnabled(false);
            cdjActivity.this.ChargerClose_I_set_Button.setEnabled(false);
            cdjActivity.this.set_Charger_Time_Button.setEnabled(false);
            cdjActivity.this.setRealU_button.setEnabled(false);
            cdjActivity.this.setRealI_button.setEnabled(false);
            cdjActivity.this.save_Button.setEnabled(false);
            cdjActivity.this.setStart_ONOFF_button.setEnabled(false);
            cdjActivity.this.VoltCalibration_button.setEnabled(false);
            cdjActivity.this.CurrCalibration_button.setEnabled(false);
            cdjActivity.this.defultSet_Button.setEnabled(false);
        }


        /**
         * 处理case 9的消息
         *
         * @param msg 包含更新状态的消息
         */
        private void handleCase9(Message msg) {
            String data=(String) msg.obj;
            cdjActivity.this.upgrateStatus.setText(data);
        }

        /**
         * 处理case 10的消息
         *
         * @param msg 包含进度值的消息
         */
        private void handleCase10(Message msg) {
            int value=((Integer) msg.obj).intValue();
            cdjActivity.this.mProgressBar.setProgress(value);
        }


    };
    private String target_device_name=null;

    static int updateReadCount(cdjActivity x0) {
        int i=x0.recDataNum;
        x0.recDataNum=i + 1;
        return i;
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission=ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != 0) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chongdianji);
        this.autoRead_menu=(MenuItem) findViewById(R.id.autoRead_menu);
        this.outInfo_View=(TextView) findViewById(R.id.outInfo_View);
        this.recDataNumView=(TextView) findViewById(R.id.textView6);
        this.chargingInfo_View=(TextView) findViewById(R.id.chargingInfo_View);
        this.Vout_S_TextView=(TextView) findViewById(R.id.Vout_S_Text);
        this.Iout_S_TextView=(TextView) findViewById(R.id.Iout_S_Text);
        this.Pout_S_TextView=(TextView) findViewById(R.id.Pout_S_Text);
        this.Iout_ALL_TextView=(TextView) findViewById(R.id.Iout_ALL_Text);
        this.PM_num_TextView=(TextView) findViewById(R.id.PM_num_Text);
        this.Out_statue_TextView=(TextView) findViewById(R.id.Out_statue_Text);
        this.Pout_ALL_TextView=(TextView) findViewById(R.id.Pout_ALL_Text);
        this.RunTime_TextView=(TextView) findViewById(R.id.RunTime_Text);
        this.RealU_View=(TextView) findViewById(R.id.RealU_View);
        this.RealI_View=(TextView) findViewById(R.id.RealI_View);
        this.currVoltCalibration_View=(TextView) findViewById(R.id.currVoltCalibration_View);
        this.currCurrCalibration_View=(TextView) findViewById(R.id.currCurrCalibration_View);
        this.Start_ONOFF_View=(TextView) findViewById(R.id.Start_ONOFF_View);
        this.setRealU_button=(Button) findViewById(R.id.setRealU_button);
        this.setRealI_button=(Button) findViewById(R.id.setRealI_button);
        this.VoltCalibration_button=(Button) findViewById(R.id.VoltCalibration_button);
        this.CurrCalibration_button=(Button) findViewById(R.id.CurrCalibration_button);
        this.setStart_ONOFF_button=(Button) findViewById(R.id.setStart_ONOFF_button);
        this.defultSet_Button=(Button) findViewById(R.id.defultSet_Button);
        this.setBleName_Button=(Button) findViewById(R.id.setBleName_Button);
        this.setKey_Button=(Button) findViewById(R.id.setKey_Button);
        this.WHclear_Button=(Button) findViewById(R.id.WHclear_Button);
        this.AHclear_Button=(Button) findViewById(R.id.AHclear_Button);
        this.ChargerClose_I_set_Button=(Button) findViewById(R.id.ChargerClose_I_set_Button);
        this.set_Charger_Time_Button=(Button) findViewById(R.id.set_Charger_Time_Button);
        this.ONOFF_Button=(Button) findViewById(R.id.ONOFF_Button);
        this.save_Button=(Button) findViewById(R.id.save_Button);
        this.setRealU_Text=(TextView) findViewById(R.id.setRealU_Text);
        this.setRealI_Text=(TextView) findViewById(R.id.setRealI_Text);
        this.setVoltCalibration_Text=(TextView) findViewById(R.id.setVoltCalibration_Text);
        this.setCurrCalibration_Text=(TextView) findViewById(R.id.setCurrCalibration_Text);
        this.setStart_ONOFF_Text=(TextView) findViewById(R.id.setStart_ONOFF_Text);
        this.AH_Text=(TextView) findViewById(R.id.AH_Text);
        this.WH_Text=(TextView) findViewById(R.id.WH_Text);
        this.setCharger_Time_Text=(TextView) findViewById(R.id.setCharger_Time_Text);
        this.setChargerClose_I_Text=(TextView) findViewById(R.id.setChargerClose_I_Text);
        this.setBleName_Text=(TextView) findViewById(R.id.setBleName_Text);
        this.setKey_Text=(TextView) findViewById(R.id.setKey_Text);
        this.ChargerClose_I_view=(TextView) findViewById(R.id.ChargerClose_I_view);
        this.setCharger_Time_view=(TextView) findViewById(R.id.setCharger_Time_view);
        this.label3=(TextView) findViewById(R.id.textView3);
        setupListener();
        this.mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setTitle("蓝牙充电机(未连接)");
        this.ONOFF_Button.setEnabled(false);
        this.VoltCalibration_button.setEnabled(false);
        this.CurrCalibration_button.setEnabled(false);
        this.save_Button.setEnabled(false);
        this.WHclear_Button.setEnabled(false);
        this.AHclear_Button.setEnabled(false);
        this.ChargerClose_I_set_Button.setEnabled(false);
        this.set_Charger_Time_Button.setEnabled(false);
        this.setRealU_button.setEnabled(false);
        this.setRealI_button.setEnabled(false);
        this.setStart_ONOFF_button.setEnabled(false);
        this.defultSet_Button.setEnabled(false);
        this.setBleName_Button.setEnabled(false);
        this.setKey_Button.setEnabled(false);
        this.Vout_S_TextView.setEnabled(false);
        this.Iout_S_TextView.setEnabled(false);
        this.Pout_S_TextView.setEnabled(false);
        this.Iout_ALL_TextView.setEnabled(false);
        this.PM_num_TextView.setEnabled(false);
        this.Out_statue_TextView.setEnabled(false);
        this.Pout_ALL_TextView.setEnabled(false);
        this.RunTime_TextView.setEnabled(false);
        this.AH_Text.setEnabled(false);
        this.WH_Text.setEnabled(false);
        this.setRealU_Text.setInputType(8194);
        this.setRealI_Text.setInputType(8194);
        this.setChargerClose_I_Text.setInputType(8194);
        this.setCharger_Time_Text.setInputType(2);
        this.setStart_ONOFF_Text.setInputType(2);
        this.setVoltCalibration_Text.setInputType(2);
        this.setCurrCalibration_Text.setInputType(2);
        init_hex_string_table();
        verifyStoragePermissions(this);
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        if (!this.mBluetoothAdapter.isEnabled()) {
            Intent enableIntent=new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            startActivityForResult(enableIntent, 2);
        } else if (this.mConnectService == null) {
            this.mConnectService=new BluetoothService(this.mHandler);
        }
    }

    @Override // android.app.Activity
    public synchronized void onResume() {
        super.onResume();
        if (this.mConnectService != null && this.mConnectService.getState() == 0) {
            this.mConnectService.acceptWait();
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (System.currentTimeMillis() - this.exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", 0).show();
            this.exitTime=System.currentTimeMillis();
            return true;
        }
        finish();
        System.exit(0);
        return true;
    }

    private void setupListener() {
        this.setRealU_button.setOnClickListener(this.ButtonClickListener);
        this.setRealI_button.setOnClickListener(this.ButtonClickListener);
        this.VoltCalibration_button.setOnClickListener(this.ButtonClickListener);
        this.CurrCalibration_button.setOnClickListener(this.ButtonClickListener);
        this.defultSet_Button.setOnClickListener(this.ButtonClickListener);
        this.ONOFF_Button.setOnClickListener(this.ButtonClickListener);
        this.setStart_ONOFF_button.setOnClickListener(this.ButtonClickListener);
        this.save_Button.setOnClickListener(this.ButtonClickListener);
        this.WHclear_Button.setOnClickListener(this.ButtonClickListener);
        this.AHclear_Button.setOnClickListener(this.ButtonClickListener);
        this.ChargerClose_I_set_Button.setOnClickListener(this.ButtonClickListener);
        this.set_Charger_Time_Button.setOnClickListener(this.ButtonClickListener);
        this.setBleName_Button.setOnClickListener(this.ButtonClickListener);
        this.setKey_Button.setOnClickListener(this.ButtonClickListener);
    }

    int crc16_ccitt(byte[] buf, int len) {
        int crc=0;
        for (int counter=0; counter < len; counter++) {
            crc=((crc << 8) ^ this.crc16tab[(crc >> 8) ^ (buf[counter] & 255)]) & SupportMenu.USER_MASK;
        }
        return crc;
    }

    public static class FileUtils {
        public static String getPath(Context context, Uri uri) {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection={"_data"};
                try {
                    Cursor cursor=context.getContentResolver().query(uri, projection, null, null, null);
                    int column_index=cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFileChooser() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        intent.addCategory("android.intent.category.OPENABLE");
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 3);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Please install a File Manager.", 0).show();
        }
    }

    private class IAPThread extends Thread {
        private int sleeptime;

        IAPThread(int militime) {
            this.sleeptime=militime;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (!isInterrupted()) {
                cdjActivity.this.UpgrateFirmware();
                cdjActivity.this.iapTask.interrupt();
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public static byte[] intToBytes(int value) {
        byte[] src={(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
        return src;
    }

    public static byte[] intToBytes2(int value) {
        byte[] src={(byte) ((value >> 24) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 8) & 255), (byte) (value & 255)};
        return src;
    }

    private void sendMessage(byte[] data) {
        if (this.mConnectService == null || this.mConnectService.getState() != 3) {
            Toast.makeText(this, "未连接到任何蓝牙设备", Toast.LENGTH_SHORT).show();
        } else {
            if (this.mConnectService == null || data == null) {
                return;
            }
            this.mConnectService.write(data);
        }
    }

    private class timeThread extends Thread {
        private int sleeptime;

        timeThread(int militime) {
            this.sleeptime=militime;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int num1=0;
            int num2=0;
            while (true) {
                try {
                    if (cdjActivity.this.BT_connet && !cdjActivity.this.doIAP && cdjActivity.this.autoRead) {
                        if (cdjActivity.this.S_reply || num1 > 2) {
                            switch (cdjActivity.this.cdj_State) {
                                case 0:
                                    cdjActivity.this.mConnectService.write(cdjActivity.this.cmd0);
                                    break;
                                case 1:
                                    cdjActivity.this.mConnectService.write(cdjActivity.this.cmd2);
                                    break;
                                case 2:
                                    if (cdjActivity.this.cmd_list.size() > 0) {
                                        Integer[] jj=cdjActivity.this.cmd_list.removeFirst();
                                        byte[] da=new byte[jj.length];
                                        for (int i=0; i < jj.length; i++) {
                                            da[i]=(byte) jj[i].intValue();
                                        }
                                        cdjActivity.this.mConnectService.write(da);
                                        break;
                                    } else if (num2 == 0 || num2 == 3 || num2 == 6 || num2 == 9) {
                                        cdjActivity.this.mConnectService.write(cdjActivity.this.cmd1);
                                        break;
                                    } else if (num2 == 2 || num2 == 4 || num2 == 8) {
                                        cdjActivity.this.mConnectService.write(cdjActivity.this.cmd11);
                                        break;
                                    } else if (num2 == 1 || num2 == 5) {
                                        cdjActivity.this.mConnectService.write(cdjActivity.this.cmd4);
                                        break;
                                    } else if (num2 == 7) {
                                        cdjActivity.this.mConnectService.write(cdjActivity.this.cmd2);
                                        break;
                                    } else if (num2 == 10) {
                                        cdjActivity.this.mConnectService.write(cdjActivity.this.cmd0);
                                        break;
                                    }
                                    break;
                            }
                            cdjActivity.this.S_reply=false;
                            num1=0;
                        }
                        num2++;
                        if (num2 > 10) {
                            num2=0;
                        }
                        try {
                            Thread.sleep(this.sleeptime);
                            num1++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Thread.sleep(1000L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void read_CDJData(int[] da) {
        int command=da[2];
        //充电量和充电时间
        if (command == 90) {
            this.charging_AH=(da[3] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[4] * 65536) + (da[5] * 256) + da[6];
            this.charging_WH=(da[7] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[8] * 65536) + (da[9] * 256) + da[10];
            this.currCharging_Time=(da[11] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[12] * 65536) + (da[13] * 256) + da[14];
            BigDecimal b2=new BigDecimal(this.charging_AH / 100000.0f);
            float f2=b2.setScale(2, 4).floatValue();
            this.AH_Text.setText(String.valueOf(f2));
            BigDecimal b22=new BigDecimal(this.charging_WH / 1000.0f);
            float f22=b22.setScale(2, 4).floatValue();
            this.WH_Text.setText(String.valueOf(f22));
            return;
        }
        //恢复出厂设置状态
        if (command == 95) {
            if (da[3] == 1) {
                Toast.makeText(this, "恢复成功，重启有效", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "恢复失败", Toast.LENGTH_SHORT).show();
            }
            this.defultSet_Button.setEnabled(true);
            return;
        }
        //
        if (command != 133) {
            switch (command) {
                //更新实时信息
                case 74:
                    this.Vout_meas_s=(da[3] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[4] * 65536) + (da[5] * 256) + da[6];
                    this.Iout_meas_s=(da[7] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[8] * 65536) + (da[9] * 256) + da[10];
                    this.Iout_ALL=(da[11] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[12] * 65536) + (da[13] * 256) + da[14];
                    this.RunTime=(da[15] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[16] * 65536) + (da[17] * 256) + da[18];
                    this.ONOFF_statue=da[19];
                    this.PM_num=(byte) da[20];
                    BigDecimal b1=new BigDecimal(this.Vout_meas_s / 1000.0f);
                    float f1=b1.setScale(1, 4).floatValue();
                    this.Vout_S_TextView.setText(String.valueOf(f1));
                    BigDecimal b12=new BigDecimal(this.Iout_meas_s / 1000.0f);
                    this.Iout_S_TextView.setText(String.valueOf(b12.setScale(2, 4).floatValue()));
                    BigDecimal b13=new BigDecimal(f1 * r1);
                    this.Pout_S_TextView.setText(String.valueOf(b13.setScale(1, 4).floatValue()));
                    BigDecimal b14=new BigDecimal(this.Iout_ALL / 1000.0f);
                    this.Iout_ALL_TextView.setText(String.valueOf(b14.setScale(1, 4).floatValue()));
                    this.PM_num_TextView.setText(String.valueOf((int) this.PM_num));
                    BigDecimal b15=new BigDecimal(f1 * r1);
                    this.Pout_ALL_TextView.setText(String.valueOf(b15.setScale(1, 4).floatValue()));
                    this.RunTime_TextView.setText(String.valueOf(this.RunTime));
                    if (this.ONOFF_statue == 0) {
                        this.ONOFF_Button.setText("打开输出");
                        this.Out_statue_TextView.setText("关机");
                        break;
                    } else {
                        this.ONOFF_Button.setText("关闭输出");
                        this.Out_statue_TextView.setText("运行");
                        break;
                    }
                case 75:
                    this.Curr_U=(da[3] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[4] * 65536) + (da[5] * 256) + da[6];
                    this.Curr_I=(da[7] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[8] * 65536) + (da[9] * 256) + da[10];
                    this.currVoltCalibration_value=(da[11] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[12] * 65536) + (da[13] * 256) + da[14];
                    this.currCurrCalibration_value=(da[15] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[16] * 65536) + (da[17] * 256) + da[18];
                    this.charging_cutoff_I=(da[19] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[20] * 65536) + (da[21] * 256) + da[22];
                    this.charging_cutoff_Time=(da[23] * ViewCompat.MEASURED_STATE_TOO_SMALL) + (da[24] * 65536) + (da[25] * 256) + da[26];
                    this.curr_STARTONOFF=da[27];
                    this.currRunModeType=da[28];
                    this.RealU_View.setText(String.valueOf(this.Curr_U / 1000.0f));
                    this.RealI_View.setText(String.valueOf(this.Curr_I / 1000.0f));
                    this.currVoltCalibration_View.setText(String.valueOf(this.currVoltCalibration_value));
                    this.currCurrCalibration_View.setText(String.valueOf(this.currCurrCalibration_value));
                    this.ChargerClose_I_view.setText(String.valueOf(this.charging_cutoff_I / 100.0f));
                    this.setCharger_Time_view.setText(String.valueOf(this.charging_cutoff_Time / 60));
                    if (this.curr_STARTONOFF == 0) {
                        this.Start_ONOFF_View.setText("关闭输出");
                    } else if (this.curr_STARTONOFF != 1) {
                        if (this.curr_STARTONOFF == 2) {
                            this.Start_ONOFF_View.setText("充电输出");
                        }
                    } else {
                        this.Start_ONOFF_View.setText("打开输出");
                    }
                    if (this.currRunModeType == 0) {
                        this.outInfo_View.setText("模块输出信息");
                        break;
                    } else if (this.currRunModeType != 1) {
                        if (this.currRunModeType == 2) {
                            this.outInfo_View.setText("模块输出信息(充电完成)");
                            break;
                        }
                    } else {
                        this.outInfo_View.setText("模块输出信息(充电模式运行中)");
                        break;
                    }
                    break;
                case 76:
                    if (da[3] == 1) {
                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_LONG).show();
                    }
                    this.setRealU_button.setEnabled(true);
                    break;
                case 77:
                    if (da[3] == 1) {
                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_LONG).show();
                    }
                    this.setRealI_button.setEnabled(true);
                    break;
                case 78:
                    this.ONOFF_statue=da[3];
                    if (this.ONOFF_statue == 0) {
                        this.ONOFF_Button.setText("打开输出");
                    } else {
                        this.ONOFF_Button.setText("关闭输出");
                    }
                    this.ONOFF_Button.setEnabled(true);
                    break;
                case 79:
                    if (da[3] == 1) {
                        Toast.makeText(this, "保存成功", 0).show();
                    } else {
                        Toast.makeText(this, "保存失败", 0).show();
                    }
                    this.save_Button.setEnabled(true);
                    break;
                case 80:
                    this.ONOFF_statue=da[3];
                    this.ONOFF_Button.setEnabled(true);
                    if (this.ONOFF_statue == 0) {
                        this.ONOFF_Button.setText("打开输出");
                        this.Out_statue_TextView.setText("关机");
                    } else {
                        this.ONOFF_Button.setText("关闭输出");
                        this.Out_statue_TextView.setText("运行");
                    }
                    if (this.cdj_State == 0) {
                        this.cdj_State=(byte) 1;
                    }
                    this.WHclear_Button.setEnabled(true);
                    this.AHclear_Button.setEnabled(true);
                    this.ChargerClose_I_set_Button.setEnabled(true);
                    this.set_Charger_Time_Button.setEnabled(true);
                    this.setRealU_button.setEnabled(true);
                    this.setRealI_button.setEnabled(true);
                    this.save_Button.setEnabled(true);
                    this.setStart_ONOFF_button.setEnabled(true);
                    this.VoltCalibration_button.setEnabled(true);
                    this.CurrCalibration_button.setEnabled(true);
                    this.defultSet_Button.setEnabled(true);
                    this.setBleName_Button.setEnabled(true);
                    this.setKey_Button.setEnabled(true);
                    break;
                case 81:
                    break;
                case 82:
                    Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
                    this.CurrCalibration_button.setEnabled(true);
                    break;
                case 83:
                    Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
                    this.VoltCalibration_button.setEnabled(true);
                    break;
                case 84:
                    this.cdj_State=(byte) 2;
                    break;
                case 85:
                    if (da[3] == 1) {
                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_LONG).show();
                        break;
                    }
                case 86:
                    this.AHclear_Button.setEnabled(true);
                    this.WHclear_Button.setEnabled(true);
                    break;
                case 87:
                    if (da[3] == 1) {
                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_LONG).show();
                        break;
                    }
                case 88:
                    if (da[3] == 1) {
                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_LONG).show();
                        break;
                    }
                default:
                    switch (command) {
                        case 128:
                            if (da[3] == 8) {
                                this.EraseOk=true;
                                break;
                            } else if (da[3] == 9) {
                                this.EraseOk=false;
                                break;
                            }
                            break;
                        case 129:
                            if (da[3] == 8) {
                                this.WriteInfoOk=true;
                                break;
                            } else if (da[3] == 9) {
                                this.WriteInfoOk=false;
                                break;
                            }
                            break;
                        case KEYCODE_MEDIA_RECORD /* 130 */:
                            if (da[3] == 8) {
                                this.WriteDataOk=true;
                                break;
                            } else if (da[3] == 9) {
                                this.WriteDataOk=false;
                                break;
                            }
                            break;
                        case 131:
                            if (da[7] == 170) {
                                this.CheckOk=true;
                                this.currFW_Type=170;
                                break;
                            } else if (da[7] == 85) {
                                this.CheckOk=true;
                                this.currFW_Type=85;
                                break;
                            }
                            break;
                    }
            }
            return;
        }
        //执行状态
        if (da[3] == 8) {
            this.ExcuteOk=true;
        } else if (da[3] == 9) {
            this.ExcuteOk=false;
        }
    }

    public int getCRC16(int[] bytes, int startindex, int lengs) {
        int CRC=65535;
        for (int CRC2=startindex; CRC2 < lengs; CRC2++) {
            CRC^=bytes[CRC2];
            for (int j=0; j < 8; j++) {
                if ((CRC & 1) == 1) {
                    CRC=(CRC >> 1) ^ 40961;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return CRC;
    }

    private void init_hex_string_table() {
        for (int i=0; i < 256; i++) {
            if (i < 16) {
                this.hex_string_table[i]=" 0" + Integer.toHexString(i).toUpperCase();
            } else {
                this.hex_string_table[i]=" " + Integer.toHexString(i).toUpperCase();
            }
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e(TAG, "onActivityResult");
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    String address=intent.getExtras().getString(DeviceListActivity.DEVICE_ADDRESS);
                    BluetoothDevice device=this.mBluetoothAdapter.getRemoteDevice(address);
                    this.target_device_name=device.getName();
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
                    this.mConnectService=new BluetoothService(this.mHandler);
                    break;
                } else {
                    Toast.makeText(this, "拒绝打开蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                }
            case 3:
                Uri uri=intent.getData();
                String path=FileUtils.getPath(this, uri);
                this.firmwareFile.setText(path);
                this.upgrateStatus.setText("选择了固件文件");
                System.out.println("select: " + path);
                break;
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        if (itemId == R.id.Connect) {
            if (!this.mBluetoothAdapter.isEnabled()) {
                Intent enableIntent=new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
                startActivityForResult(enableIntent, 2);
                return true;
            }
            Intent serverIntent=new Intent(this, (Class< ? >) DeviceListActivity.class);
            startActivityForResult(serverIntent, 1);
            return true;
        }
        if (itemId != R.id.Upgrate_FW) {
            if (itemId == R.id.autoRead_menu) {
                if (!this.BT_connet) {
                    if (this.autoRead) {
                        this.autoRead=false;
                    }
                    Toast.makeText(this, "蓝牙未连接", 0).show();
                    return true;
                }
                this.autoRead=!this.autoRead;
                return true;
            }
        } else {
            if (!this.BT_connet) {
                Toast.makeText(this, "蓝牙未连接", 0).show();
                return true;
            }
            if (this.timeTask != null) {
                this.timeTask.interrupt();
            }
            setContentView(R.layout.iap);
            this.firmwareFile=(EditText) findViewById(R.id.editTextFirmwareFile);
            this.upgrateStatus=(TextView) findViewById(R.id.textViewUpgrateState);
            this.buttonselectFirmwareFile=(Button) findViewById(R.id.buttonSelectFirmwareFile);
            this.buttonUpgrateFirmware=(Button) findViewById(R.id.buttonUpgrateFirmware);
            this.mProgressBar=(ProgressBar) findViewById(R.id.progressBar);
            this.buttonselectFirmwareFile.setOnClickListener(this.ButtonClickListener);
            this.buttonUpgrateFirmware.setOnClickListener(this.ButtonClickListener);
        }
        return false;
    }

    public static String getCRC3(byte[] data) {
        byte[] crc16_h={0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64};
        byte[] crc16_l={0, -64, -63, 1, -61, 3, 2, -62, -58, 6, 7, -57, 5, -59, -60, 4, -52, 12, 13, -51, 15, -49, -50, 14, 10, -54, -53, 11, -55, 9, 8, -56, -40, 24, 25, -39, 27, -37, -38, 26, 30, -34, -33, 31, -35, 29, 28, -36, 20, -44, -43, 21, -41, 23, 22, -42, -46, 18, 19, -45, 17, -47, -48, 16, -16, 48, 49, -15, 51, -13, -14, 50, 54, -10, -9, 55, -11, 53, 52, -12, 60, -4, -3, 61, -1, 63, 62, -2, -6, 58, 59, -5, 57, -7, -8, 56, 40, -24, -23, 41, -21, 43, 42, -22, -18, 46, 47, -17, 45, -19, -20, 44, -28, 36, 37, -27, 39, -25, -26, 38, 34, -30, -29, 35, -31, 33, 32, -32, -96, 96, 97, -95, 99, -93, -94, 98, 102, -90, -89, 103, -91, 101, 100, -92, 108, -84, -83, 109, -81, 111, 110, -82, -86, 106, 107, -85, 105, -87, -88, 104, 120, -72, -71, 121, -69, 123, 122, -70, -66, 126, Byte.MAX_VALUE, -65, 125, -67, -68, 124, -76, 116, 117, -75, 119, -73, -74, 118, 114, -78, -77, 115, -79, 113, 112, -80, 80, -112, -111, 81, -109, 83, 82, -110, -106, 86, 87, -105, 85, -107, -108, 84, -100, 92, 93, -99, 95, -97, -98, 94, 90, -102, -101, 91, -103, 89, 88, -104, -120, 72, 73, -119, 75, -117, -118, 74, 78, -114, -113, 79, -115, 77, 76, -116, 68, -124, -123, 69, -121, 71, 70, -122, -126, 66, 67, -125, 65, -127, Byte.MIN_VALUE, 64};
        int ucCRCLo=255;
        int ucCRCHi=255;
        for (byte b : data) {
            int iIndex=(b ^ ucCRCLo) & 255;
            ucCRCLo=ucCRCHi ^ crc16_h[iIndex];
            ucCRCHi=crc16_l[iIndex];
        }
        int i=ucCRCHi & 255;
        int crc=(i << 8) | (ucCRCLo & 255 & SupportMenu.USER_MASK);
        return String.format("%04X", Integer.valueOf(((65280 & crc) >> 8) | ((crc & 255) << 8)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean UpgrateFirmware() {
        verifyStoragePermissions(this);
        File file=new File(this.firmwareFile.getText().toString());
        if (file.exists()) {
            int FirmwareFileSize=(int) file.length();
            byte[] firmwareData=new byte[FirmwareFileSize];
            this.mProgressBar.setMax(FirmwareFileSize);
            this.mProgressBar.setProgress(0);
            try {
                InputStream in=new FileInputStream(file);
                in.read(firmwareData, 0, FirmwareFileSize);
                in.close();
                this.CheckOk=false;
                this.currFW_Type=0;
                cheackCom();
                int count=0;
                while (!this.CheckOk) {
                    sleepTime(1000);
                    if (count > 0) {
                        cheackCom();
                        sleepTime(1500);
                    }
                    count++;
                    if (count > 10) {
                        sendMessageToHandler("节点检测失败");
                        return false;
                    }
                }
                if (this.CheckOk) {
                    sendMessageToHandler("已经检测到节点");
                    sleepTime(200);
                    if (this.currFW_Type == 170) {
                        sendMessageToHandler("节点类型APP");
                        Excute(85);
                        sendMessageToHandler("正在跳转到BootLoader");
                        sleepTime(500);
                    } else {
                        sendMessageToHandler("节点类型BootLoader");
                    }
                    this.CheckOk=false;
                    this.currFW_Type=0;
                    cheackCom();
                    int count2=0;
                    while (!this.CheckOk) {
                        sleepTime(200);
                        count2++;
                        if (count2 > 10) {
                            sendMessageToHandler("跳转失败，升级失败");
                            return false;
                        }
                    }
                    sendMessageToHandler("跳转到BootLoader成功");
                    this.EraseOk=false;
                    EraseFlash(FirmwareFileSize);
                    sendMessageToHandler("擦除固件APP");
                    int count3=0;
                    while (!this.EraseOk) {
                        sleepTime(200);
                        count3++;
                        if (count3 > 10) {
                            sendMessageToHandler("擦除固件APP失败，，升级失败");
                            return false;
                        }
                    }
                    sendMessageToHandler("擦除固件APP成功");
                    sleepTime(200);
                    byte[] DataBuffer=new byte[512];
                    for (int AddrOffset=0; AddrOffset < FirmwareFileSize; AddrOffset+=512) {
                        int read_data_num=AddrOffset + 512 > FirmwareFileSize ? FirmwareFileSize % 512 : 512;
                        System.arraycopy(firmwareData, AddrOffset, DataBuffer, 0, read_data_num);
                        this.WriteInfoOk=false;
                        WriteInfo(AddrOffset, read_data_num + 2);
                        int count4=0;
                        while (!this.WriteInfoOk) {
                            sleepTime(200);
                            count4++;
                            if (count4 > 10) {
                                sendMessageToHandler("写需求地址出错，升级失败");
                                return false;
                            }
                        }
                        this.WriteDataOk=false;
                        sendDataPkg(DataBuffer, read_data_num);
                        sendMessageToHandler("写入数据" + (AddrOffset + read_data_num) + "字节");
                        int count5=0;
                        while (!this.WriteDataOk) {
                            sleepTime(200);
                            count5++;
                            if (count5 > 10) {
                                sendMessageToHandler("写数据出错，升级失败");
                                return false;
                            }
                        }
                        setProseedbarMessage(AddrOffset + read_data_num);
                    }
                    sendMessageToHandler("升级完毕");
                    sleepTime(500);
                    this.ExcuteOk=false;
                    Excute(170);
                    sendMessageToHandler("跳转到APP");
                    sleepTime(2000);
                    sendMessageToHandler("升级完成，请关闭APP断电重新启动");
                    return true;
                }
                sendMessageToHandler("节点检测失败");
                return false;
            } catch (Exception ep) {
                ep.printStackTrace();
                return false;
            }
        }
        return true;
    }

    void sendMessageToHandler(String s) {
        Message msg=new Message();
        msg.what=9;
        msg.obj=s;
        this.mHandler.sendMessage(msg);
    }

    void setProseedbarMessage(int value) {
        Message msg=new Message();
        msg.what=10;
        msg.obj=Integer.valueOf(value);
        this.mHandler.sendMessage(msg);
    }

    void sleepTime(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendDataPkg(byte[] data, int read_data_num) {
        char c;
        byte b=22;
        byte[] com1_sendata=new byte[22];
        byte[] adddata=new byte[read_data_num + 2];
        char c2=2;
        byte[] bArr=new byte[2];
        int crc=crc16_ccitt(data, read_data_num);
        for (int x=0; x < read_data_num; x++) {
            adddata[x]=data[x];
        }
        byte x2=16;
        int i=1;
        adddata[adddata.length - 2]=(byte) ((crc & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
        adddata[adddata.length - 1]=(byte) ((crc & 255) >> 0);
        byte k=0;
        byte[] sendobj=new byte[16];
        int crc2=0;
        while (crc2 < adddata.length) {
            sendobj[k]=adddata[crc2];
            byte k2=(byte) (k + 1);
            if (k2 == x2) {
                com1_sendata[0]=126;
                com1_sendata[i]=b;
                com1_sendata[c2]=-126;
                for (int i2=0; i2 < k2; i2++) {
                    com1_sendata[i2 + 3]=sendobj[i2];
                }
                int crc3=crc16_ccitt(com1_sendata, 19);
                com1_sendata[19]=(byte) ((crc3 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
                com1_sendata[20]=(byte) ((crc3 & 255) >> 0);
                com1_sendata[21]=13;
                BTwriteData(com1_sendata);
                sleepTime(150);
                k=0;
                c=65280;
            } else {
                int i3=adddata.length;
                if (crc2 == i3 - i) {
                    byte[] endData=new byte[k2 + 6];
                    endData[0]=126;
                    endData[i]=(byte) (k2 + 6);
                    endData[2]=-126;
                    for (int i4=0; i4 < k2; i4++) {
                        endData[i4 + 3]=sendobj[i4];
                    }
                    int crc4=crc16_ccitt(endData, k2 + 3);
                    c=65280;
                    endData[k2 + 3]=(byte) ((crc4 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
                    endData[k2 + 4]=(byte) ((crc4 & 255) >> 0);
                    endData[k2 + 5]=13;
                    BTwriteData(endData);
                } else {
                    c=65280;
                }
                k=k2;
            }
            crc2++;
            b=22;
            c2=2;
            x2=16;
            i=1;
        }
    }

    private void WriteInfo(int offset, int leng) {
        byte[] data={126, 14, -127, (byte) ((offset & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) ((offset & 16711680) >> 16), (byte) ((offset & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((offset & 255) >> 0), (byte) (((-16777216) & leng) >> 24), (byte) ((leng & 16711680) >> 16), (byte) ((leng & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((leng & 255) >> 0), (byte) ((65280 & crcData) >> 8), (byte) ((crcData & 255) >> 0), 13};
        int crcData=crc16_ccitt(data, 11);
        BTwriteData(data);
    }

    private void EraseFlash(int fileSize) {
        byte[] data={126, 10, Byte.MIN_VALUE, (byte) (((-16777216) & fileSize) >> 24), (byte) ((16711680 & fileSize) >> 16), (byte) ((fileSize & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((fileSize & 255) >> 0), (byte) ((65280 & crcData) >> 8), (byte) ((crcData & 255) >> 0), 13};
        int crcData=crc16_ccitt(data, 7);
        BTwriteData(data);
    }

    private void Excute(int fwtppe) {
        byte[] data=new byte[10];
        if (fwtppe == 170) {
            data[3]=-86;
            data[4]=-86;
            data[5]=-86;
            data[6]=-86;
        } else if (fwtppe == 85) {
            data[3]=85;
            data[4]=85;
            data[5]=85;
            data[6]=85;
        }
        data[0]=126;
        data[1]=10;
        data[2]=-123;
        int crcData=crc16_ccitt(data, 7);
        data[7]=(byte) ((65280 & crcData) >> 8);
        data[8]=(byte) ((crcData & 255) >> 0);
        data[9]=13;
        BTwriteData(data);
    }

    private void cheackCom() {
        byte[] data={126, 6, -125, (byte) ((65280 & crcData) >> 8), (byte) ((crcData & 255) >> 0), 13};
        int crcData=crc16_ccitt(data, 3);
        BTwriteData(data);
    }

    void BTwriteData(byte[] data) {
        this.mConnectService.write(data);
    }
}