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

    private static final String BLUETOOTH_HEADSET_ACTION = "android.bluetooth.headset.action.STATE_CHANGED";
    public SharedPreferences pref;
    private static final int SEND_THREAD_START_MESSAGE = 0;
    private static final int SEND_THREAD_STOP_MESSAGE = 1;
    private static final int SEND_THREAD_NORMALEARPHONE_PLUGGED = 2;
    private static final int SEND_THREAD_NORMALEARPHONE_UNPLUGGED = 3;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED = 4;
    private static final int SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED = 5;

    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pref = new SharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("서비스 onStartCommand","mainservice여기 옴");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler, this);
        thread.setIsServiceRun(true);
        thread.start();
        pref.putValue("0",true,"service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        thread.stopRunning();
        notificationManager.cancel(0);
        //unregisterReceiver(headSetConnectReceiver);
        thread = null;
        pref.putValue("0",false,"service");
        Log.i("onDestroy 실행","종료");
        Toast.makeText(getApplicationContext(), "서비스 종료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i("노티비코드", "handleMessage");
            switch (msg.what) {

                case SEND_THREAD_START_MESSAGE:
                    Intent intent = new Intent(MainService.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MainService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    notification = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Decibel Check")
                            .setContentText("서비스 On")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .build();

                    notification.flags = Notification.FLAG_NO_CLEAR;
                    notificationManager.notify(0, notification);
                    break;

                case SEND_THREAD_STOP_MESSAGE:
                    break;

                case SEND_THREAD_NORMALEARPHONE_PLUGGED :
                    MainActivity.setMainUiText("이어폰","O");
                    break;

                case SEND_THREAD_NORMALEARPHONE_UNPLUGGED :
                    MainActivity.setMainUiText("이어폰","X");
                    break;

                case SEND_THREAD_BLUETOOTHEARPHONE_CONNECTED :
                    MainActivity.setMainUiText("블루투스 이어폰","O");
                    break;

                case SEND_THREAD_BLUETOOTHEARPHONE_UNCONNECTED :
                    MainActivity.setMainUiText("블루투스 이어폰","X");
                    break;
            }
            Toast.makeText(MainService.this, "서비스 시작", Toast.LENGTH_SHORT).show();
        }
    }


//    BroadcastReceiver headSetConnectReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            int headSetState;
//            if (action.equals(intent.ACTION_HEADSET_PLUG)) {
//                boolean isEarphoneOn = (intent.getIntExtra("state", 0) > 0) ? true : false;
//                if (isEarphoneOn) {
//                    Log.e("일반 이어폰 log", "Earphone is plugged");
//                    pref.putValue("0",true,"normalEarphone");
//                    //normalEarphoneTxt.setText("Yes");
//                } else {
//                    Log.e("일반 이어폰 log", "Earphone is unPlugged");
//                    pref.putValue("0",false,"normalEarphone");
//                    //normalEarphoneTxt.setText("No");
//                }
//            } else if (intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
//                headSetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
//                Log.i("headSetState", Integer.toString(headSetState));
//                if (headSetState == 0) {
//                    Log.e("블루투스 이어폰 log", "Earphone is unplugged");
//                    pref.putValue("0",false,"bluetoothEarphone");
//                    //bluetoothEarphoneTxt.setText("No");
//                } else if (headSetState == 1) {
//                    Log.e("블루투스 이어폰 log", "Earphone is plugging");
//                    pref.putValue("0",false,"bluetoothEarphone");
//                    //bluetoothEarphoneTxt.setText("연결중");
//                } else {
//                    Log.e("블루투스 이어폰 log", "Earphone is plugged");
//                    pref.putValue("0",true,"bluetoothEarphone");
//                    //bluetoothEarphoneTxt.setText("Yes");
//                }
//            }
//        }
//    };


}
