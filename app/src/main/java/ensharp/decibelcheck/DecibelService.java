package ensharp.decibelcheck;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Semin on 2017-02-13.
 */
public class DecibelService extends Service {

    private DecibelServiceThread mDecibelServiceThread;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDecibelServiceThread = new DecibelServiceThread(intent.getExtras().getString("trackFullPath"),intent.getExtras().getLong("position"),intent.getExtras().getString("keyName"),this);
        mDecibelServiceThread.start();
        Log.i("데시벨서비스 onStartCommand","decibelservice여기 옴");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mDecibelServiceThread.stopRunning();
        Log.i("데시벨서비스 onDestroy 실행","종료");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
