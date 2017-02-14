package ensharp.decibelcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Semin on 2017-02-11.
 */
public class DateChangedBroadcastReceiver extends BroadcastReceiver {

    private Date mToday;
    private SimpleDateFormat mDateFormat;
    private SharedPreferences mPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(Intent.ACTION_DATE_CHANGED.equals(action)) {
            mToday = new Date();
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            mPref = new SharedPreferences(context);

            mPref.putValue("todayDate",mDateFormat.toString(),"todayInfo");
            mPref.putValue("todayListeningTime",0,"todayInfo");
            Log.i("날짜변경브로드캐스트리시버 작동","todayDate : " + mDateFormat.toString());
        }
    }
}
