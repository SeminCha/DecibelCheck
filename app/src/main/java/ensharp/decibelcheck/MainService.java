package ensharp.decibelcheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service {
    NotificationManager notificationManager;
    ServiceThread thread;
    Notification notification;

    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("서비스 onStartCommand","mainservice여기 옴");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.setIsServiceRun(true);
        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        thread.stopRunning();
        notificationManager.cancel(0);
        thread = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i("노티비코드","handleMessage");
            Intent intent = new Intent(MainService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Decibel Check")
                    .setContentText("서비스 On")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();

            notification.flags = Notification.FLAG_NO_CLEAR;

            notificationManager.notify(0, notification);

            Toast.makeText(MainService.this, "서비스 시작", Toast.LENGTH_SHORT).show();
        }
    };
}
