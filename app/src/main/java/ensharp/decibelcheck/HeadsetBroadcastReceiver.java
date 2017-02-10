package ensharp.decibelcheck;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.util.Log;

/**
 * Created by Semin on 2017-02-10.
 */
public class HeadsetBroadcastReceiver extends BroadcastReceiver {

    IntentFilter mIntentFilter;
    Message mMsg;

    MainService mainService = new MainService();
    MainService.myServiceHandler mHandler = mainService.new myServiceHandler();

    SharedPreferences mPref;
    private static final String BLUETOOTH_HEADSET_ACTION = "android.bluetooth.headset.action.STATE_CHANGED";
    private static final int SEND_THREAD_START_MESSAGE = 0;
    private static final int SEND_THREAD_STOP_MESSAGE = 1;
    private static final int SEND_THREAD_NORMALEARPHONE_PLUGGED = 2;
    private static final int SEND_THREAD_NORMALEARPHONE_UNPLUGGED = 3;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED = 4;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED = 5;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        int headSetState;
        mMsg = mHandler.obtainMessage();
        mPref = new SharedPreferences(context);
        headSetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
        Log.i("headSetState", Integer.toString(headSetState));
        if (intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
//                headSetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
//                Log.i("headSetState", Integer.toString(headSetState));
            if (headSetState == 0) {
                Log.e("블루투스 이어폰 log", "Earphone is unconnected");
                mPref.putValue("0", false, "bluetoothEarphone");
                mMsg.what = SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED;
                mMsg.obj = "X";
                mHandler.sendMessage(mMsg);
                //bluetoothEarphoneTxt.setText("No");
            } else if (headSetState == 1) {
                Log.e("블루투스 이어폰 log", "Earphone is unconnected");
                mPref.putValue("0", false, "bluetoothEarphone");
                mMsg.what = SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED;
                mMsg.obj = "X";
                mHandler.sendMessage(mMsg);
                //bluetoothEarphoneTxt.setText("연결중");
            } else {
                Log.e("블루투스 이어폰 log", "Earphone is connected");
                mPref.putValue("0", true, "bluetoothEarphone");
                mMsg.what = SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED;
                mMsg.obj = "O";
                mHandler.sendMessage(mMsg);
                //bluetoothEarphoneTxt.setText("Yes");
            }
        }

    }
}
