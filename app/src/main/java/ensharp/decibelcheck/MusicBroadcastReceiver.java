package ensharp.decibelcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Semin on 2017-02-10.
 */
public class MusicBroadcastReceiver extends BroadcastReceiver {

    Message mMsg;
    MainService mainService = new MainService();
    MainService.myServiceHandler mHandler = mainService.new myServiceHandler();
    MusicAccessibilityService mMusicAccessibilityService = new MusicAccessibilityService();
    SharedPreferences mPref;
    private static final int SEND_MUSIC_INFORMATION = 6;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPref = new SharedPreferences(context);
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
        mMsg = mHandler.obtainMessage();
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

        if (manager.isMusicActive()) {
            mMsg.what = SEND_MUSIC_INFORMATION;
            String appname = mMusicAccessibilityService.packageName;
            String musicInfo = new String("가수 : " + artist + "\n" + "제목 : " + intent.getStringExtra("track") + "\n" + "음원저장경로 : " + fullpath + "\n" + "앱명 : " + appname);
            mMsg.obj = musicInfo;
            mHandler.sendMessage(mMsg);
            mPref.putValue("0", musicInfo, "음악 재생 정보");
            Log.e("음악재생여부", "재생중");
        } else {
            mMsg.what = SEND_MUSIC_INFORMATION;
            String musicInfo = new String("없음");
            mMsg.obj = musicInfo;
            mHandler.sendMessage(mMsg);
            mPref.putValue("0", musicInfo, "음악 재생 정보");
            Log.e("음악재생여부", "일시정지");
        }
    }

    public String convertDuration(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }
}
