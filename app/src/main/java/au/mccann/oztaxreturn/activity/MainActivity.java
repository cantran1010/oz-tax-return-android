package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.utils.TransitionScreen;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class), TransitionScreen.FADE_IN);
                break;
            case R.id.btn_register:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }

    }
}
