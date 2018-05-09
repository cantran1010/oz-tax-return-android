package au.mccann.oztaxreturn.fragment.review.summary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.GeneralInfoActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.fragment.ReviewBeginBFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.DeductionPart;
import au.mccann.oztaxreturn.model.IncomePart;
import au.mccann.oztaxreturn.model.Summary;
import au.mccann.oztaxreturn.model.TaxPart;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.TextViewCustom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.utils.Utils.formatMoney;

/**
 * Created by CanTran on 4/23/18.
 */
public class ReviewSummary extends BaseFragment implements View.OnClickListener {
    private static final String TAG = ReviewSummary.class.getSimpleName();
    private TextViewCustom tvTaxReturn, tvTotalIncome, tvTotalDeduction, tvTaxPayable, tvTaxWidthheld;
    private TextViewCustom tvIncomeSalary, tvGovernmentPayments, tvInterest, tvDividends, tvEarlyTermination, tvSuperIncomeStream, tvSuperLumpSum, tvRentaIncome;
    private TextViewCustom tvVehicles, tvWorkRelatedClothing, tvWorkRelatedEducation, tvOtherWorkRelatedExpenses, tvDonations, tvTaxAgentFees, tvBankFees;
    private TextViewCustom tvTaxOn, tvMedicareLevy, tvMedicareLevySurcharge, tvRepayment, tvTaxOffsets, tvTaxCredits;
    private ExpandableLayout layoutIncome, layoutTax, layoutDeduction;
    private ImageView icIncome, icDeduction, icTax;
    private int appID;
    private Animation anim_down, anim_up;
    private ButtonCustom btnNext;
    private TextViewCustom tvPolicy;

    @Override
    protected int getLayout() {
        return R.layout.fragment_summary;
    }

