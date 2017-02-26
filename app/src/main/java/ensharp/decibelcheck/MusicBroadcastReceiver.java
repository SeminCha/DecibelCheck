package ensharp.decibelcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by Semin on 2017-02-10.
 */
public class MusicBroadcastReceiver extends BroadcastReceiver {

    Message mMsg;
    MainService mainService = new MainService();
    MainService.myServiceHandler mHandler = mainService.new myServiceHandler();
    MusicAccessibilityService mMusicAccessibilityService = new MusicAccessibilityService();
    ListeningService mListeningService;
    SharedPreferences mPref;
    SoundFile mSoundfile;
    private static final int SEND_MUSIC_INFORMATION = 6;

    private String mTrackName;
    private String mArtistName;
    private String mTrackFullPath;
    private String mPackageName;
    private boolean mIsPlaying;
    private ServiceData mServiceData;
    private String mMusicKeyName;
    Intent mIntent;
    Intent mdBIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPref = new SharedPreferences(context);
        mListeningService = new ListeningService();
        mIntent = new Intent(context, ListeningService.class);
        mdBIntent = new Intent(context, DecibelService.class);
        mServiceData = new ServiceData(context);
        String action = intent.getAction();
        //String cmd = intent.getStringExtra("command");
        Log.v("tag ", action);
        mTrackName = intent.getStringExtra("track");
        mIsPlaying = intent.getExtras().getBoolean("playing");
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
            long startPosition=0;
            Cursor cursor = context.getContentResolver().query(mAudioUri, STAR, selection, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    mTrackFullPath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    mArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if(action.contains("playstatechanged")) {
                        startPosition = intent.getExtras().getLong("position");
                    } else {
                        startPosition = 0;
                    }
                }
            }
            mPref.putValue("lastPackageName", mPackageName, "lastTrackInformation");
            mPref.putValue("lastTrackName",mTrackName,"lastTrackInformation");
            Log.i("삼성앱 음원정보 결과값","가수 : " + mArtistName + " 제목 : " + mTrackName + " 음원저장경로 : " + mTrackFullPath + " 앱명 : " + mPackageName + " 시작점 : " + Long.toString(startPosition));
            mMusicKeyName = mArtistName + mTrackName;
            mMsg = mHandler.obtainMessage();
            if(mServiceData.isMyServiceRunning(MainService.class)) {

                if (mIsPlaying) {
                    mMsg.what = SEND_MUSIC_INFORMATION;
                    String musicInfo = new String("가수 : " + mArtistName + "\n" + "제목 : " + mTrackName + "\n" + "음원저장경로 : " + mTrackFullPath + "\n" + "앱명 : " + mPackageName);
                   Log.i("음악정보",musicInfo);
                    mMsg.obj = musicInfo;
                    mHandler.sendMessage(mMsg);
                    mPref.putValue("0", musicInfo, "음악 재생 정보");
                    if (!mServiceData.isMyServiceRunning(ListeningService.class)) {
                        context.startService(mIntent);
                    }

                    if (mServiceData.isMyServiceRunning(DecibelService.class)) {
                        context.stopService(mdBIntent);
                        mdBIntent = new Intent(context, DecibelService.class);
                        mdBIntent.putExtra("trackFullPath", mTrackFullPath);
                        mdBIntent.putExtra("position", startPosition);
                        mdBIntent.putExtra("keyName", mMusicKeyName);
                        context.startService(mdBIntent);
                    } else {
                        mdBIntent.putExtra("trackFullPath", mTrackFullPath);
                        mdBIntent.putExtra("position", startPosition);
                        mdBIntent.putExtra("keyName", mMusicKeyName);
                        context.startService(mdBIntent);
                    }
                    Log.e("음악재생여부", "재생중");
//            mSoundfile = new SoundFile();
//            try {
//                mSoundfile.create(mTrackFullPath);
//            }catch (final Exception e) {
//                Log.e("mSoundfile이 null", "사운드 파일 없음");
//            }
                } else {
                    mMsg.what = SEND_MUSIC_INFORMATION;
                    String musicInfo = new String("정지");
                    mMsg.obj = musicInfo;
                    mHandler.sendMessage(mMsg);
                    //Log.i("저장값", "lastTrackName : " + mTrackName + " lastPackageName : " + mPackageName);
                    mPref.putValue("0", musicInfo, "음악 재생 정보");
                    if (mServiceData.isMyServiceRunning(ListeningService.class)) {
                        context.stopService(mIntent);
                    }
                    //mdBIntent = new Intent(context, DecibelService.class);
                    context.stopService(mdBIntent);
                    Log.e("음악재생여부", "일시정지");
                }
            }
        } else {
            if(mServiceData.isMyServiceRunning(MainService.class)) {

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
                Log.i("다른앱 음원정보 결과값", "가수 : " + mArtistName + " 제목 : " + mTrackName + " 음원저장경로 : " + mTrackFullPath + " 앱명 : " + mPackageName);

                mMsg = mHandler.obtainMessage();
                if (mIsPlaying) {
                    mMsg.what = SEND_MUSIC_INFORMATION;
                    String musicInfo = new String("가수 : " + mArtistName + "\n" + "제목 : " + mTrackName + "\n" + "음원저장경로 : " + mTrackFullPath + "\n" + "앱명 : " + mPackageName);
                    mMsg.obj = musicInfo;
                    mHandler.sendMessage(mMsg);
                    mPref.putValue("0", musicInfo, "음악 재생 정보");
                    if (!mServiceData.isMyServiceRunning(ListeningService.class)) {
                        context.startService(mIntent);
                    }
//                mIntent = new Intent(context, DecibelService.class);
//                mIntent.putExtra("trackFullPath",mTrackFullPath);
//                mIntent.putExtra("position", ())
//                context.startService(mIntent);
                    Log.e("음악재생여부", "재생중");
//            mSoundfile = new SoundFile();
//            try {
//                mSoundfile.create(mTrackFullPath);
//            }catch (final Exception e) {
//                Log.e("mSoundfile이 null", "사운드 파일 없음");
//            }
                } else {
                    mMsg.what = SEND_MUSIC_INFORMATION;
                    String musicInfo = new String("정지");
                    mMsg.obj = musicInfo;
                    mHandler.sendMessage(mMsg);
                    //Log.i("저장값", "lastTrackName : " + mTrackName + " lastPackageName : " + mPackageName);
                    mPref.putValue("0", musicInfo, "음악 재생 정보");
                    if (mServiceData.isMyServiceRunning(ListeningService.class)) {
                        context.stopService(mIntent);
                    }
                    mIntent = new Intent(context, DecibelService.class);
                    context.stopService(mIntent);
                    Log.e("음악재생여부", "일시정지");
                }
            }
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


    }
}
