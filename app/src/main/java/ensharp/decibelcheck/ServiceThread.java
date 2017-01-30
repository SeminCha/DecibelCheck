package ensharp.decibelcheck;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Semin on 2017-01-27.
 */
public class ServiceThread extends Thread implements AudioManager.OnAudioFocusChangeListener {

    Handler handler;
    Context context;
    boolean isRun;
    IntentFilter intentFilter;
    Message msg;
    public SharedPreferences pref;
    private static final String BLUETOOTH_HEADSET_ACTION = "android.bluetooth.headset.action.STATE_CHANGED";
    private static final int SEND_THREAD_START_MESSAGE = 0;
    private static final int SEND_THREAD_STOP_MESSAGE = 1;
    private static final int SEND_THREAD_NORMALEARPHONE_PLUGGED = 2;
    private static final int SEND_THREAD_NORMALEARPHONE_UNPLUGGED = 3;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED = 4;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED = 5;
    AudioManager audioManager;

    public ServiceThread() {
    }

    public boolean getIsServiceRun() {
        return isRun;
    }

    public void setIsServiceRun(boolean isRun) {
        this.isRun = isRun;
    }

    public ServiceThread(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
        pref = new SharedPreferences(context);
        intentFilter = new IntentFilter();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            intentFilter.addAction(BLUETOOTH_HEADSET_ACTION);
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        } else {
            intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        }

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
//        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                       audioFocusGranted
//        } else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
//            mState.audioFocusGranted = false;
//        }
    }

    public void stopRunning() {
        synchronized (this) {
            Log.i("서비스 중지", "stopRunning");
            context.unregisterReceiver(headSetConnectReceiver);
            this.isRun = false;
        }
    }

    public void run() {
        context.registerReceiver(headSetConnectReceiver, intentFilter, null, handler);
        if (isRun) {
            Log.i("서비스 run", "sendEmptyMessage");
            handler.sendEmptyMessage(SEND_THREAD_START_MESSAGE);
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }
    }

    BroadcastReceiver headSetConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int headSetState;
            msg = handler.obtainMessage();
            headSetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
            Log.i("headSetState", Integer.toString(headSetState));
            if (intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
//                headSetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
//                Log.i("headSetState", Integer.toString(headSetState));
                if (headSetState == 0) {
                    Log.e("블루투스 이어폰 log", "Earphone is unconnected");
                    pref.putValue("0", false, "bluetoothEarphone");
                    msg.what = SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED;
                    msg.obj = "X";
                    handler.sendMessage(msg);
                    //bluetoothEarphoneTxt.setText("No");
                } else if (headSetState == 1) {
                    Log.e("블루투스 이어폰 log", "Earphone is unconnected");
                    pref.putValue("0", false, "bluetoothEarphone");
                    msg.what = SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED;
                    msg.obj = "X";
                    handler.sendMessage(msg);
                    //bluetoothEarphoneTxt.setText("연결중");
                } else {
                    Log.e("블루투스 이어폰 log", "Earphone is connected");
                    pref.putValue("0", true, "bluetoothEarphone");
                    msg.what = SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED;
                    msg.obj = "O";
                    handler.sendMessage(msg);
                    //bluetoothEarphoneTxt.setText("Yes");
                }
            } else if (action.equals(intent.ACTION_HEADSET_PLUG)) {
                boolean isEarphoneOn = (intent.getIntExtra("state", 0) > 0) ? true : false;
                if (isEarphoneOn) {
                    Log.e("일반 이어폰 log", "Earphone is plugged");
                    msg.what = SEND_THREAD_NORMALEARPHONE_PLUGGED;
                    msg.obj = "O";
                    handler.sendMessage(msg);
                    pref.putValue("0", true, "normalEarphone");
                    //normalEarphoneTxt.setText("Yes");
                } else {
                    Log.e("일반 이어폰 log", "Earphone is unPlugged");
                    msg.what = SEND_THREAD_NORMALEARPHONE_UNPLUGGED;
                    msg.obj = "X";
                    handler.sendMessage(msg);
                    pref.putValue("0", false, "normalEarphone");
                    //normalEarphoneTxt.setText("No");
                }
            }
        }
    };

    @Override
    public void onAudioFocusChange(int focusChange) {

    }
}
