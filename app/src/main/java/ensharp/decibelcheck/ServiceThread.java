package ensharp.decibelcheck;

import android.os.Handler;
import android.util.Log;

/**
 * Created by Semin on 2017-01-27.
 */
public class ServiceThread extends Thread{

    Handler handler;
    boolean isRun;

    public ServiceThread() {}

    public boolean getIsServiceRun() {
        return isRun;
    }

    public void setIsServiceRun(boolean isRun) {
        this.isRun = isRun;
    }

    public ServiceThread(Handler handler) {
        this.handler = handler;
    }

    public void stopRunning() {
        synchronized (this) {
            Log.i("서비스 중지","stopRunning");
            this.isRun = false;
        }
    }

    public void run() {
        if(isRun) {
            Log.i("서비스 run","sendEmptyMessage");
            handler.sendEmptyMessage(0);
        }
    }
}
