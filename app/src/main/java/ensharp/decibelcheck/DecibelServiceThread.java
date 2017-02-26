package ensharp.decibelcheck;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by Semin on 2017-02-13.
 */
public class DecibelServiceThread extends Thread {

    private SoundFile mSoundFile;
    private String mTrackFullPath;
    private long mStartPosition;
    private Context mContext;
    private long mMillis;
    private static final int RUN = 0;
    private static final int PAUSE = 1;
    private long mBaseTime;
    private SharedPreferences mPref;
    private int mSeconds;
    private String mDecibels;
    private String mKeyName;
    private AudioManager mAudioManager;
    private static final int MEASURE_DECIBEL = 91;

    public DecibelServiceThread() {
    }

    public DecibelServiceThread(String trackFullPath, long startPosition, String keyName, Context context) {
        this.mTrackFullPath = trackFullPath;
        this.mStartPosition = startPosition;
        this.mContext = context;
        this.mKeyName = keyName;
    }

    public void stopRunning() {
        synchronized (this) {
            controlElapse(PAUSE);
            Log.i("서비스 중지", "stopRunning");
        }
    }

    public void run() {
        mSoundFile = new SoundFile();
        mPref = new SharedPreferences(mContext);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        try {
            controlElapse(RUN);
            if (mPref.getValue(Integer.toString(mSeconds), "인식못함", mKeyName).equals("인식못함")) {
                mSoundFile.create(mTrackFullPath, mKeyName, mContext);
            }
        } catch (final Exception e) {
            Log.e("mSoundfile이 null", "사운드 파일 없음");
        }
        Log.i("서비스 run", "sendEmptyMessage");

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
    }

    public void controlElapse(int mode) {
        Log.i("시간측정", "시작");
        switch (mode) {
            case RUN:
                mBaseTime = SystemClock.elapsedRealtime();
                myTimer.sendEmptyMessage(0);
                break;
            case PAUSE:
                myTimer.removeMessages(0);
                Log.i("PAUSE", "보냄");
                break;
        }
    }

    public String getTimeOut() {
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        mMillis = now - mBaseTime + mStartPosition;
        //String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mMillis)),
                TimeUnit.MILLISECONDS.toSeconds(mMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mMillis)));
        mSeconds = (int) mMillis / 1000;
        mDecibels = mPref.getValue(Integer.toString(mSeconds), "54", mKeyName);
        Log.i("전 mDecibels=",mDecibels);
        mDecibels = getDecibel();
        Log.i("후 mDecibels=",mDecibels);
        //return "현재 재생위치 : " + mSeconds + " / " + ms + " 현재 데시벨 : " + mDecibels;
        return mDecibels;
    }

    public String getDecibel() {
        //W = (V * V)/R
        //dB = 10log(1/mW)
        double V, mW, dB, temp;
        int decibel;
        int currentVolume = Integer.parseInt(VolumeBroadcastReceiver.currentVolume);
        boolean isAutoControl = mPref.getValue("autovolume", false, "setting");
        //Log.i("getDecibel()함수", currentVolume+"");
        if (!isAutoControl) {
            //Log.i("볼륨컨트롤x", "dd");
            V = Double.parseDouble(mPref.getValue(VolumeBroadcastReceiver.currentVolume, "13.28", "Note5"));
            //Log.i("음량전압값 변화", " V = " + V + "  볼륨 : " + mPref.getValue("0", "5", "currentVolume"));
            mW = ((V * V) / Double.parseDouble(mPref.getValue("ohm", "35", "earphone"))) / 1000;
            dB = Math.round(10 * Math.log10(1 / mW));
            dB = Double.parseDouble(mPref.getValue("spl", "97", "earphone")) - dB;
            //이제부터 음원에 대한 계산
            temp = MEASURE_DECIBEL - Double.parseDouble(mDecibels);
            dB = Math.round(dB - temp);
            decibel = (int) dB;
            return Integer.toString(decibel);
        } else {
            //Log.i("볼륨컨트롤O", "dd");
            V = Double.parseDouble(mPref.getValue(Integer.toString(currentVolume), "13.28", "Note5"));
            //Log.i("음량전압값 변화", " V = " + V);
            mW = ((V * V) / Double.parseDouble(mPref.getValue("ohm", "35", "earphone"))) / 1000;
            //Log.i("음량전압값 변화", " mW = " + mW);
            dB = Math.round(10 * Math.log10(1 / mW));
            //Log.i("음량전압값 변화", " dB = " + dB);
            dB = Double.parseDouble(mPref.getValue("spl", "97", "earphone")) - dB;
            //Log.i("음량전압값 변화", " dB = " + dB);
            //이제부터 음원에 대한 계산
            temp = MEASURE_DECIBEL - Double.parseDouble(mDecibels);
            //Log.i("temp=",temp+" ");
            dB = Math.round(dB - temp);
            //Log.i("dB=",dB+" ");
            if (dB > 80) {
                //Log.i("볼륨 80 이상", "줄이기");
                currentVolume--;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
            }
            decibel = (int) dB;
            return Integer.toString(decibel);
        }

    }

    Handler myTimer = new Handler() {
        public void handleMessage(Message msg) {
            MainActivity.setMainUiText("현재 데시벨", getTimeOut());
            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            //Log.i("RUN","작동중");
            myTimer.sendEmptyMessage(0);
        }
    };
}
