package ensharp.decibelcheck;

import android.app.ActivityManager;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.util.List;

/**
 * Created by Semin on 2017-01-27.
 */
public class ServiceThread extends Thread implements AudioManager.OnAudioFocusChangeListener {

    Handler handler;
    Context context;
    boolean isRun;
    IntentFilter intentFilter;
    IntentFilter intentFilter_music;
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

        intentFilter_music = new IntentFilter();
        //intentFilter_music.addAction("com.sec.android.app.music.metachanged");
       ;
        intentFilter_music.addAction("com.android.music.playstatechanged");
        intentFilter_music.addAction("com.htc.music.playstatechanged");
        intentFilter_music.addAction("fm.last.android.playstatechanged");
        intentFilter_music.addAction("com.sec.android.app.music.playstatechanged");
        intentFilter_music.addAction("com.nullsoft.winamp.playstatechanged");
        intentFilter_music.addAction("com.amazon.mp3.playstatechanged");
        intentFilter_music.addAction("com.miui.player.playstatechanged");
        intentFilter_music.addAction("com.real.IMP.playstatechanged");
        intentFilter_music.addAction("com.sonyericsson.music.playstatechanged");
        intentFilter_music.addAction("com.rdio.android.playstatechanged");
        intentFilter_music.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
        intentFilter_music.addAction("com.andrew.apollo.playstatechanged");
        intentFilter_music.addAction("gonemad.dashclock.music.playstatechanged");
        intentFilter_music.addAction("com.piratemedia.musicmod.playstatechanged");

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
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
            context.unregisterReceiver(headSetConnectReceiver);
            context.unregisterReceiver(mReceiver);
            this.isRun = false;
        }
    }

    public void run() {
        context.registerReceiver(headSetConnectReceiver, intentFilter, null, handler);
        context.registerReceiver(mReceiver, intentFilter_music, null, handler);

        if (isRun) {
            Log.i("서비스 run", "sendEmptyMessage");
            handler.sendEmptyMessage(SEND_THREAD_START_MESSAGE);
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.v("tag ", action + " / " + cmd);
            String all = intent.getScheme();
            String artists = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            Log.v("tag", all + "*******" + artists + "********" + track + "********" + album);
            Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.TITLE + " == \"" + intent.getStringExtra("track") + "\"";
            String artist = intent.getStringExtra("artist");
            String fullpath = "없음";
            String[] STAR = {"*"};
            Cursor cursor = context.getContentResolver().query(mAudioUri, STAR, selection, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));

                }
            }
            Log.e("노래경로", "노래경로 : " + fullpath + "****** 가수 : " + artist + "****** 제목 : " + selection);
        }
    };

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
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.e("오디오 포커스 변화", "GAIN");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.e("오디오 포커스 변화", "LOSS");
//                serviceList();
//                processList();
//                runningList();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.e("오디오 포커스 변화", "LOSS_TRANSTENT");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.e("오디오 포커스 변화", "LOSS_TRANSIENT_CAN_DUCK");
                break;
        }
    }

    private void serviceList() {
        /* 실행중인 service 목록 보기 */
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);

        for (int i = 0; i < rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            Log.d("run service", "Package Name : " + rsi.service.getPackageName());
        }

    }

    private void processList() {
        /* 실행중인 process 목록 보기*/
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();

        for (int i = 0; i < appList.size(); i++) {
            ActivityManager.RunningAppProcessInfo rapi = appList.get(i);
            Log.d("run Process", "Package Name : " + rapi.processName);
        }

    }

    private void runningList() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);


        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        for (int i = 0; i < taskInfo.size(); i++) {
            Log.d("topActivity", "CURRENT Activity ::"
                    + taskInfo.get(0).topActivity.getClassName());

            ComponentName componentInfo = taskInfo.get(0).topActivity;
            Log.d("topActivity", "CURRENT Package ::"
                    + componentInfo.getPackageName());
        }
    }


}
