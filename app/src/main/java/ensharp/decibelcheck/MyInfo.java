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
import android.widget.ImageButton;
import android.widget.ImageView;

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
        settingButton();
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
                    Log.i("설정","자동볼륨조정off");
                } else {
                    mAutoVolumeBtn.setImageResource(R.drawable.onswitch);
                    mPref.putValue("autovolume", true, "setting");
                    mIsAutoVolume = true;
                    Log.i("설정","자동볼륨조정on");
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
