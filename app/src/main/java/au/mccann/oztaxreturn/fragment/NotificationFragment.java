package au.mccann.oztaxreturn.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.HomeActivity;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.adapter.NotificationAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Notification;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.TextViewCustom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/17/18.
 */

public class NotificationFragment extends BaseFragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    private RecyclerView rcvList;
    private ArrayList<Notification> notifications = new ArrayList<>();
    private TextViewCustom tvNoData;

    @Override
    protected int getLayout() {
        return R.layout.notification_fragment;
    }

    @Override
    protected void initView() {
        rcvList = (RecyclerView) findViewById(R.id.rcv_list);
        tvNoData = (TextViewCustom) findViewById(R.id.tv_no_data);
    }

    @Override
    protected void initData() {
        ((HomeActivity) getActivity()).setIndex(3);
        setTitle(getString(R.string.notification_title));
        appBarVisibility(true, false, 0);

        getNotification(null,300);
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }
    private void updateList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvList.setLayoutManager(linearLayoutManager);
        NotificationAdapter notificationAdapter = new NotificationAdapter(notifications, getActivity());
        rcvList.setAdapter(notificationAdapter);
    }

    private void getNotification(final String since, final int limmit) {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getNotitifications(UserManager.getUserToken(), null,limmit).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getNotification code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getNotification body : " + response.body());
                    notifications = (ArrayList<Notification>) response.body();

                    if(notifications.size() > 0){
                        rcvList.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                    }else {
                        rcvList.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                    updateList();
                }else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getNotification error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                LogUtils.e(TAG, "getNotification onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getNotification(since,limmit);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

}
