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
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (au.mccann.oztaxreturn.database.UserManager.checkLogin())
                    startActivity(HomeActivity.class, TransitionScreen.FADE_IN);
                else
                    startActivity(MainActivity.class, TransitionScreen.FADE_IN);
            }
        }, 1000);
    }

    @Override
    protected void resumeData() {

    }

}
