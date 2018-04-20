package au.mccann.oztaxreturn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.fragment.ContactFragment;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.fragment.NotificationFragment;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.TextViewCustom;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private ImageView imgHome, imgContact, imgNotification, imgBack, imgNavigation;
    private TextViewCustom tvHome, tvContact, tvNotification, tvTitle;

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

        imgNavigation = findViewById(R.id.img_navigation);

        imgBack = findViewById(R.id.img_back);
        tvTitle = findViewById(R.id.tv_title);
    }

    @Override
    protected void initData() {

        openFragment(R.id.layout_container, HomeFragment.class, false, new Bundle(), TransitionScreen.NON);

        findViewById(R.id.img_navigation).setOnClickListener(this);
        findViewById(R.id.layout_home).setOnClickListener(this);
        findViewById(R.id.layout_contact).setOnClickListener(this);
        findViewById(R.id.layout_notification).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    @Override
    protected void resumeData() {

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
//            super.onBackPressed();
        } else {
//            super.onBackPressed();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() == 0) {
//            super.onBackPressed();
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            if (doubleBackToExitPressedOnce) {
                this.finish();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            Utils.showLongToast(this, getString(R.string.back_to_exit), false, false);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {

            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            LogUtils.d(TAG, "fragmentTag : " + fragmentTag);
            if (fragmentTag.equals("au.mccann.oztaxreturn.fragment.HomeFragment")
                    || fragmentTag.equals("au.mccann.oztaxreturn.fragment.ContactFragment")
                    || fragmentTag.equals("au.mccann.oztaxreturn.fragment.NotificationFragment")) {

                if (doubleBackToExitPressedOnce) {
                    this.finish();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;

                Utils.showLongToast(this, getString(R.string.back_to_exit), false, false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            } else {
                fragmentManager.popBackStack();
            }

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

    public void appBarVisibility(boolean navigationVisibility, boolean backVisibility) {
        if (navigationVisibility) imgNavigation.setVisibility(View.VISIBLE);
        else
            imgNavigation.setVisibility(View.INVISIBLE);

        if (backVisibility) imgBack.setVisibility(View.VISIBLE);
        else
            imgBack.setVisibility(View.INVISIBLE);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_navigation:
                drawer.openDrawer(GravityCompat.END);
                break;

            case R.id.layout_home:
                updateMenu(1);
                openFragment(R.id.layout_container, HomeFragment.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.layout_contact:
                updateMenu(2);
                openFragment(R.id.layout_container, ContactFragment.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.layout_notification:
                updateMenu(3);
                openFragment(R.id.layout_container, NotificationFragment.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.img_back:
                onBackPressed();
                break;
        }

    }
}
