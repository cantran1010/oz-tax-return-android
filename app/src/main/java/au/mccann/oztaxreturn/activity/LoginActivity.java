package au.mccann.oztaxreturn.activity;

import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.EdittextCustom;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EdittextCustom edtUsername, edtPassword;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;

    }

    @Override
    protected void initView() {
        edtUsername = findViewById(R.id.edt_user_name);
        edtPassword = findViewById(R.id.edt_password);

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

        }
    }
}
