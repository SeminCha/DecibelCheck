package ensharp.decibelcheck;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

/**
 * Created by Semin on 2017-02-28.
 */
public class BreakNotifyService extends Service {

    Intent mIntent;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = new Intent(this, BreakNotifyActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            { startActivity(mIntent);
                //여기에 딜레이 후 시작할 작업들을 입력
            }
        }, 2000);// 0.5초 정도 딜레이를 준 후 시작


        return START_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
