package au.mccann.oztaxreturn.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AddNewBoardActivity;
import au.mccann.oztaxreturn.adapter.HomeAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.basic.IncomeWagesSalaryFragment;
import au.mccann.oztaxreturn.fragment.review.summary.ReviewAfterBeingAudited;
import au.mccann.oztaxreturn.fragment.review.summary.ReviewSummary;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/17/18.
 */

public class HomeFragment extends BaseFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private ArrayList<ApplicationResponse> applicationResponses;

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_app);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.home_title));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewBoardActivity.class);
                intent.putParcelableArrayListExtra(Constants.APP_LIST_EXTRA, applicationResponses);
                startActivityForResult(intent, Constants.CREATE_APP_REQUEST_CODE, TransitionScreen.RIGHT_TO_LEFT);
            }
        });
    }

    @Override
    protected void resumeData() {
        getAllApplication();
        appBarVisibility(true, false, 0);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CREATE_APP_REQUEST_CODE && resultCode == Constants.CREATE_APP_RESULT_CODE) {
            getAllApplication();
        }
    }

    private void updateList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        homeAdapter = new HomeAdapter(applicationResponses, getActivity());
        recyclerView.setAdapter(homeAdapter);
        homeAdapter.setOnClickListener(new HomeAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                ApplicationResponse applicationResponse = applicationResponses.get(position);
                switch (applicationResponse.getStatus()) {
                    case "init":
                        setEditApp(true);
                        setApplicationResponse(applicationResponses.get(position));
                        updateAppInNavigation(applicationResponses.get(position));
                        openFragment(R.id.layout_container, IncomeWagesSalaryFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                        break;
                    case "submitted":
                        setApplicationResponse(applicationResponses.get(position));
                        updateAppInNavigation(applicationResponses.get(position));
                        setEditApp(true);
                        openFragment(R.id.layout_container, ReviewBeginAFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                        break;
                    case "reviewed":
                        setEditApp(true);
                        setApplicationResponse(applicationResponses.get(position));
                        updateAppInNavigation(applicationResponses.get(position));
                        openFragment(R.id.layout_container, ReviewBeginBFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                        break;
                    case "lodged":
                        setEditApp(false);
                        setApplicationResponse(applicationResponses.get(position));
                        updateAppInNavigation(applicationResponses.get(position));
                        openFragment(R.id.layout_container, ReviewSummary.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                        break;
                    case "auditing":
                        setEditApp(true);
                        setApplicationResponse(applicationResponses.get(position));
                        updateAppInNavigation(applicationResponses.get(position));
                        openFragment(R.id.layout_container, ReviewAfterBeingAudited.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                        break;
                    case "completed":
                        setEditApp(false);
                        setApplicationResponse(applicationResponses.get(position));
                        updateAppInNavigation(applicationResponses.get(position));
                        openFragment(R.id.layout_container, ReviewSummary.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                        break;
                }
            }
        });
    }

    private void getAllApplication() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getAllApplication(UserManager.getUserToken()).enqueue(new Callback<List<ApplicationResponse>>() {
            @Override
            public void onResponse(Call<List<ApplicationResponse>> call, Response<List<ApplicationResponse>> response) {
                LogUtils.d(TAG, "getAllApplication code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getAllApplication body : " + response.body().toString());
                    applicationResponses = (ArrayList<ApplicationResponse>) response.body();
                    updateList();
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "getAllApplication error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
                ProgressDialogUtils.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<List<ApplicationResponse>> call, Throwable t) {
                LogUtils.e(TAG, "doCreateApplication onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getAllApplication();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }
}
