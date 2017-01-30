package ensharp.decibelcheck;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static TextView normalEarphoneTxt;
    public TextView bluetoothEarphoneTxt;
    public TextView musicOnTxt;
    public TextView currentPlayingAppTxt;

    private static final String BLUETOOTH_HEADSET_ACTION = "android.bluetooth.headset.action.STATE_CHANGED";
    private static final String BLUETOOTH_HEADSET_STATE = "android.bluetooth.headset.extra.STATE";

    private static IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    private static BroadcastReceiver mBroadcastReceiver = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;

    private Button serviceBtn;
    public boolean isServiceOn;
    public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("처음부터 시작", "onCreate 실행");
        normalEarphoneTxt = (TextView) findViewById(R.id.earphoneTxt);
        bluetoothEarphoneTxt = (TextView) findViewById(R.id.bluetoothTxt);
        musicOnTxt = (TextView) findViewById(R.id.musicOnTxt);
        currentPlayingAppTxt = (TextView) findViewById(R.id.currentPlayingAppTxt);
        serviceBtn = (Button) findViewById(R.id.serviceBtn);
        final ServiceThread thread = new ServiceThread();
        pref = new SharedPreferences(this);

        Toast.makeText(this, "onCreate 실행", Toast.LENGTH_SHORT).show();
        if (pref.getValue("0", false, "normalEarphone")) {
            normalEarphoneTxt.setText("O");
        } else {
            normalEarphoneTxt.setText("X");
        }

        if (pref.getValue("0", false, "bluetoothEarphone")) {
            bluetoothEarphoneTxt.setText("O");
        } else {
            bluetoothEarphoneTxt.setText("X");
        }

        if (pref.getValue("0", false, "service")) {
            serviceBtn.setText("서비스 종료");
        } else {
            serviceBtn.setText("서비스 시작");
        }
        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pref.getValue("0", false, "service")) {
                    Log.i("isRun여부", "true로 통과");
                    //Toast.makeText(getApplicationContext(),"서비스 시작",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    serviceBtn.setText("서비스 종료");
                    startService(intent);
                } else {
                    //Toast.makeText(getApplicationContext(), "서비스 종료", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    serviceBtn.setText("서비스 시작");
                    stopService(intent);
                }
            }
        });

        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (manager.isMusicActive()) {
            musicOnTxt.setText("Yes");
        } else {
            musicOnTxt.setText("No");
        }

//        BroadcastReceiver headSetConnectReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final String action = intent.getAction();
//                int headSetState;
//                if (action.equals(intent.ACTION_HEADSET_PLUG)) {
//                    boolean isEarphoneOn = (intent.getIntExtra("state", 0) > 0) ? true : false;
//                    if (isEarphoneOn) {
//                        Log.e("일반 이어폰 log", "Earphone is plugged");
//                        normalEarphoneTxt.setText("Yes");
//                    } else {
//                        Log.e("일반 이어폰 log", "Earphone is unPlugged");
//                        normalEarphoneTxt.setText("No");
//                    }
//                } else if (intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
//                    headSetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
//                    Log.i("headSetState", Integer.toString(headSetState));
//                    if (headSetState == 0) {
//                        bluetoothEarphoneTxt.setText("No");
//                    } else if (headSetState == 1) {
//                        bluetoothEarphoneTxt.setText("연결중");
//                    } else {
//                        bluetoothEarphoneTxt.setText("Yes");
//                    }
//                }
//            }
//        };

//        IntentFilter intentFilter = new IntentFilter();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
//            intentFilter.addAction(BLUETOOTH_HEADSET_ACTION);
//            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
//        } else {
//            intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
//            intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
//            intentFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
//            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
//        }
//
//        registerReceiver(headSetConnectReceiver, intentFilter);
    }


    public static void setMainUiText(String textName, String textContent) {
        switch(textName) {

            case "이어폰" :
                Log.i("메인으로 넘어온 값", textContent + "?");
                if(normalEarphoneTxt != null) {
                    normalEarphoneTxt.setText(textContent);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume 실행", Toast.LENGTH_SHORT).show();
    }
        //BluetoothReceiver bluetoothReceiver = new BluetoothReceiver(this);
// BluetoothAdapter 인스턴스를 얻는다
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        // 프로필 프록시에 연결한다
//        mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.HEADSET);
//        mBroadcastReceiver = new BroadcastReceiver() {
//            private AudioManager localAudioManager;
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//                //int systemVolume = audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
//
//                boolean isEarphoneOn = (intent.getIntExtra("state", 0) > 0) ? true : false;
//
//                if (isEarphoneOn) {
//                    Log.e("일반 이어폰 log", "Earphone is plugged");
//                    normalEarphoneTxt.setText("Yes");
//                } else {
//                    Log.e("일반 이어폰 log", "Earphone is unPlugged");
//                    normalEarphoneTxt.setText("No");
//                }
//            }
//        };
//        registerReceiver(mBroadcastReceiver, mIntentFilter);


//    public boolean isNamedProcessRunning(String processName){
//        if (processName == null)
//            return false;
//
//        ActivityManager manager =
//                (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo process : processes)
//        {
//            if (processName.equals(process.processName))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

//
//
//    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
//        // 프로필 프록시와 연결되면 호출된다
//        public void onServiceConnected(int profile, BluetoothProfile proxy) {
//            if (profile == BluetoothProfile.HEADSET) {
//                mBluetoothHeadset = (BluetoothHeadset) proxy;
//                // 연결되어 있는 헤드셋 목록을 얻는다
//              List<BluetoothDevice> connectedDevices = mBluetoothHeadset.getConnectedDevices();
//                for (BluetoothDevice device : connectedDevices) {
//                    device.getName();
//                    device.getAddress();
//                    device.getName();
//                    Log.i("디바이스 정보",device.getName().toString() + " " + device.getAddress().toString() + " " + device.getName().toString());
//                    mBluetoothHeadset.getConnectionState(device);
//                    if (mBluetoothHeadset.isAudioConnected(device)) {
//                        // 음성 전송 연결이 활성화되어 있다
//                        bluetoothEarphoneTxt.setText("Yes");
//                    }
//                }
//            }
//        }
//
//        // 프로필 프록시와의 연결이 끊어지면 호출된다
//        public void onServiceDisconnected(int profile) {
//            if (profile == BluetoothProfile.HEADSET) {
//                mBluetoothHeadset = null;
//                bluetoothEarphoneTxt.setText("No");
//            }
//        }
//    };

    //        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        List l = am.getRunningAppProcesses();
//        Iterator i = l.iterator();
//        PackageManager pm = this.getPackageManager();
//        while (i.hasNext()) {
//            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
//            try {
//                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
//                Log.i("앱 목록", c.toString());
//                name = name + "\n" + c.toString();
//
//            } catch (Exception e) {
//                //Name Not FOund Exception
//            }
//        }
//        currentPlayingAppTxt.setText(name);
}
