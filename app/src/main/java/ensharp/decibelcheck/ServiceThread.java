package ensharp.decibelcheck;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    MusicAccessibilityService musicAccessibilityService;
    private static final String BLUETOOTH_HEADSET_ACTION = "android.bluetooth.headset.action.STATE_CHANGED";
    private static final int SEND_THREAD_START_MESSAGE = 0;
    private static final int SEND_THREAD_STOP_MESSAGE = 1;
    private static final int SEND_THREAD_NORMALEARPHONE_PLUGGED = 2;
    private static final int SEND_THREAD_NORMALEARPHONE_UNPLUGGED = 3;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED = 4;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED = 5;
    private static final int SEND_MUSIC_INFORMATION = 6;
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
        musicAccessibilityService = new MusicAccessibilityService();
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
        intentFilter_music.addAction("com.android.music.metachanged");
        intentFilter_music.addAction("com.android.music.playstatechanged");
        intentFilter_music.addAction("com.android.music.playbackcomplete");
        intentFilter_music.addAction("com.android.music.queuechanged");
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

    public String convertDuration(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
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
            Set<String> keys = intent.getExtras().keySet();
            Bundle bundle = intent.getExtras();
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("인텐트키정보", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
            Long trackLength = intent.getExtras().getLong("trackLength");
            Long position = intent.getExtras().getLong("position");
            boolean isplaying = intent.getExtras().getBoolean("playing");
//            String artists = intent.getStringExtra("artist");
//            String album = intent.getStringExtra("album");
//            String track = intent.getStringExtra("track");
//            String duration = intent.getStringExtra("duration");
//            String bookmark = intent.getStringExtra("bookmark");
            String artists = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            Log.e("음악정보", "가수 : " + artists + " / 제목 : " + track + " / 음악길이 : " + convertDuration(trackLength) + " / 포지션 : " + convertDuration(position));

            //Log.v("tag", all + " / " + artists + " / " + track + "********" + album);
            msg = handler.obtainMessage();
            Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.TITLE + " == \"" + intent.getStringExtra("track") + "\"";
            String artist = intent.getStringExtra("artist");
            String fullpath = "스트리밍 음원이므로 없음";
            String[] STAR = {"*"};
            Cursor cursor = context.getContentResolver().query(mAudioUri, STAR, selection, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));


                }
            }
            Log.e("노래경로", "노래경로 : " + fullpath + "****** 가수 : " + artist + "****** 제목 : " + selection);
            AudioManager manager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);

            if(manager.isMusicActive()) {
                msg.what = SEND_MUSIC_INFORMATION;
                String appname = musicAccessibilityService.packageName;
                String musicInfo = new String("가수 : " + artist + "\n" + "제목 : " + intent.getStringExtra("track") + "\n" + "음원저장경로 : " + fullpath + "\n" + "앱명 : " + appname);
                msg.obj = musicInfo;
                handler.sendMessage(msg);
                pref.putValue("0",musicInfo,"음악 재생 정보");
                Log.e("음악재생여부", "재생중");
            } else {
                msg.what = SEND_MUSIC_INFORMATION;
                String musicInfo = new String("없음");
                msg.obj = musicInfo;
                handler.sendMessage(msg);
                pref.putValue("0",musicInfo,"음악 재생 정보");
                Log.e("음악재생여부", "일시정지");
            }
        }
    };

    public String getRunningPackageName() {
        String appName = "인식불가";
        return appName;
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
