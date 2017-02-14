package ensharp.decibelcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Semin on 2017-02-14.
 */
public class VolumeBroadcastReceiver extends BroadcastReceiver {

    private SharedPreferences mPref;
    private AudioManager mAudiomanager;
    private int mVolume;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPref = new SharedPreferences(context);
        mAudiomanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //Log.i("음량 조절 리시버 작동","작동");
        mVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
        //Log.i("현재볼륨",volume+" ");
        mPref.putValue("0",mVolume,"currentVolume");
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            MainActivity.setMainUiText("현재 음량", Integer.toString(mVolume));
        }
    };
}
