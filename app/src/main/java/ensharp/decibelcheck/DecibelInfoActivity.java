package ensharp.decibelcheck;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Semin on 2017-02-28.
 */
public class DecibelInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_decibellinfo);
    }
}
