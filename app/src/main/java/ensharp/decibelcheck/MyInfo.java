package ensharp.decibelcheck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Semin on 2017-02-20.
 */
public class MyInfo extends AppCompatActivity {

    private ImageView mEarphoneImage;
    private Bitmap mEarphonePicture;
    private SharedPreferences mPref;
    private boolean mIsAutoVolume;
    private boolean mIsBreakNotification;
    private ImageButton mAutoVolumeBtn;
    private ImageButton mBreakNotificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.settingstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("내 정보");
        mPref = new SharedPreferences(this);
        mEarphoneImage = (ImageView) findViewById(R.id.earphoneImg);
        mEarphonePicture = resizeImage("earphone", 190, 190);
        mEarphoneImage.setImageBitmap(getCircleBitmap(mEarphonePicture));
        //settingMyInfo();
        settingButton();
    }

    public void settingMyInfo() {
        TextView nameTxt = (TextView) findViewById(R.id.nameTxt);
        TextView ageTxt = (TextView) findViewById(R.id.ageTxt);
        TextView earphone_companyTxt = (TextView) findViewById(R.id.earphone_companyTxt);
        TextView earphone_modelTxt = (TextView) findViewById(R.id.earphone_modelTxt);
        TextView earphone_impedanceTxt = (TextView) findViewById(R.id.earphone_impedanceTxt);
        TextView earphone_soundpressureTxt = (TextView) findViewById(R.id.earphone_soundpressureTxt);
        Button maleBtn = (Button) findViewById(R.id.manBtn);
        Button femaleBtn = (Button) findViewById(R.id.womanBtn);

        nameTxt.setText(mPref.getValue("name", "성민경", "userinfo"));
        ageTxt.setText(mPref.getValue("age", "23", "userinfo"));
        earphone_companyTxt.setText(mPref.getValue("earphone_company", "LG", "userinfo"));
        earphone_modelTxt.setText(mPref.getValue("earphone_model", "HSS-100", "userinfo"));
        earphone_soundpressureTxt.setText(mPref.getValue("earphone_soundpressure", "98", "userinfo"));
        earphone_impedanceTxt.setText(mPref.getValue("earphone_impedance", "16", "userinfo"));

        if (mPref.getValue("sex", "male", "userinfo").equals("male")) {
             maleBtn.setBackgroundResource(R.drawable.chosen_button);
             femaleBtn.setBackgroundResource(R.drawable.not_chosen_button);
        } else {
            maleBtn.setBackgroundResource(R.drawable.not_chosen_button);
            femaleBtn.setBackgroundResource(R.drawable.chosen_button);
        }

        mEarphoneImage = (ImageView) findViewById(R.id.earphoneImg);
        mEarphonePicture = resizeImage(mPref.getValue("earphone_image","hss_100","userinfo"), 190, 190);
        mEarphoneImage.setImageBitmap(getCircleBitmap(mEarphonePicture));
    }

    public Bitmap resizeImage(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public void analysis_click(View view) {
        Intent intent = new Intent(getBaseContext(), WeeklyInfoActivity.class);
        startActivity(intent);
    }

    private void settingButton() {
        mAutoVolumeBtn = (ImageButton) findViewById(R.id.autoVolumeBtn);
        mBreakNotificationBtn = (ImageButton) findViewById(R.id.breakNotificationBtn);
        mIsAutoVolume = mPref.getValue("autovolume", true, "setting");
        mIsBreakNotification = mPref.getValue("breaknotification", true, "setting");

        if (mIsAutoVolume) {
            mAutoVolumeBtn.setImageResource(R.drawable.onswitch);
        } else {
            mAutoVolumeBtn.setImageResource(R.drawable.offswitch);
        }

        if (mIsBreakNotification) {
            mBreakNotificationBtn.setImageResource(R.drawable.onswitch);
        } else {
            mBreakNotificationBtn.setImageResource(R.drawable.offswitch);
        }

        mAutoVolumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsAutoVolume) {
                    mAutoVolumeBtn.setImageResource(R.drawable.offswitch);
                    mPref.putValue("autovolume", false, "setting");
                    mIsAutoVolume = false;
                    Log.i("설정", "자동볼륨조정off");
                } else {
                    mAutoVolumeBtn.setImageResource(R.drawable.onswitch);
                    mPref.putValue("autovolume", true, "setting");
                    mIsAutoVolume = true;
                    Log.i("설정", "자동볼륨조정on");
                }
            }
        });

        mBreakNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBreakNotification) {
                    mBreakNotificationBtn.setImageResource(R.drawable.offswitch);
                    mPref.putValue("breaknotification", false, "setting");
                    mIsBreakNotification = false;
                } else {
                    mBreakNotificationBtn.setImageResource(R.drawable.onswitch);
                    mPref.putValue("breaknotification", true, "setting");
                    mIsBreakNotification = true;
                }
            }
        });
    }
}
