package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserEntity;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.ContactFragment;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.fragment.NotificationFragment;
import au.mccann.oztaxreturn.fragment.basic.DeductionFragment;
import au.mccann.oztaxreturn.fragment.basic.FirstCheckoutFragment;
import au.mccann.oztaxreturn.fragment.basic.OtherFragment;
import au.mccann.oztaxreturn.fragment.basic.PersonalFragment;
import au.mccann.oztaxreturn.fragment.basic.SubmitFragment;
import au.mccann.oztaxreturn.fragment.basic.WagesSalaryFragment;
import au.mccann.oztaxreturn.fragment.review.deduction.ReviewClothesFragment;
import au.mccann.oztaxreturn.fragment.review.deduction.ReviewDonationsFragment;
import au.mccann.oztaxreturn.fragment.review.deduction.ReviewEducationsFragment;
import au.mccann.oztaxreturn.fragment.review.deduction.ReviewOthersFragment;
import au.mccann.oztaxreturn.fragment.review.deduction.ReviewTaxAgentFragment;
import au.mccann.oztaxreturn.fragment.review.deduction.ReviewVehicleFragment;
import au.mccann.oztaxreturn.fragment.review.family.ReviewFamilyHealthDependantsFragment;
import au.mccann.oztaxreturn.fragment.review.family.ReviewFamilyHealthMedicareFragment;
import au.mccann.oztaxreturn.fragment.review.family.ReviewFamilyHealthPrivateFragment;
import au.mccann.oztaxreturn.fragment.review.family.ReviewFamilyHealthSpouseFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewAnnuitiesFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewDividendsFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewETPsFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewGovementFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewInterestsFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewLumpSumFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewRentalFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewWagesSalaryFragment;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationA;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationB;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationC;
import au.mccann.oztaxreturn.fragment.review.summary.ReviewSummaryFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.BaseAppProgress;
import au.mccann.oztaxreturn.model.Notification;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.rest.response.ReviewProgressResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.TextViewCustom;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private ImageView imgHome, imgContact, imgNotification, imgBack, imgNavigation;
    private TextViewCustom tvHome, tvContact, tvNotification, tvTitle, tvVersion, tvName;
    private ExpandableLayout expPersonalLayout, expIncomesLayout, expDeductionsLayout, expFamilyLayout;
    private RelativeLayout homeNavigation, reviewNavigation, baseNavigation;
    private ApplicationResponse applicationResponse;
    private TextViewCustom tvAppName, tvYear, tvProgress, tvBaseProgress, tvBaseAppName, tvBaseYear;
    private ProgressBar progressBar, baseProgressBar;
    private boolean editApp;
    private CircleImageView imgAvatar;
    boolean doubleBackToExitPressedOnce = false;

    private TextViewCustom tvReviewPersonalName, tvReviewPersonalBank, tvReviewPersonalEducation;
    private TextViewCustom tvIncomeWS, tvIncomeGovernment, tvIncomeInterests, tvIncomeDevidents, tvIncomeEarly, tvIncomeAnnuities, tvIncomeLumpSum, tvIncomeRental;
    private TextViewCustom tvDeductionVehicles, tvDeductionClothing, tvDeductionEducation, tvDeductionOther, tvDeductionDonation, tvDeductionTaxAgents;
    private TextViewCustom tvHealthDependants, tvHealthMedicare, tvHealthPrivate, tvHealthSpouse;
    private TextViewCustom tvBaseIncome, tvBaseOthe, tvBaseDeduction, tvBaseBank, tvBaseCheckout, tvBasePrersonal;


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

        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.ProgressBar);

        tvBaseProgress = findViewById(R.id.tv_base_progress);
        baseProgressBar = findViewById(R.id.base_ProgressBar);

        tvHome = findViewById(R.id.tv_home_menu1);
        tvContact = findViewById(R.id.tv_home_menu2);
        tvNotification = findViewById(R.id.tv_home_menu3);
        tvName = findViewById(R.id.tv_user_name);

        imgNavigation = findViewById(R.id.img_navigation);
        imgAvatar = findViewById(R.id.img_user_avatar);

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
        findViewById(R.id.layout_sumary).setOnClickListener(this);

        findViewById(R.id.tv_review_family_health_dependants).setOnClickListener(this);
        findViewById(R.id.tv_review_family_health_medicare).setOnClickListener(this);
        findViewById(R.id.tv_review_family_health_private).setOnClickListener(this);
        findViewById(R.id.tv_review_family_health_spouse).setOnClickListener(this);

        tvHealthDependants = findViewById(R.id.tv_review_family_health_dependants);
        tvHealthMedicare = findViewById(R.id.tv_review_family_health_medicare);
        tvHealthPrivate = findViewById(R.id.tv_review_family_health_private);
        tvHealthSpouse = findViewById(R.id.tv_review_family_health_spouse);

        findViewById(R.id.tv_review_personal_name).setOnClickListener(this);
        findViewById(R.id.tv_review_personal_bank).setOnClickListener(this);
        findViewById(R.id.tv_review_personal_education).setOnClickListener(this);
        tvReviewPersonalName = findViewById(R.id.tv_review_personal_name);
        tvReviewPersonalBank = findViewById(R.id.tv_review_personal_bank);
        tvReviewPersonalEducation = findViewById(R.id.tv_review_personal_education);

        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_privacy).setOnClickListener(this);
        findViewById(R.id.tv_terms).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        findViewById(R.id.layout_rate).setOnClickListener(this);

        findViewById(R.id.tv_wages_salary).setOnClickListener(this);
        findViewById(R.id.tv_income_government_payment).setOnClickListener(this);
        findViewById(R.id.tv_income_interests).setOnClickListener(this);
        findViewById(R.id.tv_income_dividends).setOnClickListener(this);
        findViewById(R.id.tv_incomes_early).setOnClickListener(this);
        findViewById(R.id.tv_income_annuities_suppers).setOnClickListener(this);
        findViewById(R.id.tv_income_lump_sum).setOnClickListener(this);
        findViewById(R.id.tv_income_rental).setOnClickListener(this);

        tvIncomeWS = findViewById(R.id.tv_wages_salary);
        tvIncomeGovernment = findViewById(R.id.tv_income_government_payment);
        tvIncomeInterests = findViewById(R.id.tv_income_interests);
        tvIncomeDevidents = findViewById(R.id.tv_income_dividends);
        tvIncomeEarly = findViewById(R.id.tv_incomes_early);
        tvIncomeAnnuities = findViewById(R.id.tv_income_annuities_suppers);
        tvIncomeLumpSum = findViewById(R.id.tv_income_lump_sum);
        tvIncomeRental = findViewById(R.id.tv_income_rental);

        findViewById(R.id.tv_deduction_vehicles).setOnClickListener(this);
        findViewById(R.id.tv_deduction_clothing).setOnClickListener(this);
        findViewById(R.id.tv_deduction_education).setOnClickListener(this);
        findViewById(R.id.tv_deduction_other).setOnClickListener(this);
        findViewById(R.id.tv_deduction_donation).setOnClickListener(this);
        findViewById(R.id.tv_deduction_tax_agents).setOnClickListener(this);

        tvDeductionVehicles = findViewById(R.id.tv_deduction_vehicles);
        tvDeductionClothing = findViewById(R.id.tv_deduction_clothing);
        tvDeductionEducation = findViewById(R.id.tv_deduction_education);
        tvDeductionOther = findViewById(R.id.tv_deduction_other);
        tvDeductionDonation = findViewById(R.id.tv_deduction_donation);
        tvDeductionTaxAgents = findViewById(R.id.tv_deduction_tax_agents);

        tvBaseIncome = findViewById(R.id.tv_base_navi_income);
        tvBaseOthe = findViewById(R.id.tv_base_navi_other);
        tvBaseDeduction = findViewById(R.id.tv_base_navi_deductions);
        tvBaseBank = findViewById(R.id.tv_base_navi_bank);
        tvBaseCheckout = findViewById(R.id.tv_base_navi_checkout);
        tvBasePrersonal = findViewById(R.id.tv_base_navi_personal);
        tvBaseIncome.setOnClickListener(this);
        tvBaseOthe.setOnClickListener(this);
        tvBaseDeduction.setOnClickListener(this);
        tvBaseBank.setOnClickListener(this);
        tvBaseCheckout.setOnClickListener(this);
        tvBasePrersonal.setOnClickListener(this);


        findViewById(R.id.img_logout).setOnClickListener(this);

        tvName.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);

        homeNavigation = findViewById(R.id.home_navi_layout);
        reviewNavigation = findViewById(R.id.review_navi_layout);
        baseNavigation = findViewById(R.id.base_navi_layout);

        homeNavigation.setOnClickListener(this);
        reviewNavigation.setOnClickListener(this);
        baseNavigation.setOnClickListener(this);

        tvVersion = findViewById(R.id.tv_version);

        tvAppName = findViewById(R.id.tv_app_name);
        tvYear = findViewById(R.id.tv_year);

        tvBaseAppName = findViewById(R.id.tv_base_app_name);
        tvBaseYear = findViewById(R.id.tv_base_year);
    }

    @Override
    protected void initData() {

        if (getIntent().hasExtra(Constants.NOTIFICATION_EXTRA)) {
            Notification notification = (Notification) getIntent().getSerializableExtra(Constants.NOTIFICATION_EXTRA);

            if (notification.getEvent().equals("message_received")) {
                updateMenu(2);
                openFragment(R.id.layout_container, ContactFragment.class, false, new Bundle(), TransitionScreen.NON);
            } else {
                updateMenu(3);
                openFragment(R.id.layout_container, NotificationFragment.class, false, new Bundle(), TransitionScreen.NON);
            }

        } else {
            openFragment(R.id.layout_container, HomeFragment.class, false, new Bundle(), TransitionScreen.NON);
        }

        tvVersion.setText(getString(R.string.home_version, Utils.getCurrentVersion(this)));
        findViewById(R.id.img_navigation).setOnClickListener(this);
        findViewById(R.id.layout_home).setOnClickListener(this);
        findViewById(R.id.layout_contact).setOnClickListener(this);
        findViewById(R.id.layout_notification).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    @Override
    protected void resumeData() {
        UserEntity userEntity = au.mccann.oztaxreturn.database.UserManager.getUserEntity();
        if (userEntity.getAvatar() != null)
            Utils.displayImage(HomeActivity.this, imgAvatar, userEntity.getAvatar().getUrl());
        tvName.setText(userEntity.getUserName());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(Constants.NOTIFICATION_EXTRA)) {
            Notification notification = (Notification) intent.getSerializableExtra(Constants.NOTIFICATION_EXTRA);

            if (notification.getEvent().equals("message_received")) {
                updateMenu(2);
                openFragment(R.id.layout_container, ContactFragment.class, false, new Bundle(), TransitionScreen.NON);
            } else {
                updateMenu(3);
                openFragment(R.id.layout_container, NotificationFragment.class, false, new Bundle(), TransitionScreen.NON);
            }
        }
    }

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
            baseNavigation.setVisibility(View.GONE);
        } else if (navigationType == 1) {
            homeNavigation.setVisibility(View.GONE);
            reviewNavigation.setVisibility(View.VISIBLE);
            baseNavigation.setVisibility(View.GONE);
        } else {
            homeNavigation.setVisibility(View.GONE);
            reviewNavigation.setVisibility(View.GONE);
            baseNavigation.setVisibility(View.VISIBLE);
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

    public boolean isEditApp() {
        return editApp;
    }

    public void setEditApp(boolean editApp) {
        this.editApp = editApp;
    }

    private void doLogout() {

        DialogUtils.showOkAndCancelDialog(this, getString(R.string.app_name), getString(R.string.logout), getString(R.string.Yes), getString(R.string.No), new AlertDialogOkAndCancel.AlertDialogListener() {
            @Override
            public void onSubmit() {
                logOut();
            }

            @Override
            public void onCancel() {

            }
        });

    }

    private void logOut() {
        LogUtils.d(TAG, "logOut , Token : " + UserManager.getUserToken());
        ApiClient.getApiService().logOut(UserManager.getUserToken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LogUtils.d(TAG, "logOut , code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_NO_CONTENT) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "logOut error : " + error.message());
                        DialogUtils.showOkDialog(HomeActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogUtils.showRetryDialog(HomeActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        logOut();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
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
        tvBaseAppName.setText(applicationResponse.getPayerName());
        tvBaseYear.setText(applicationResponse.getFinancialYear());
    }

    private void openGeneralInfoActivity(String title, String url) {
        Intent intent = new Intent(this, GeneralInfoActivity.class);
        intent.putExtra(Constants.URL_EXTRA, url);
        intent.putExtra(Constants.TITLE_INFO_EXTRA, title);
        startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
    }

    public void getReviewProgress(final ApplicationResponse applicationResponse) {
        LogUtils.d(TAG, "getReviewProgress applicationResponse : " + applicationResponse.toString());
        ApiClient.getApiService().getReviewProgress(UserManager.getUserToken(), applicationResponse.getId()).enqueue(new Callback<ReviewProgressResponse>() {
            @Override
            public void onResponse(Call<ReviewProgressResponse> call, @NonNull Response<ReviewProgressResponse> response) {
                LogUtils.d(TAG, "getReviewProgress code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewProgress body : " + response.body().toString());
                    if (response.body() != null) {
                        ReviewProgressResponse reviewProgressResponse = response.body();

                        // personal infomation
                        if (reviewProgressResponse.isInfoNameResidency())
                            tvReviewPersonalName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvReviewPersonalName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isInfoBankAddress())
                            tvReviewPersonalBank.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvReviewPersonalBank.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isInfoEducationDebt())
                            tvReviewPersonalEducation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvReviewPersonalEducation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        //income
                        if (reviewProgressResponse.isIncomeWagesSalary())
                            tvIncomeWS.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeWS.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isIncomeGovPayments())
                            tvIncomeGovernment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeGovernment.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isIncomeBankInterests())
                            tvIncomeInterests.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeInterests.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isIncomeDividends())
                            tvIncomeDevidents.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeDevidents.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isIncomeEtps())
                            tvIncomeEarly.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeEarly.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isIncomeAnnuities())
                            tvIncomeAnnuities.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeAnnuities.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isIncomeLumpSums())
                            tvIncomeLumpSum.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeLumpSum.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isIncomeRentals())
                            tvIncomeRental.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvIncomeRental.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        // deduction
                        if (reviewProgressResponse.isDeductionVehicles())
                            tvDeductionVehicles.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvDeductionVehicles.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isDeductionClothes())
                            tvDeductionClothing.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvDeductionClothing.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isDeductionEducations())
                            tvDeductionEducation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvDeductionEducation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isDeductionOthers())
                            tvDeductionOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvDeductionOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isDeductionDonations())
                            tvDeductionDonation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvDeductionDonation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isDeductionTaxAgents())
                            tvDeductionTaxAgents.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvDeductionTaxAgents.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        // tvHealthDependants,tvHealthMedicare,tvHealthPrivate,tvHealthSpouse;
                        // Family and Health
                        if (reviewProgressResponse.isHealthDependants())
                            tvHealthDependants.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvHealthDependants.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isHealthMedicares())
                            tvHealthMedicare.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvHealthMedicare.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isHealthPrivates())
                            tvHealthPrivate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvHealthPrivate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (reviewProgressResponse.isHealthSpouses())
                            tvHealthSpouse.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvHealthSpouse.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        tvProgress.setText(reviewProgressResponse.getPercent() + "%");
                        progressBar.setProgress(reviewProgressResponse.getPercent());
                    }
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewProgress error : " + error.message());
                        DialogUtils.showOkDialog(HomeActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ReviewProgressResponse> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "getReviewProgress onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(HomeActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewProgress(applicationResponse);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    public void getBaseProgress(final ApplicationResponse applicationResponse) {
        LogUtils.d(TAG, "getReviewProgress applicationResponse : " + applicationResponse.toString());
        ApiClient.getApiService().getBaseProgress(UserManager.getUserToken(), applicationResponse.getId()).enqueue(new Callback<BaseAppProgress>() {
            @Override
            public void onResponse(Call<BaseAppProgress> call, @NonNull Response<BaseAppProgress> response) {
                LogUtils.d(TAG, "getReviewProgress code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewProgress body : " + response.body().toString());
                    if (response.body() != null) {
                        BaseAppProgress baseAppProgress = response.body();

                        // personal infomation
                        if (baseAppProgress.isIncome())
                            tvBaseIncome.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvBaseIncome.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (baseAppProgress.isOther())
                            tvBaseOthe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvBaseOthe.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (baseAppProgress.isDeduction())
                            tvBaseDeduction.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvBaseDeduction.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        //income
                        if (baseAppProgress.isBank())
                            tvBaseBank.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvBaseBank.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (baseAppProgress.isPersonal())
                            tvBasePrersonal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvBasePrersonal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        if (baseAppProgress.isCheckout())
                            tvBaseCheckout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_check_review, 0);
                        else
                            tvBaseCheckout.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        tvBaseProgress.setText(baseAppProgress.getPercent() + "%");
                        baseProgressBar.setProgress(baseAppProgress.getPercent());
                    }
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewProgress error : " + error.message());
                        DialogUtils.showOkDialog(HomeActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<BaseAppProgress> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "getReviewProgress onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(HomeActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewProgress(applicationResponse);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }


    private void openExpandale(ExpandableLayout expan) {
        if (expPersonalLayout.isExpanded() && expan != expPersonalLayout) {
            expPersonalLayout.toggle();
        } else if (expIncomesLayout.isExpanded() && expan != expIncomesLayout) {
            expIncomesLayout.toggle();
        } else if (expDeductionsLayout.isExpanded() && expan != expDeductionsLayout) {
            expDeductionsLayout.toggle();
        } else if (expFamilyLayout.isExpanded() && expan != expFamilyLayout) {
            expFamilyLayout.toggle();
        }
        expan.toggle();
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
                openExpandale(expPersonalLayout);
                break;

            case R.id.layout_incomes:
                openExpandale(expIncomesLayout);
                break;

            case R.id.layout_deductions:
                openExpandale(expDeductionsLayout);
                break;

            case R.id.layout_family:
                openExpandale(expFamilyLayout);
                break;
            case R.id.layout_sumary:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewSummaryFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_review_personal_name:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewPersonalInfomationA.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_review_personal_bank:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewPersonalInfomationB.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_review_personal_education:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewPersonalInfomationC.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
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
            case R.id.img_user_avatar:
            case R.id.tv_user_name:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                startActivity(new Intent(this, ManageAccountActivity.class), TransitionScreen.RIGHT_TO_LEFT);
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
                openFragment(R.id.layout_container, ReviewFamilyHealthDependantsFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_review_family_health_medicare:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewFamilyHealthMedicareFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_review_family_health_private:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewFamilyHealthPrivateFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_review_family_health_spouse:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewFamilyHealthSpouseFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_wages_salary:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewWagesSalaryFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_income_government_payment:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewGovementFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_income_interests:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewInterestsFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_income_dividends:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewDividendsFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_incomes_early:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewETPsFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_income_annuities_suppers:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewAnnuitiesFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_income_lump_sum:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewLumpSumFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_income_rental:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewRentalFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;

            case R.id.tv_deduction_vehicles:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewVehicleFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_deduction_clothing:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewClothesFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_deduction_education:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewEducationsFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_deduction_other:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewOthersFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_deduction_donation:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewDonationsFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_deduction_tax_agents:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, ReviewTaxAgentFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_base_navi_income:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, WagesSalaryFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_base_navi_other:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, OtherFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_base_navi_deductions:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, DeductionFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_base_navi_personal:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, PersonalFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_base_navi_bank:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, SubmitFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
            case R.id.tv_base_navi_checkout:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                openFragment(R.id.layout_container, FirstCheckoutFragment.class, true, new Bundle(), TransitionScreen.LEFT_TO_RIGHT);
                break;
        }

    }
}