    @Override
    protected void initView() {
        tvTaxReturn = (TextViewCustom) findViewById(R.id.tv_tax_return);
        tvTotalIncome = (TextViewCustom) findViewById(R.id.tv_total_income);
        tvTotalDeduction = (TextViewCustom) findViewById(R.id.tv_total_deduction);
        tvTaxPayable = (TextViewCustom) findViewById(R.id.tv_tax_payable);
        tvTaxWidthheld = (TextViewCustom) findViewById(R.id.tv_tax_widthheld);
        tvPolicy = (TextViewCustom) findViewById(R.id.tv_privacy_policy);

        icIncome = (ImageView) findViewById(R.id.ic_income);
        icDeduction = (ImageView) findViewById(R.id.ic_deduction);
        icTax = (ImageView) findViewById(R.id.ic_tax);

        layoutIncome = (ExpandableLayout) findViewById(R.id.layout_income);
        layoutTax = (ExpandableLayout) findViewById(R.id.layout_tax);
        layoutDeduction = (ExpandableLayout) findViewById(R.id.layout_deduction);

        tvIncomeSalary = (TextViewCustom) findViewById(R.id.tv_total_income_salary);
        tvGovernmentPayments = (TextViewCustom) findViewById(R.id.tv_government_payments);
        tvInterest = (TextViewCustom) findViewById(R.id.tv_interest);
        tvDividends = (TextViewCustom) findViewById(R.id.tv_dividends);
        tvEarlyTermination = (TextViewCustom) findViewById(R.id.tv_early_termination);
        tvSuperIncomeStream = (TextViewCustom) findViewById(R.id.tv_super_income_stream);
        tvSuperLumpSum = (TextViewCustom) findViewById(R.id.tv_super_lump_sum);
        tvRentaIncome = (TextViewCustom) findViewById(R.id.tv_renta_income);

        tvVehicles = (TextViewCustom) findViewById(R.id.tv_vehicles);
        tvWorkRelatedClothing = (TextViewCustom) findViewById(R.id.tv_work_related_clothing);
        tvWorkRelatedEducation = (TextViewCustom) findViewById(R.id.tv_work_related_education);
        tvOtherWorkRelatedExpenses = (TextViewCustom) findViewById(R.id.tv_other_work_related_expenses);
        tvDonations = (TextViewCustom) findViewById(R.id.tv_donations);
        tvTaxAgentFees = (TextViewCustom) findViewById(R.id.tv_tax_agent_fees);
        tvBankFees = (TextViewCustom) findViewById(R.id.tv_bank_fees);

        tvTaxOn = (TextViewCustom) findViewById(R.id.tv_tax_on);
        tvMedicareLevy = (TextViewCustom) findViewById(R.id.tv_medicare_levy);
        tvMedicareLevySurcharge = (TextViewCustom) findViewById(R.id.tv_medicare_levy_surcharge);
        tvRepayment = (TextViewCustom) findViewById(R.id.tv_repayment);
        tvTaxOffsets = (TextViewCustom) findViewById(R.id.tv_tax_offsets);
        tvTaxCredits = (TextViewCustom) findViewById(R.id.tv_tax_credits);

        btnNext = (ButtonCustom) findViewById(R.id.btn_review);
        btnNext.setOnClickListener(this);
        findViewById(R.id.lo_total_income).setOnClickListener(this);
        findViewById(R.id.lo_total_deduction).setOnClickListener(this);
        findViewById(R.id.lo_total_tax).setOnClickListener(this);
        anim_down = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate_down);
        anim_up = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate_up);

    }


    @Override
    protected void initData() {
        appID = getApplicationResponse().getId();
        if (isEditApp()) btnNext.setText(getContext().getString(R.string.lodge));
        else btnNext.setText(getContext().getString(R.string.review));
        setTitle(getString(R.string.review_summary_title));
        appBarVisibility(true, true, 1);
        getReviewSummary();
    }

    private void updateUI(Summary summary) {
        if (summary.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETED)) {
            if (Integer.parseInt(summary.getActualTaxRefund()) > 0) {
                tvPolicy.setText(getContext().getString(R.string.positive_tax_refund));
            } else {
                String negativeTaxRefund = getContext().getString(R.string.negative_tax_refund) + getContext().getString(R.string.dolla) + summary.getActualTaxRefund() + getContext().getString(R.string.negative_tax_refund_end);
                tvPolicy.setText(negativeTaxRefund);
            }
        } else {
            if (isEditApp()) {
                tvPolicy.setText(getContext().getString(R.string.privacy_policy));
                setUnderLinePolicy(tvPolicy);
            } else tvPolicy.setText(getContext().getString(R.string.review_summary_note));
        }
        tvTaxReturn.setText(formatMoney(getContext(), Integer.parseInt(summary.getEstimatedTaxRefund())));
        tvTotalIncome.setText(formatMoney(getContext(), Integer.parseInt(summary.getIncome().getTotal())));
        tvTotalDeduction.setText(formatMoney(getContext(), Integer.parseInt(summary.getDeduction().getTotal())));
        tvTaxPayable.setText(formatMoney(getContext(), Integer.parseInt(summary.getTaxLiability().getTotal())));
        tvTaxWidthheld.setText(formatMoney(getContext(), Integer.parseInt(summary.getTaxWithheld())));
        updateIncome(summary.getIncome().getParts());
        updateDeduction(summary.getDeduction().getParts());
        updateTax(summary.getTaxLiability().getParts());
    }

    private void updateIncome(IncomePart part) {
        tvIncomeSalary.setText(formatMoney(getContext(), Integer.parseInt(part.getSalary())));
        tvGovernmentPayments.setText(formatMoney(getContext(), Integer.parseInt(part.getGovPayments())));
        tvInterest.setText(formatMoney(getContext(), Integer.parseInt(part.getBankInterests())));
        tvDividends.setText(formatMoney(getContext(), Integer.parseInt(part.getDividends())));
        tvEarlyTermination.setText(formatMoney(getContext(), Integer.parseInt(part.getEtps())));
        tvSuperIncomeStream.setText(formatMoney(getContext(), Integer.parseInt(part.getSuperIncomeStream())));
        tvSuperLumpSum.setText(formatMoney(getContext(), Integer.parseInt(part.getSuperLumpSum())));
        tvRentaIncome.setText(formatMoney(getContext(), Integer.parseInt(part.getRentals())));
    }

    private void updateDeduction(DeductionPart part) {
        tvVehicles.setText(formatMoney(getContext(), Integer.parseInt(part.getVehicles())));
        tvWorkRelatedClothing.setText(formatMoney(getContext(), Integer.parseInt(part.getClothes())));
        tvWorkRelatedEducation.setText(formatMoney(getContext(), Integer.parseInt(part.getEducations())));
        tvOtherWorkRelatedExpenses.setText(formatMoney(getContext(), Integer.parseInt(part.getOthers())));
        tvDonations.setText(formatMoney(getContext(), Integer.parseInt(part.getDonations())));
        tvTaxAgentFees.setText(formatMoney(getContext(), Integer.parseInt(part.getTaxAgents())));
        tvBankFees.setText(formatMoney(getContext(), Integer.parseInt(part.getBankFees())));

    }

    private void updateTax(TaxPart part) {
        tvTaxOn.setText(formatMoney(getContext(), Integer.parseInt(part.getOnTaxableIncome())));
        tvMedicareLevy.setText(formatMoney(getContext(), Integer.parseInt(part.getMedicareLevy())));
        tvMedicareLevySurcharge.setText(formatMoney(getContext(), Integer.parseInt(part.getMedicareLevySurcharge())));
        tvRepayment.setText(formatMoney(getContext(), Integer.parseInt(part.getHecsHelpRepayment())));
        tvTaxOffsets.setText(formatMoney(getContext(), Integer.parseInt(part.getTaxOffsets())));
        tvTaxCredits.setText(formatMoney(getContext(), Integer.parseInt(part.getTaxCredits())));
    }

    private void expandableLayout(ExpandableLayout expan, ImageView img) {
        closeExpandale(expan);
        if (expan.isExpanded()) {
            img.startAnimation(anim_down);
        } else {
            img.startAnimation(anim_up);
        }
        expan.toggle();
    }

    private void closeExpandale(ExpandableLayout expan) {
        if (layoutIncome.isExpanded() && expan != layoutIncome) {
            layoutIncome.toggle();
            icIncome.startAnimation(anim_down);
        } else if (layoutDeduction.isExpanded() && expan != layoutDeduction) {
            layoutDeduction.toggle();
            icDeduction.startAnimation(anim_down);
        } else if (layoutTax.isExpanded() && expan != layoutTax) {
            layoutTax.toggle();
            icTax.startAnimation(anim_down);
        }
    }

    private void getReviewSummary() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewSummary appID : " + appID);
        ApiClient.getApiService().getReviewSummary(UserManager.getUserToken(), appID).enqueue(new Callback<Summary>() {
            @Override
            public void onResponse(Call<Summary> call, @NonNull Response<Summary> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewSummary code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
//                    LogUtils.d(TAG, "getReviewSummary body : " + response.body().toString());
                    if (response.body() != null) {
                        updateUI(response.body());
                    }
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewSummary error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Summary> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "getReviewSummary onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewSummary();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void lodgeApplication() {
        ProgressDialogUtils.showProgressDialog(getContext());
        ApiClient.getApiService().loggeApplicaction(UserManager.getUserToken(), appID).enqueue(new Callback<ApplicationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApplicationResponse> call, @NonNull Response<ApplicationResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doSaveReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doSaveReview code: " + response.body().toString());
                    Utils.showLongToast(getContext(), getContext().getString(R.string.lodged_successfully), false, true);
                    openFragment(R.id.layout_container, HomeFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.e(TAG, "doSaveReview error : " + error.message());
                        DialogUtils.showOkDialog(getContext(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ApplicationResponse> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "doSaveReview onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getContext(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        lodgeApplication();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }


    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void setUnderLinePolicy(TextViewCustom textViewCustom) {
        String text = getString(R.string.privacy_policy);
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        ClickableSpan conditionClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(getString(R.string.app_term_conditions), "http://hozo.vn/dieu-khoan-su-dung/?ref=app");
            }
        };
        ClickableSpan nadClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(getString(R.string.app_privacy_policy), "http://hozo.vn/chinh-sach-bao-mat/?ref=app");
            }
        };
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#e55a1d")), // Span to add
                0, // Start of the span (inclusive)
                1, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.app_privacy_policy)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.app_privacy_policy)) + String.valueOf(getString(R.string.app_privacy_policy)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                nadClickableSpan,
                text.indexOf(getString(R.string.app_privacy_policy)),
                text.indexOf(getString(R.string.app_privacy_policy)) + String.valueOf(getString(R.string.app_privacy_policy)).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.app_term_conditions)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.app_term_conditions)) + String.valueOf(getString(R.string.app_term_conditions)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                conditionClickableSpan, // Span to add
                text.indexOf(getString(R.string.app_term_conditions)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.app_term_conditions)) + String.valueOf(getString(R.string.app_term_conditions)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        textViewCustom.setText(ssBuilder);
        textViewCustom.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCustom.setHighlightColor(Color.TRANSPARENT);
    }

    private void openGeneralInfoActivity(String title, String url) {
        Intent intent = new Intent(getContext(), GeneralInfoActivity.class);
        intent.putExtra(Constants.URL_EXTRA, url);
        intent.putExtra(Constants.TITLE_INFO_EXTRA, title);
        startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_review:
                if (isEditApp())
                    lodgeApplication();
                else {
                    openFragment(R.id.layout_container, ReviewBeginBFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                }
                break;
            case R.id.lo_total_income:
                expandableLayout(layoutIncome, icIncome);
                break;
            case R.id.lo_total_deduction:
                expandableLayout(layoutDeduction, icDeduction);
                break;
            case R.id.lo_total_tax:
                expandableLayout(layoutTax, icTax);
                break;
        }

    }
}
