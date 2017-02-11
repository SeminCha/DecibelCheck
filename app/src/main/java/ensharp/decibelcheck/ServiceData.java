package ensharp.decibelcheck;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

/**
 * Created by Semin on 2017-02-11.
 */
public class ServiceData extends Application {

    private boolean isServiceRunning;

    public boolean getIsServiceRunning() {
        return isServiceRunning;
    }

    public void setServiceRunning(boolean isServiceRunning) {
        this.isServiceRunning = isServiceRunning;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
