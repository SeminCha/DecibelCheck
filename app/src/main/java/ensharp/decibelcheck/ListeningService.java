package ensharp.decibelcheck;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Semin on 2017-02-11.
 */
public class ListeningService extends Service {

    private long mBaseTime;
    private long mPauseTime;
    private Date mToday;
    private SimpleDateFormat mTodayFormat;
    private SharedPreferences mPref;
    private Message mMsg;
    private String mListeningTime;
    private long mMillis;
    private static final int RUN= 0;
    private static final int PAUSE = 1;
    private int mState;
    private static final int SEND_LISTENING_ELAPSE = 7;

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = new SharedPreferences(this);
        Log.i("리스닝서비스 onCreate", "listeningservice여기 옴");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("리스닝서비스 onStartCommand", "listeningservice여기 옴");
        controlElapse(RUN);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        controlElapse(PAUSE);
        Log.i("리스닝서비스 onDestroy 실행", "종료");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void controlElapse(int mode) {

        switch (mode) {

            case RUN :
                mBaseTime = SystemClock.elapsedRealtime();
                myTimer.sendEmptyMessage(0);
                break;
            case PAUSE :
                myTimer.removeMessages(0);
                //mPref.putValue("pauseTime",SystemClock.elapsedRealtime(),"todayInfo");
                //mPref.putValue("todayListeningTimeUI",mListeningTime,"todayInfo");
                mPref.putValue("todayListeningTime",mMillis,"todayInfo");
                mPauseTime = SystemClock.elapsedRealtime();
                break;
        }
    }

    public String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        mMillis = now - mBaseTime + mPref.getValue("todayListeningTime",0,"todayInfo");
        //String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mMillis),
                TimeUnit.MILLISECONDS.toMinutes(mMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mMillis)),
                TimeUnit.MILLISECONDS.toSeconds(mMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mMillis)));
        //Log.i("getTimeOut의 값",hms);
        return hms;
    }

    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            MainActivity.setMainUiText("음악 청취 시간", getTimeOut());
            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };
}
