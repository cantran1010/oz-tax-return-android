package au.mccann.oztaxreturn.fragment.review.income;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.JobAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.IncomeResponse;
import au.mccann.oztaxreturn.model.Job;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CanTran on 4/23/18.
 */
public class ReviewIncomeWS extends BaseFragment implements View.OnClickListener {
    private static final String TAG = ReviewIncomeWS.class.getSimpleName();
    private JobAdapter jobAdapter;
    private ArrayList<Job> jobs = new ArrayList<>();
    private RecyclerView recyclerView;
    private int appID;
    private FloatingActionButton fab;

    @Override
    protected int getLayout() {
        return R.layout.fragment_review_income_ws;
    }

    @Override
    protected void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        ButtonCustom btnnext = (ButtonCustom) findViewById(R.id.btn_next);
        btnnext.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.rcv_job);

    }


    @Override
    protected void initData() {
        appID = getApplicationResponse().getId();
        fab.setEnabled(isEditApp());
        setTitle(getString(R.string.review_income_title));
        appBarVisibility(true, true, 1);
        getReviewIncome();
        updateList();
        updateProgress(4);
    }


    private void updateList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        jobAdapter = new JobAdapter(jobs, getActivity());
        recyclerView.setAdapter(jobAdapter);
    }

    private void getReviewIncome() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewIncome code : " + appID);
        ApiClient.getApiService().getReviewIncome(UserManager.getUserToken(), appID).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewIncome code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewIncome body : " + response.body().toString());
                    jobs.clear();
                    jobs.addAll(response.body().getJobs());
                    jobAdapter.notifyDataSetChanged();
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewIncome error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<IncomeResponse> call, Throwable t) {
                LogUtils.e(TAG, "getReviewIncome onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewIncome();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doSaveReview() {
        ProgressDialogUtils.showProgressDialog(getContext());
        JSONObject jsonRequest = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (Job job : jobs
                    ) {
                JSONObject mJs = new JSONObject();
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_ID, job.getId());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_GROSS, job.getTotalGrossIncom());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_TAX, job.getTotalTaxWidthheld());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_ALLOWANCES, job.getTotalTaxWidthheld());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_FRINGE, job.getReporTableFringerBenefits());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_EMPLOYER, job.getReporTableEmployerSupper());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_COMPANY_NAME, job.getCompanyName());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_COMPANY_ABN, job.getCompanyAbn());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_JOB_COMPANY_CONTACT, job.getCompanyContact());
                jsonArray.put(mJs);
            }
            jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_JOB, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveReview jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().putReviewIncom(UserManager.getUserToken(), appID, body).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doSaveReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doSaveReview code: " + response.body().getJobs().toString());
                    openFragment(R.id.layout_container, GovementPayment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doSaveReview error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getContext(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<IncomeResponse> call, Throwable t) {
                LogUtils.e(TAG, "doSaveReview onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getContext(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doSaveReview();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                for (Job job : jobs
                        ) {
                    job.setEdit(true);
                }
                jobAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_next:
                if (isEditApp())
                    doSaveReview();
                else
                    openFragment(R.id.layout_container, GovementPayment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }

    }
}
