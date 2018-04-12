package au.mccann.oztaxreturn.activity;

import android.os.Handler;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.utils.TransitionScreen;

/**
 * Created by LongBui on 4/12/18.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.splash_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(MainActivity.class, TransitionScreen.RIGHT_TO_LEFT);
            }
        }, 2000);
    }

    @Override
    protected void resumeData() {

    }

}
