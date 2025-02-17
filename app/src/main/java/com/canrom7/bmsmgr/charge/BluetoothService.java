package com.canrom7.bmsmgr.charge;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/* loaded from: classes.dex */
public class BluetoothService {
    private static final String BT_NAME = "XULANG";
    public static final int CONNECTED = 3;
    public static final int CONNECTING = 2;
    public static final int IDLE = 0;
    public static final int LISTENING = 1;
    private static final String TAG = "Service";
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final boolean DEBUG = true;
    public static boolean allowRec = DEBUG;
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private int BtState = 0;

    public BluetoothService(Handler handler) {
        this.mHandler = handler;
    }

    private synchronized void setState(int state) {
        this.BtState = state;
    }

    public synchronized int getState() {
        return this.BtState;
    }

    public synchronized void acceptWait() {
        Log.e(TAG, "进入acceptWait");
        if (this.mAcceptThread == null && this.mConnectedThread == null) {
            this.mAcceptThread = new AcceptThread();
            this.mAcceptThread.start();
        }
        setState(1);
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.e(TAG, "正在连接" + device);
        cancelAllBtThread();
        this.mConnectThread = new ConnectThread(device);
        this.mConnectThread.start();
        setState(2);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.e(TAG, "connected");
        cancelAllBtThread();
        this.mConnectedThread = new ConnectedThread(socket);
        this.mConnectedThread.start();
        sendString2UI(4, "device name", device.getName());
        setState(3);
    }

    public synchronized void cancelAllBtThread() {
        Log.e(TAG, "cancelAllBtThread方法");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }
        setState(0);
    }

    public void write(byte[] out) {
        synchronized (this) {
            if (this.BtState != 3) {
                return;
            }
            ConnectedThread r = this.mConnectedThread;
            r.write(out);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectionFailed() {
        setState(1);
        this.mConnectedThread = null;
        acceptWait();
        sendString2UI(5, "toast", "连接失败");
    }

    private void sendString2UI(int what, String key, String str) {
        Message msg = this.mHandler.obtainMessage(what);
        Bundle bundle = new Bundle();
        bundle.putString(key, str);
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectionBreak() {
        setState(1);
        this.mConnectedThread = null;
        acceptWait();
        sendString2UI(5, "toast", "连接断开");
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mBtServSocket;

        public AcceptThread() {
            BluetoothServerSocket bss = null;
            try {
                bss = BluetoothService.this.mAdapter.listenUsingRfcommWithServiceRecord(BluetoothService.BT_NAME, BluetoothService.MY_UUID);
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "listen() failed", e);
            }
            this.mBtServSocket = bss;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.e(BluetoothService.TAG, "Begin mAcceptThread");
            setName("AcceptThread");
            while (BluetoothService.this.BtState != 3) {
                try {
                    BluetoothSocket socket = this.mBtServSocket.accept();
                    if (socket != null) {
                        synchronized (BluetoothService.this) {
                            switch (BluetoothService.this.BtState) {
                                case 0:
                                case 3:
                                    try {
                                        socket.close();
                                        break;
                                    } catch (IOException e) {
                                        Log.e(BluetoothService.TAG, "Could not close unwanted socket", e);
                                        break;
                                    }
                                case 1:
                                case 2:
                                    BluetoothService.this.connected(socket, socket.getRemoteDevice());
                                    break;
                            }
                        }
                    }
                } catch (IOException e2) {
                    Log.e(BluetoothService.TAG, "accept() failed", e2);
                }
            }
            Log.e(BluetoothService.TAG, "End mAcceptThread");
        }

        public void cancel() {
            Log.e(BluetoothService.TAG, "cancel " + this);
            try {
                this.mBtServSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "close() of server failed", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mBtDevice;
        private final BluetoothSocket mBtSocket;

        public ConnectThread(BluetoothDevice device) {
            this.mBtDevice = device;
            BluetoothSocket bs = null;
            try {
                bs = device.createRfcommSocketToServiceRecord(BluetoothService.MY_UUID);
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "create() failed", e);
            }
            this.mBtSocket = bs;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.e(BluetoothService.TAG, "Begin mConnectThread");
            setName("ConnectThread");
            try {
                this.mBtSocket.connect();
                synchronized (BluetoothService.this) {
                    BluetoothService.this.mConnectThread = null;
                }
                BluetoothService.this.connected(this.mBtSocket, this.mBtDevice);
                Log.d(BluetoothService.TAG, "End mConnectThread");
            } catch (IOException e) {
                BluetoothService.this.connectionFailed();
                try {
                    this.mBtSocket.close();
                } catch (IOException e2) {
                    Log.e(BluetoothService.TAG, "close() fail", e2);
                }
                BluetoothService.this.acceptWait();
                Log.d(BluetoothService.TAG, "End mConnectThread");
            }
        }

        public void cancel() {
            Log.e(BluetoothService.TAG, "cancel " + this);
            try {
                this.mBtSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "close() fail", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mBtSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(BluetoothService.TAG, "construct ConnectedThread");
            this.mBtSocket = socket;
            InputStream is = null;
            OutputStream os = null;
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
            } catch (IOException e) {
                Log.i(BluetoothService.TAG, "get Stream fail", e);
            }
            this.mInputStream = is;
            this.mOutputStream = os;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.i(BluetoothService.TAG, "Begin mConnectedThread");
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    int bytes = this.mInputStream.read(buffer);
                    if (bytes != -1 && BluetoothService.allowRec) {
                        BluetoothService.this.mHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                    }
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e2) {
                    Log.e(BluetoothService.TAG, "connection break", e2);
                    BluetoothService.this.connectionBreak();
                    Log.i(BluetoothService.TAG, "End mConnectedThread");
                    return;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                this.mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            Log.e(BluetoothService.TAG, "cancel " + this);
            try {
                this.mBtSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothService.TAG, "close() fail", e);
            }
        }
    }
}