package ensharp.decibelcheck;

import android.content.Context;
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
    private static final int RUN= 0;
    private static final int PAUSE = 1;
    private long mBaseTime;
    private SharedPreferences mPref;
    private int mSeconds;
    private String mDecibels;
    private String mKeyName;

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

        try {
            mSoundFile.create(mTrackFullPath, mKeyName, mContext);
            //controlElapse(RUN);
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
        Log.i("시간측정","시작");
        switch (mode) {
            case RUN :
                mBaseTime = SystemClock.elapsedRealtime();
                myTimer.sendEmptyMessage(0);
                break;
            case PAUSE :
                myTimer.removeMessages(0);
                break;
        }
    }

    public String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        mMillis = now - mBaseTime + mStartPosition;
        //String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mMillis)),
                TimeUnit.MILLISECONDS.toSeconds(mMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mMillis)));
        mSeconds = (int) (mMillis/1000)%60;
        mDecibels = mPref.getValue(Integer.toString(mSeconds),"인식못함",mKeyName);
        return "현재 재생위치 : " + mSeconds + " / " + ms + " 현재 데시벨 : " + mDecibels;
    }

    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            MainActivity.setMainUiText("현재 데시벨", getTimeOut());
            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };
}
