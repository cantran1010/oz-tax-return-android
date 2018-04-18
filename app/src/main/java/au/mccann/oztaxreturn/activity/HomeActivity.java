package au.mccann.oztaxreturn.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.fragment.Deduction;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.fragment.IncomeOther;
import au.mccann.oztaxreturn.fragment.NotificationFragment;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.view.TextViewCustom;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private DrawerLayout drawer;
    private ImageView imgHome, imgContact, imgNotification;
    private TextViewCustom tvHome, tvContact, tvNotification;

    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        drawer = findViewById(R.id.drawer_layout);
        imgHome = findViewById(R.id.img_home_menu1);
        imgContact = findViewById(R.id.img_home_menu2);
        imgNotification = findViewById(R.id.img_home_menu3);

        tvHome = findViewById(R.id.tv_home_menu1);
        tvContact = findViewById(R.id.tv_home_menu2);
        tvNotification = findViewById(R.id.tv_home_menu3);
    }

    @Override
    protected void initData() {

        showFragment(R.id.layout_container, HomeFragment.class, false, new Bundle(), TransitionScreen.NON);

        findViewById(R.id.drawer_button).setOnClickListener(this);
        findViewById(R.id.layout_home).setOnClickListener(this);
        findViewById(R.id.layout_contact).setOnClickListener(this);
        findViewById(R.id.layout_notification).setOnClickListener(this);
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

    private void updateMenu(int position) {

        imgHome.setImageResource(R.drawable.home_coin_off);
        imgContact.setImageResource(R.drawable.home_contact_off);
        imgNotification.setImageResource(R.drawable.home_notification_off);

        tvHome.setTextColor(ContextCompat.getColor(this, R.color.white));
        tvContact.setTextColor(ContextCompat.getColor(this, R.color.white));
        tvNotification.setTextColor(ContextCompat.getColor(this, R.color.white));

        switch (position) {

            case 1:
                imgHome.setImageResource(R.drawable.home_coin_on);
                tvHome.setTextColor(ContextCompat.getColor(this, R.color.app_bg));
                break;

            case 2:
                imgContact.setImageResource(R.drawable.home_contact_on);
                tvContact.setTextColor(ContextCompat.getColor(this, R.color.app_bg));
                break;

            case 3:
                imgNotification.setImageResource(R.drawable.home_notification_on);
                tvNotification.setTextColor(ContextCompat.getColor(this, R.color.app_bg));
                break;

        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_button:
                drawer.openDrawer(GravityCompat.END);
                break;

            case R.id.layout_home:
                updateMenu(1);
                showFragment(R.id.layout_container, IncomeOther.class, false, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.layout_contact:
                updateMenu(2);
                showFragment(R.id.layout_container, Deduction.class, false, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.layout_notification:
                updateMenu(3);
                showFragment(R.id.layout_container, NotificationFragment.class, false, new Bundle(), TransitionScreen.FADE_IN);
                break;
        }

    }
}
