package ensharp.decibelcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

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

    private String mTrackName;
    private String mArtistName;
    private String mTrackFullPath;
    private String mPackageName;

    private AudioManager mAudioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPref = new SharedPreferences(context);
        mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        String action = intent.getAction();
        //String cmd = intent.getStringExtra("command");
        Log.v("tag ", action);
        mTrackName = intent.getStringExtra("track");
        mPackageName = mMusicAccessibilityService.packageName;
        //음악제목값이 null일 경우 마지막 재생된 노래제목 가져오기
        if(mTrackName == null) {
            mTrackName = mPref.getValue("lastTrackName","묘해, 너와","lastTrackInformation");
        }
        Log.i("TrackName 및 PackageName", "mTrackName : " + mTrackName + " / mPackageName : " + mPackageName);
        // 삼성 기본플레이어로 재생 시
        if(mPackageName.contains("com.sec.android.app.music")) {
            Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.TITLE + " == \"" + mTrackName + "\"";
            String[] STAR = {"*"};
            String startPosition = "00:00:00";
            Cursor cursor = context.getContentResolver().query(mAudioUri, STAR, selection, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    mTrackFullPath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    mArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if(action.contains("playstatechanged")) {
                        startPosition = convertDuration(intent.getExtras().getLong("position"));
                    } else {
                        startPosition = "00:00:00";
                    }
                }
            }
            mPref.putValue("lastPackageName", mPackageName, "lastTrackInformation");
            mPref.putValue("lastTrackName",mTrackName,"lastTrackInformation");
            Log.i("삼성앱 음원정보 결과값","가수 : " + mArtistName + " 제목 : " + mTrackName + " 음원저장경로 : " + mTrackFullPath + " 앱명 : " + mPackageName + " 시작점 : " + startPosition);
        } else {
            Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.TITLE + " == \"" + mTrackName + "\"";
            String[] STAR = {"*"};
            String startPosition;
            Cursor cursor = context.getContentResolver().query(mAudioUri, STAR, selection, null, null);
            // 다운 받아져 있는 음원일 경우
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    mTrackFullPath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    mArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                }
            }
            // 스트리밍 음원일 경우
            else {
                mTrackFullPath = "스트리밍 음원";
                mArtistName = intent.getStringExtra("artist");
            }
            mPref.putValue("lastPackageName", mPackageName, "lastTrackInformation");
            Log.i("다른앱 음원정보 결과값","가수 : " + mArtistName + " 제목 : " + mTrackName + " 음원저장경로 : " + mTrackFullPath + " 앱명 : " + mPackageName);
        }
//        String all = intent.getScheme();
//        Set<String> keys = intent.getExtras().keySet();
//        Bundle bundle = intent.getExtras();
//        for (String key : bundle.keySet()) {
//            Object value = bundle.get(key);
//            Log.d("인텐트키정보", String.format("%s %s (%s)", key,
//                    value.toString(), value.getClass().getName()));
//        }
//        Long trackLength = intent.getExtras().getLong("trackLength");
//        Long position = intent.getExtras().getLong("position");
//        boolean isplaying = intent.getExtras().getBoolean("playing");
//            String artists = intent.getStringExtra("artist");
//            String album = intent.getStringExtra("album");
//            String track = intent.getStringExtra("track");
//            String duration = intent.getStringExtra("duration");
//            String bookmark = intent.getStringExtra("bookmark");
//        String artists = intent.getStringExtra("artist");
//        String album = intent.getStringExtra("album");
//
//        Log.e("음악정보", "가수 : " + artists + " / 제목 : " + track + " / 음악길이 : " + convertDuration(trackLength) + " / 포지션 : " + convertDuration(position));
//
//        //Log.v("tag", all + " / " + artists + " / " + track + "********" + album);
//        Log.e("노래경로", "노래경로 : " + fullpath + "****** 가수 : " + artist + "****** 제목 : " + selection);

       mMsg = mHandler.obtainMessage();
        if (mAudioManager.isMusicActive()) {
            mMsg.what = SEND_MUSIC_INFORMATION;
            String musicInfo = new String("가수 : " + mArtistName + "\n" + "제목 : " + mTrackName + "\n" + "음원저장경로 : " + mTrackFullPath + "\n" + "앱명 : " + mPackageName);
            mMsg.obj = musicInfo;
            mHandler.sendMessage(mMsg);
            mPref.putValue("0", musicInfo, "음악 재생 정보");
            Log.e("음악재생여부", "재생중");
        } else {
            mMsg.what = SEND_MUSIC_INFORMATION;
            String musicInfo = new String("정지");
            mMsg.obj = musicInfo;
            mHandler.sendMessage(mMsg);
            //Log.i("저장값", "lastTrackName : " + mTrackName + " lastPackageName : " + mPackageName);
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
