package com.canrom7.bmsmgr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public Bluetooth mBluetooth = null;
    private List<String> mPermissionList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    NativeClass.deviceUpdateName(1L,"","");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }





    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals("mounted")) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            for (String str : file.list()) {
                if (!deleteDir(new File(file, str))) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static final String getFileProviderName(Context context) {
        return context.getPackageName() + ".file-provider";
    }

    public static PackageInfo getPackage(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getExternalStorageDir() {
        return Environment.getExternalStorageDirectory().toString();
    }


    public int getTitleBarHeight() {
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return getResources().getDimensionPixelSize(identifier);
        }
        return -1;
    }

    @Override // org.qtproject.p001qt.android.QtActivityBase, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Bluetooth bluetooth;
        String str;
        super.onActivityResult(i, i2, intent);
        if (i == 18) {
            Log.w(TAG, "********+++++++ LOCATION_ACCESS: " + i2);
            bluetooth = this.mBluetooth;
            if (bluetooth == null) {
                return;
            } else {
                str = "location";
            }
        } else {
            if (i != 20) {
                Log.w(TAG, "********+++++++ ????: " + i2);
                return;
            }
            Log.w(TAG, "********+++++++ BLUETOOTH: " + i2);
            bluetooth = this.mBluetooth;
            if (bluetooth == null) {
                return;
            } else {
                str = "ble";
            }
        }
        bluetooth.setPermissionState(true, "", str);
    }


    public boolean openUri(String str) {
        Uri uriForFile = FileProvider.getUriForFile(this, getFileProviderName(this), new File(str));
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this).setStream(uriForFile).setType("text/html").getIntent().setAction("android.intent.action.VIEW").setDataAndType(uriForFile, "text/html").addFlags(1), "select application"));
        return true;
    }

    public void requestScanPermission() {



    }



}
