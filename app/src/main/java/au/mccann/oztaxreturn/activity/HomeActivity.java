package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.ContactFragment;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.fragment.NotificationFragment;
import au.mccann.oztaxreturn.fragment.review.family.ReviewFamilyHealthDependantsFragment;
import au.mccann.oztaxreturn.fragment.review.family.ReviewFamilyHealthMedicareFragment;
import au.mccann.oztaxreturn.fragment.review.family.ReviewFamilyHealthPrivateFragment;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationA;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationB;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationC;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.TextViewCustom;
import io.realm.Realm;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private ImageView imgHome, imgContact, imgNotification, imgBack, imgNavigation, imgLogout;
    private TextViewCustom tvHome, tvContact, tvNotification, tvTitle, tvReviewPersonalName, tvReviewPersonalBank, tvReviewPersonalEducation, tvVersion;
    private ExpandableLayout expPersonalLayout, expIncomesLayout, expDeductionsLayout, expFamilyLayout;
    private RelativeLayout homeNavigation, reviewNavigation;
    private ApplicationResponse applicationResponse;
    private TextViewCustom tvAppName, tvYear;

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

        expPersonalLayout = findViewById(R.id.layout_expandable_personal);
        expIncomesLayout = findViewById(R.id.layout_expandable_income);
        expDeductionsLayout = findViewById(R.id.layout_expandable_deductions);
        expFamilyLayout = findViewById(R.id.layout_expandable_family_health);

        findViewById(R.id.layout_personal).setOnClickListener(this);
        findViewById(R.id.layout_incomes).setOnClickListener(this);
        findViewById(R.id.layout_deductions).setOnClickListener(this);
        findViewById(R.id.layout_family).setOnClickListener(this);

        findViewById(R.id.tv_review_family_health_dependants).setOnClickListener(this);
        findViewById(R.id.tv_review_family_health_medicare).setOnClickListener(this);
        findViewById(R.id.tv_review_family_health_private).setOnClickListener(this);
        findViewById(R.id.tv_review_family_health_spouse).setOnClickListener(this);

        findViewById(R.id.tv_review_personal_name).setOnClickListener(this);
        findViewById(R.id.tv_review_personal_bank).setOnClickListener(this);
        findViewById(R.id.tv_review_personal_education).setOnClickListener(this);
        findViewById(R.id.img_logout).setOnClickListener(this);

        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_privacy).setOnClickListener(this);
        findViewById(R.id.tv_terms).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        findViewById(R.id.layout_rate).setOnClickListener(this);

        homeNavigation = findViewById(R.id.home_navi_layout);
        reviewNavigation = findViewById(R.id.review_navi_layout);

        homeNavigation.setOnClickListener(this);
        reviewNavigation.setOnClickListener(this);

        tvVersion = findViewById(R.id.tv_version);

        tvAppName = findViewById(R.id.tv_app_name);
        tvYear = findViewById(R.id.tv_year);
    }

    @Override
    protected void initData() {
        tvVersion.setText(getString(R.string.home_version, Utils.getCurrentVersion(this)));
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

    public void appBarVisibility(boolean navigationVisibility, boolean backVisibility, int navigationType) {
        if (navigationVisibility) imgNavigation.setVisibility(View.VISIBLE);
        else
            imgNavigation.setVisibility(View.INVISIBLE);

        if (backVisibility) imgBack.setVisibility(View.VISIBLE);
        else
            imgBack.setVisibility(View.INVISIBLE);

        // 0 home navigation
        // 1 review navigation
        if (navigationType == 0) {
            homeNavigation.setVisibility(View.VISIBLE);
            reviewNavigation.setVisibility(View.GONE);
        } else {
            homeNavigation.setVisibility(View.GONE);
            reviewNavigation.setVisibility(View.VISIBLE);
        }

    }

    public ApplicationResponse getApplicationResponse() {
        return applicationResponse;
    }

    public void setApplicationResponse(ApplicationResponse applicationResponse) {
        this.applicationResponse = applicationResponse;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    private void doLogout() {

        DialogUtils.showOkAndCancelDialog(this, getString(R.string.app_name), getString(R.string.logout), getString(R.string.Yes), getString(R.string.No), new AlertDialogOkAndCancel.AlertDialogListener() {
            @Override
            public void onSubmit() {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }
        });


    }

    private void doShareApp() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        String content = "https://play.google.com/store/apps/details?id=" + appPackageName;
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            i.putExtra(Intent.EXTRA_TEXT, content);
            startActivity(Intent.createChooser(i, getString(R.string.app_name)));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showLongToast(this, getString(R.string.error), true, false);
        }
    }

    public void updateAppInNavigation(ApplicationResponse applicationResponse) {
        tvAppName.setText(applicationResponse.getPayerName());
        tvYear.setText(applicationResponse.getFinancialYear());
    }

    private void openGeneralInfoActivity(String title, String url) {
        Intent intent = new Intent(this, GeneralInfoActivity.class);
        intent.putExtra(Constants.URL_EXTRA, url);
        intent.putExtra(Constants.TITLE_INFO_EXTRA, title);
        startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
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

            case R.id.layout_personal:
                if (expPersonalLayout.isExpanded()) {
                    expPersonalLayout.setExpanded(false);
                } else {
                    expPersonalLayout.setExpanded(true);
                }
                break;

            case R.id.layout_incomes:
                if (expIncomesLayout.isExpanded()) {
                    expIncomesLayout.setExpanded(false);
                } else {
                    expIncomesLayout.setExpanded(true);
                }
                break;

            case R.id.layout_deductions:
                if (expDeductionsLayout.isExpanded()) {
                    expDeductionsLayout.setExpanded(false);
                } else {
                    expDeductionsLayout.setExpanded(true);
                }
                break;

            case R.id.layout_family:
                if (expFamilyLayout.isExpanded()) {
                    expFamilyLayout.setExpanded(false);
                } else {
                    expFamilyLayout.setExpanded(true);
                }
                break;

            case R.id.tv_review_personal_name:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewPersonalInfomationA.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.tv_review_personal_bank:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewPersonalInfomationB.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.tv_review_personal_education:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewPersonalInfomationC.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.img_logout:
                doLogout();
                break;

            case R.id.layout_rate:
                Utils.openPlayStore(this);
                break;

            // do not remove,must have
            case R.id.home_navi_layout:

                break;

            // do not remove,must have
            case R.id.review_navi_layout:

                break;

            case R.id.tv_share:
                doShareApp();
                break;

            case R.id.tv_about_us:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openGeneralInfoActivity(getString(R.string.home_navi_about), "http://oztax.tonishdev.com/about-us");
                break;

            case R.id.tv_privacy:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openGeneralInfoActivity(getString(R.string.home_navi_privacy), "http://oztax.tonishdev.com/privacy-policy");
                break;

            case R.id.tv_terms:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openGeneralInfoActivity(getString(R.string.home_navi_terms), "http://oztax.tonishdev.com/terms-and-conditions");
                break;

            case R.id.tv_review_family_health_dependants:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewFamilyHealthDependantsFragment.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.tv_review_family_health_medicare:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewFamilyHealthMedicareFragment.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.tv_review_family_health_private:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewFamilyHealthPrivateFragment.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

            case R.id.tv_review_family_health_spouse:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewFamilyHealthDependantsFragment.class, true, new Bundle(), TransitionScreen.FADE_IN);
                break;

        }

    }
}
