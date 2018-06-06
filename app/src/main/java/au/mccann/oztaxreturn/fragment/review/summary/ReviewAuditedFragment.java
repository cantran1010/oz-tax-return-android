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

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationA;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Summary;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.TextViewCustom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/23/18.
 */

public class ReviewAuditedFragment extends BaseFragment implements View.OnClickListener {
    private TextViewCustom tvDownload;
    private Summary summary;
    private static final String TAG = ReviewAuditedFragment.class.getSimpleName();

    @Override
    protected int getLayout() {
        return R.layout.review_after_being_audited;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_review).setOnClickListener(this);
        tvDownload = (TextViewCustom) findViewById(R.id.tv_download);

    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.Review_your_application));
        appBarVisibility(false, true, 1);
        getReviewSummary();
        setUnderLinePolicy(tvDownload);
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void getReviewSummary() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewSummary appID : " + getApplicationResponse().getId());
        ApiClient.getApiService().getReviewSummary(UserManager.getUserToken(), getApplicationResponse().getId()).enqueue(new Callback<Summary>() {
            @Override
            public void onResponse(Call<Summary> call, @NonNull Response<Summary> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewSummary code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewSummary body : " + response.body().toString());
                    if (response.body() != null) {
                        summary = response.body();
                    }
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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


    private void setUnderLinePolicy(TextViewCustom textViewCustom) {
        String text = tvDownload.getText().toString();
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);

        ClickableSpan nadClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (summary.getAttachmentUrl() != null || summary.getAttachmentUrl().isEmpty())
                    FileUtils.downloadFile(getActivity(), summary.getAttachmentUrl(), getString(R.string.ATO_letter));
            }
        };
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.review_audi_download)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.review_audi_download)) + String.valueOf(getString(R.string.review_audi_download)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                nadClickableSpan,
                text.indexOf(getString(R.string.review_audi_download)),
                text.indexOf(getString(R.string.review_audi_download)) + String.valueOf(getString(R.string.review_audi_download)).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        textViewCustom.setText(ssBuilder);
        textViewCustom.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCustom.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_review:
                openFragment(R.id.layout_container, ReviewPersonalInfomationA.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }
    }
}
