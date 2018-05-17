package au.mccann.oztaxreturn.fragment.review.summary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.GeneralInfoActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.fragment.review.begin.ReviewBeginBFragment;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by CanTran on 4/23/18.
 */
public class ReviewSummaryFragment extends BaseFragment implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = ReviewSummaryFragment.class.getSimpleName();
    private TextViewCustom tvTaxReturn, tvActual, tvTotalIncome, tvTotalDeduction, tvTaxPayable, tvTaxWidthheld;
    private TextViewCustom tvIncomeSalary, tvGovernmentPayments, tvInterest, tvDividends, tvEarlyTermination, tvSuperIncomeStream, tvSuperLumpSum, tvRentaIncome;
    private TextViewCustom tvVehicles, tvWorkRelatedClothing, tvWorkRelatedEducation, tvOtherWorkRelatedExpenses, tvDonations, tvTaxAgentFees, tvBankFees;
    private TextViewCustom tvTaxOn, tvMedicareLevy, tvMedicareLevySurcharge, tvRepayment, tvTaxOffsets, tvTaxCredits;
    private ExpandableLayout layoutIncome, layoutTax, layoutDeduction;
    private ImageView icIncome, icDeduction, icTax;
    private int appID;
    private Animation anim_down, anim_up;
    private ButtonCustom btnNext;
    private TextViewCustom tvPolicy;
    File futureStudioIconFile;
    private LinearLayout layoutActual;
    private ImageView imgNoteEstimated, imgTacRefund;


    @Override
    protected int getLayout() {
        return R.layout.fragment_summary;
    }

    @Override
    protected void initView() {
        tvTaxReturn = (TextViewCustom) findViewById(R.id.tv_tax_return);
        tvActual = (TextViewCustom) findViewById(R.id.tv_tax_actual);
        tvTotalIncome = (TextViewCustom) findViewById(R.id.tv_total_income);
        tvTotalDeduction = (TextViewCustom) findViewById(R.id.tv_total_deduction);
        tvTaxPayable = (TextViewCustom) findViewById(R.id.tv_tax_payable);
        tvTaxWidthheld = (TextViewCustom) findViewById(R.id.tv_tax_widthheld);
        tvPolicy = (TextViewCustom) findViewById(R.id.tv_privacy_policy);

        icIncome = (ImageView) findViewById(R.id.ic_income);
        icDeduction = (ImageView) findViewById(R.id.ic_deduction);
        imgNoteEstimated = (ImageView) findViewById(R.id.img_estimated);
        imgTacRefund = (ImageView) findViewById(R.id.img_actual);
        icTax = (ImageView) findViewById(R.id.ic_tax);

        layoutIncome = (ExpandableLayout) findViewById(R.id.layout_income);
        layoutTax = (ExpandableLayout) findViewById(R.id.layout_tax);
        layoutDeduction = (ExpandableLayout) findViewById(R.id.layout_deduction);
        layoutActual = (LinearLayout) findViewById(R.id.layout_actual);

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
        imgNoteEstimated.setOnClickListener(this);
        imgTacRefund.setOnClickListener(this);
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
            layoutActual.setVisibility(View.VISIBLE);
            if (Double.parseDouble(summary.getActualTaxRefund()) > 0) {
                tvPolicy.setText(getContext().getString(R.string.positive_tax_refund));
            } else {
                String negativeTaxRefund = getContext().getString(R.string.negative_tax_refund) + " " + getContext().getString(R.string.dolla) + " " + summary.getActualTaxRefund() + " "+getContext().getString(R.string.negative_tax_refund_end);
                tvPolicy.setText(negativeTaxRefund);
            }
            setClickDownload(tvPolicy, summary);
        } else {
            layoutActual.setVisibility(View.GONE);
            if (isEditApp()) {
                tvPolicy.setText(getContext().getString(R.string.privacy_policy));
                setUnderLinePolicy(tvPolicy);
            } else tvPolicy.setText(getContext().getString(R.string.review_summary_note));
        }
        tvTaxReturn.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(summary.getEstimatedTaxRefund())));
        tvActual.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(summary.getActualTaxRefund())));
        tvTotalIncome.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(summary.getIncome().getTotal())));
        tvTotalDeduction.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(summary.getDeduction().getTotal())));
        tvTaxPayable.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(summary.getTaxLiability().getTotal())));
        tvTaxWidthheld.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(summary.getTaxWithheld())));
        updateIncome(summary.getIncome().getParts());
        updateDeduction(summary.getDeduction().getParts());
        updateTax(summary.getTaxLiability().getParts());
    }

    private void updateIncome(IncomePart part) {
        tvIncomeSalary.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getSalary())));
        tvGovernmentPayments.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getGovPayments())));
        tvInterest.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getBankInterests())));
        tvDividends.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getDividends())));
        tvEarlyTermination.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getEtps())));
        tvSuperIncomeStream.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getSuperIncomeStream())));
        tvSuperLumpSum.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getSuperLumpSum())));
        tvRentaIncome.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getRentals())));
    }

    private void updateDeduction(DeductionPart part) {
        tvVehicles.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getVehicles())));
        tvWorkRelatedClothing.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getClothes())));
        tvWorkRelatedEducation.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getEducations())));
        tvOtherWorkRelatedExpenses.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getOthers())));
        tvDonations.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getDonations())));
        tvTaxAgentFees.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getTaxAgents())));
        tvBankFees.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getBankFees())));
    }

    private void updateTax(TaxPart part) {
        tvTaxOn.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getOnTaxableIncome())));
        tvMedicareLevy.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getMedicareLevy())));
        tvMedicareLevySurcharge.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getMedicareLevySurcharge())));
        tvRepayment.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getHecsHelpRepayment())));
        tvTaxOffsets.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getTaxOffsets())));
        tvTaxCredits.setText(Utils.formatMoneyFloat(getActivity(), Float.parseFloat(part.getTaxCredits())));
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
                    LogUtils.d(TAG, "getReviewSummary body : " + response.body().toString());
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


    private void downloadFile(final String url) {
        ProgressDialogUtils.showProgressDialog(getContext());
        ApiClient.getApiService().downloadFileUrlAsync(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                ProgressDialogUtils.dismissProgressDialog();
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file" + response.code());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            final boolean writtenToDisk = writeResponseBodyToDisk(response.body(), url);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + System.currentTimeMillis() + String.valueOf(getString(R.string.tax_return_report));
                                    if (writtenToDisk)
                                        DialogUtils.showOkDialog(getContext(), "download was successful", path, getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                                            @Override
                                            public void onSubmit() {
                                            }
                                        });
                                }
                            });
                            return null;
                        }
                    }.execute();

                } else {
                    Log.d(TAG, "server contact failed");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "doSaveReview onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getContext(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        downloadFile(url);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

//    private void openFile() {
//        MimeTypeMap myMime = MimeTypeMap.getSingleton();
//        Intent newIntent = new Intent(Intent.ACTION_VIEW);
//        String mimeType = myMime.getMimeTypeFromExtension(fileExt(futureStudioIconFile.getAbsolutePath()).substring(1));
//        newIntent.setDataAndType(Uri.fromFile(futureStudioIconFile), mimeType);
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        try {
//            getActivity().startActivity(newIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
//        }
//    }

//    private String fileExt(String url) {
//        if (url.indexOf("?") > -1) {
//            url = url.substring(0, url.indexOf("?"));
//        }
//        if (url.lastIndexOf(".") == -1) {
//            return null;
//        } else {
//            String ext = url.substring(url.lastIndexOf(".") + 1);
//            if (ext.indexOf("%") > -1) {
//                ext = ext.substring(0, ext.indexOf("%"));
//            }
//            if (ext.indexOf("/") > -1) {
//                ext = ext.substring(0, ext.indexOf("/"));
//            }
//            return ext.toLowerCase();
//
//        }
//    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile("https://oztax-dev-static.s3-ap-southeast-1.amazonaws.com/N0SlKBZKaJ-1524469668.png");
                } else {
                    Snackbar.make(findViewById(R.id.relativeLayout), "Permission Denied, Please allow to proceed !", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }


    private void setClickDownload(TextViewCustom textViewCustom, final Summary summary) {
        String text = textViewCustom.getText().toString().trim();
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        ClickableSpan conditionClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    downloadFile(summary.getAttachmentUrl());
                } else {
                    requestPermission();
                }
            }
        };

        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.tax_return_report)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.tax_return_report)) + String.valueOf(getString(R.string.tax_return_report)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                conditionClickableSpan, // Span to add
                text.indexOf(getString(R.string.tax_return_report)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.tax_return_report)) + String.valueOf(getString(R.string.tax_return_report)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        textViewCustom.setText(ssBuilder);
        textViewCustom.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCustom.setHighlightColor(Color.TRANSPARENT);
    }

    private void setUnderLinePolicy(TextViewCustom textViewCustom) {
        String text = getString(R.string.privacy_policy);
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        ClickableSpan conditionClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(getString(R.string.app_term_conditions), "http://oztax.tonishdev.com/terms-and-conditions");
            }
        };
        ClickableSpan nadClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(getString(R.string.app_privacy_policy), "http://oztax.tonishdev.com/privacy-policy");
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


    private boolean writeResponseBodyToDisk(ResponseBody body, String url) {
        String[] pdf = url.split(".");
        try {
            // todo change the file location/name according to your needs
            futureStudioIconFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + System.currentTimeMillis() + String.valueOf(getString(R.string.tax_return_report) + pdf[pdf.length - 1]));
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
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
            case R.id.img_estimated:
                Utils.showToolTip(getActivity(), imgNoteEstimated, getString(R.string.review_summary_note));
                break;
            case R.id.img_actual:
                Utils.showToolTip(getActivity(), imgTacRefund, getString(R.string.review_summary_note));
                break;
        }

    }
}
