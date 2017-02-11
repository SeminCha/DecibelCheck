package ensharp.decibelcheck;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static TextView earphoneTxt;
    public static TextView bluetoothEarphoneTxt;
    public static TextView musicOnTxt;
    public static TextView elapseTxt;
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
    private ServiceData mServiceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("처음부터 시작", "onCreate 실행");
        earphoneTxt = (TextView) findViewById(R.id.earphoneTxt);
        //bluetoothEarphoneTxt = (TextView) findViewById(R.id.bluetoothTxt);
        musicOnTxt = (TextView) findViewById(R.id.musicOnTxt);
        elapseTxt = (TextView) findViewById(R.id.elapseTxt);
        //currentPlayingAppTxt = (TextView) findViewById(R.id.currentPlayingAppTxt);
        serviceBtn = (Button) findViewById(R.id.serviceBtn);
        final MainServiceThread thread = new MainServiceThread();
        mServiceData = (ServiceData) getApplication();
        pref = new SharedPreferences(this);
        //Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        //startActivityForResult(intent, 0);
        //Toast.makeText(this, "onCreate 실행", Toast.LENGTH_SHORT).show();
        setMainUiInfo();
        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mServiceData.isMyServiceRunning(MainService.class)) {
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
    }

    public void setMainUiInfo() {
        if (pref.getValue("0", false, "normalEarphone")) {
            earphoneTxt.setText("사용 중인 타입 : 일반 이어폰");
            Log.i("사용중인 이어폰 종류", "일반");
        } else {
            if (pref.getValue("0", false, "bluetoothEarphone")) {
                earphoneTxt.setText("사용 중인 타입 : 블루투스 이어폰");
                Log.i("사용중인 이어폰 종류", "블루투스");
            } else {
                earphoneTxt.setText("사용 안함");
                Log.i("사용중인 이어폰 종류", "사용안함");
            }
        }

        if (!mServiceData.isMyServiceRunning(MainService.class)) {
            serviceBtn.setText("서비스 종료");
            Log.i("서비스 러닝여부", "O");
        } else {
            serviceBtn.setText("서비스 시작");
            Log.i("서비스 러닝여부", "X");
        }

        musicOnTxt.setText(pref.getValue("0", "없음", "음악 재생 정보"));
        elapseTxt.setText(pref.getValue("todayListeningTimeUI", "00:00:00", "todayInfo"));
    }


    public static void setMainUiText(String textName, String textContent) {
        switch (textName) {
            case "이어폰":
                Log.i("메인으로 넘어온 값", textContent + "?");
                if (earphoneTxt != null) {
                    if (textContent.equals("O")) {
                        earphoneTxt.setText("사용 중인 타입 : " + "일반 " + textName);
                    } else {
                        earphoneTxt.setText("사용안함");
                    }
                }
                break;
            case "블루투스 이어폰":
                Log.i("메인으로 넘어온 값", textContent + "?");
                if (earphoneTxt != null) {
                    if (textContent.equals("O")) {
                        earphoneTxt.setText("사용 중인 타입 : " + textName);
                    } else {
                        earphoneTxt.setText("사용안함");
                    }
                }
                break;

            case "음악 재생 정보":
                Log.i("메인으로 넘어온 값", textContent + "?");
                if (musicOnTxt != null) {
                    musicOnTxt.setText(textContent);
                }
                break;
            case "음악 청취 시간":
                Log.i("메인으로 넘어온 값", textContent + "?");
                if (elapseTxt != null) {
                    elapseTxt.setText(textContent);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume 실행", Toast.LENGTH_SHORT).show();
    }
}
