package ensharp.decibelcheck;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static TextView earphoneTxt;
    public static TextView bluetoothEarphoneTxt;
    public static TextView musicOnTxt;
    public static TextView elapseTxt;
    public static TextView decibelTxt;
    public static TextView volumeTxt;
    public static TextView isPlayingTxt;

    private static final String BLUETOOTH_HEADSET_ACTION = "android.bluetooth.headset.action.STATE_CHANGED";
    private static final String BLUETOOTH_HEADSET_STATE = "android.bluetooth.headset.extra.STATE";

    private static IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    private static final int LIMIT_DECIBEL = 80;
    private static BroadcastReceiver mBroadcastReceiver = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;

    private ImageButton decibelInfoBtn;
    private Button serviceBtn;
    public boolean isServiceOn;
    public SharedPreferences pref;
    private ServiceData mServiceData;
    public static ServiceData sServiceData;
    private Context mContext;
    private AudioManager mAudiomanager;
    public static FrameLayout mRedCircleLayout;
    public static FrameLayout mSkyCircleLayout;
    public static FrameLayout mNormalCircleLayout;
    public static FrameLayout mCircleParentLayout;
    private Toolbar mToolbar;

    private static final String TAG = "AppPermission";

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("처음부터 시작", "onCreate 실행");
        earphoneTxt = (TextView) findViewById(R.id.earphoneTxt);
        //bluetoothEarphoneTxt = (TextView) findViewById(R.id.bluetoothTxt);
        //musicOnTxt = (TextView) findViewById(R.id.musicOnTxt);
        elapseTxt = (TextView) findViewById(R.id.elapseTxt);
        decibelTxt = (TextView) findViewById(R.id.decibelsTxt);
        volumeTxt = (TextView) findViewById(R.id.volumeTxt);
        isPlayingTxt = (TextView) findViewById(R.id.isPlayingTxt);
        mNormalCircleLayout = (FrameLayout) findViewById(R.id.normalCircleLayout);
        mSkyCircleLayout = (FrameLayout) findViewById(R.id.skyCircleLayout);
        mRedCircleLayout = (FrameLayout) findViewById(R.id.redCircleLayout);
        mCircleParentLayout = (FrameLayout) findViewById(R.id.circleParentLayout);
        decibelInfoBtn = (ImageButton) findViewById(R.id.decibelsInfoBtn);
        serviceBtn = (Button) findViewById(R.id.serviceBtn);
        mAudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mContext = this;
        mServiceData = new ServiceData(mContext);
        sServiceData = new ServiceData(mContext);
        pref = new SharedPreferences(this);
        setMainUiInfo();
        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mServiceData.isMyServiceRunning(MainService.class)) {
                    Log.i("isRun여부", "true로 통과");
                    //Toast.makeText(getApplicationContext(),"서비스 시작",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    serviceBtn.setText("서비스 종료");
                    changeCircle("sky");
                    startService(intent);
                } else {
                    //Toast.makeText(getApplicationContext(), "서비스 종료", Toast.LENGTH_SHORT).show();
                    Intent intent_mainservice = new Intent(MainActivity.this, MainService.class);
                    Intent intent_listeningservice = new Intent(MainActivity.this, ListeningService.class);
                    Intent intent_decibelservice = new Intent(MainActivity.this, DecibelService.class);
                    stopService(intent_mainservice);
                    stopService(intent_listeningservice);
                    stopService(intent_decibelservice);
                    serviceBtn.setText("서비스 시작");
                    changeCircle("normal");
                }
            }
        });
        decibelInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DecibelInfoActivity.class);
                startActivity(intent);
            }
        });

        decibelDataSave();
        TextView mainTitleTxt = (TextView) findViewById(R.id.mainTitleTxt);
        mainTitleTxt.setText("Guardear");
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mToolbar.setTitle(null);
        //mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        checkPermission();

    }

    public void decibelDataSave() {
        if (pref.getValue("0", "0", "Note5") == "0") {
            for (int i = 0; i < 16; i++) {
                if (i == 0) {
                    pref.putValue(Integer.toString(i), "3.7", "Note5");
                } else if (i == 1) {
                    pref.putValue(Integer.toString(i), "4.78", "Note5");
                } else if (i == 2) {
                    pref.putValue(Integer.toString(i), "5.11", "Note5");
                } else if (i == 3) {
                    pref.putValue(Integer.toString(i), "6.27", "Note5");
                } else if (i == 4) {
                    pref.putValue(Integer.toString(i), "8.39", "Note5");
                } else if (i == 5) {
                    pref.putValue(Integer.toString(i), "13.28", "Note5");
                } else if (i == 6) {
                    pref.putValue(Integer.toString(i), "20.42", "Note5");
                } else if (i == 7) {
                    pref.putValue(Integer.toString(i), "31.73", "Note5");
                } else if (i == 8) {
                    pref.putValue(Integer.toString(i), "44.92", "Note5");
                } else if (i == 9) {
                    pref.putValue(Integer.toString(i), "55.75", "Note5");
                } else if (i == 10) {
                    pref.putValue(Integer.toString(i), "78", "Note5");
                } else if (i == 11) {
                    pref.putValue(Integer.toString(i), "110.43", "Note5");
                } else if (i == 12) {
                    pref.putValue(Integer.toString(i), "176", "Note5");
                } else if (i == 13) {
                    pref.putValue(Integer.toString(i), "279", "Note5");
                } else if (i == 14) {
                    pref.putValue(Integer.toString(i), "443", "Note5");
                } else if (i == 15) {
                    pref.putValue(Integer.toString(i), "558", "Note5");
                }
            }
            pref.putValue("ohm", "35", "earphone");
            pref.putValue("spl", "97", "earphone");
        }

    }

    public void setMainUiInfo() {
        if (pref.getValue("0", false, "normalEarphone")) {
            earphoneTxt.setText("Connected");
            Log.i("사용중인 이어폰 종류", "일반");
        } else {
            if (pref.getValue("0", false, "bluetoothEarphone")) {
                earphoneTxt.setText("Connected");
                Log.i("사용중인 이어폰 종류", "블루투스");
            } else {
                earphoneTxt.setText("Disconnected");
                Log.i("사용중인 이어폰 종류", "사용안함");
            }
        }

        if (!mServiceData.isMyServiceRunning(MainService.class)) {
            serviceBtn.setText("서비스 시작");
            changeCircle("normal");
            Log.i("서비스 러닝여부", "X");
        } else {
            serviceBtn.setText("서비스 종료");
            changeCircle("sky");
            Log.i("서비스 러닝여부", "O");
        }

        if (!mServiceData.isMyServiceRunning(ListeningService.class)) {
            //musicOnTxt.setText(pref.getValue("0", "없음", "음악 재생 정보"));
            isPlayingTxt.setText("Stop");
            elapseTxt.setText(mServiceData.convertLongToHms(pref.getValue("todayListeningTime", 0, "todayInfo")));
            if (decibelTxt != null) {
                decibelTxt.setText("0");
            }
            Log.i("리스닝 서비스 러닝여부", "X");
        } else {
            //musicOnTxt.setText(pref.getValue("0", "없음", "음악 재생 정보"));
            isPlayingTxt.setText("Playing");
            Log.i("서비스 러닝여부", "O");
        }

        if (!mServiceData.isMyServiceRunning(DecibelService.class)) {
            decibelTxt.setText("0");
        }

        int volume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        VolumeBroadcastReceiver.currentVolume = Integer.toString(volume);
        volumeTxt.setText(Integer.toString(volume));
    }


    public static void setMainUiText(String textName, String textContent) {
        switch (textName) {
            case "이어폰":
                Log.i("메인으로 넘어온 값", textContent + "?");
                if (earphoneTxt != null) {
                    if (textContent.equals("O")) {
                        earphoneTxt.setText("Connected");
                    } else {
                        earphoneTxt.setText("Disconnected");
                    }
                }
                break;
            case "블루투스 이어폰":
                Log.i("메인으로 넘어온 값", textContent + "?");
                if (earphoneTxt != null) {
                    if (textContent.equals("O")) {
                        earphoneTxt.setText("Connected");
                    } else {
                        earphoneTxt.setText("Disconnected");
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
                //Log.i("메인으로 넘어온 값", textContent + "?");
                if (elapseTxt != null) {
                    elapseTxt.setText(textContent);
                }
                break;
            case "현재 데시벨":
                //Log.i("메인으로 넘어온 값", textContent + "?");
                if (decibelTxt != null) {
                    if (sServiceData.isMyServiceRunning(MainService.class)) {
                        if (Integer.parseInt(textContent) > LIMIT_DECIBEL) {
                            changeCircle("red");
                        } else {
                            changeCircle("sky");
                        }
                        //Log.i("뜨는 값",textContent);
                        decibelTxt.setText(textContent);
                    } else {
                        changeCircle("normal");
                    }
                }
                break;
            case "현재 음량":
                if (volumeTxt != null) {
                    volumeTxt.setText(textContent);
                }
                break;
        }
    }

    public static void changeCircle(String mode) {
        if (mode.equals("normal")) {
            mNormalCircleLayout.setVisibility(View.VISIBLE);
            mSkyCircleLayout.setVisibility(View.INVISIBLE);
            mRedCircleLayout.setVisibility(View.INVISIBLE);
        } else if (mode.equals("sky")) {
            mNormalCircleLayout.setVisibility(View.INVISIBLE);
            mSkyCircleLayout.setVisibility(View.VISIBLE);
            mRedCircleLayout.setVisibility(View.INVISIBLE);
        } else {
            mNormalCircleLayout.setVisibility(View.INVISIBLE);
            mSkyCircleLayout.setVisibility(View.INVISIBLE);
            mRedCircleLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume 실행", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //내 정보 페이지
        if (id == R.id.action_personal_info) {
            Intent intent = new Intent(this, MyInfo.class);
            startActivity(intent);
            return true;
        }
        //청력측정 페이지
        else if (id == R.id.action_hearing_test) {

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Permission check.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        Log.i(TAG, "CheckPermission : " + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            Log.e(TAG, "permission deny");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                } else {
                    Log.d(TAG, "Permission always deny");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

}
