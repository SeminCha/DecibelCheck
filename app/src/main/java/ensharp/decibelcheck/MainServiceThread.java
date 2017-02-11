package ensharp.decibelcheck;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Semin on 2017-01-27.
 */
public class MainServiceThread extends Thread implements AudioManager.OnAudioFocusChangeListener {

    private Handler mHandler;
    private Context mContext;
    private IntentFilter mIntentFilter;
    private Message mMsg;
    private SharedPreferences mPref;
    private AudioManager mAudioManager;
    private static final int SEND_THREAD_START_MESSAGE = 0;
    private static final int SEND_THREAD_STOP_MESSAGE = 1;
    private static final int SEND_THREAD_NORMALEARPHONE_PLUGGED = 2;
    private static final int SEND_THREAD_NORMALEARPHONE_UNPLUGGED = 3;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED = 4;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED = 5;
    private static final int SEND_MUSIC_INFORMATION = 6;


    public MainServiceThread() {
    }

    public MainServiceThread(Handler handler, Context context) {
        this.mHandler = handler;
        this.mContext = context;
        mPref = new SharedPreferences(context);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e("오디오 포커스 결과", "획득");
        } else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            Log.e("오디오 포커스 결과", "획득실패");
        }
    }

    public void stopRunning() {
        synchronized (this) {
            Log.i("서비스 중지", "stopRunning");
            mContext.unregisterReceiver(headSetConnectReceiver);
        }
    }

    public void run() {
        mContext.registerReceiver(headSetConnectReceiver, mIntentFilter, null, mHandler);

            Log.i("서비스 run", "sendEmptyMessage");
            mHandler.sendEmptyMessage(SEND_THREAD_START_MESSAGE);
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }

    }

    // 일반 이어폰만 정적 브로드캐스트 리시버로 구현
    BroadcastReceiver headSetConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int headSetState;
            mMsg = mHandler.obtainMessage();
            headSetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
            Log.i("headSetState", Integer.toString(headSetState));
            if (action.equals(intent.ACTION_HEADSET_PLUG)) {
                boolean isEarphoneOn = (intent.getIntExtra("state", 0) > 0) ? true : false;
                if (isEarphoneOn) {
                    Log.e("일반 이어폰 log", "Earphone is plugged");
                    mMsg.what = SEND_THREAD_NORMALEARPHONE_PLUGGED;
                    mMsg.obj = "O";
                    mHandler.sendMessage(mMsg);
                    mPref.putValue("0", true, "normalEarphone");
                    //normalEarphoneTxt.setText("Yes");
                } else {
                    Log.e("일반 이어폰 log", "Earphone is unPlugged");
                    mMsg.what = SEND_THREAD_NORMALEARPHONE_UNPLUGGED;
                    mMsg.obj = "X";
                    mHandler.sendMessage(mMsg);
                    mPref.putValue("0", false, "normalEarphone");
                    //normalEarphoneTxt.setText("No");
                }
            }
        }
    };

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.e("오디오 포커스 변화", "GAIN");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.e("오디오 포커스 변화", "LOSS");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.e("오디오 포커스 변화", "LOSS_TRANSTENT");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.e("오디오 포커스 변화", "LOSS_TRANSIENT_CAN_DUCK");
                break;
        }
    }
}
