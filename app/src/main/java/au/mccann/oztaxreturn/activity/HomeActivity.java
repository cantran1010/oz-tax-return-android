package au.mccann.oztaxreturn.activity;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import au.mccann.oztaxreturn.R;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private DrawerLayout drawer;

    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

    }

    @Override
    protected void initData() {
        findViewById(R.id.drawer_button).setOnClickListener(this);

    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_button:
                drawer.openDrawer(GravityCompat.END);
                break;
        }

    }
}
