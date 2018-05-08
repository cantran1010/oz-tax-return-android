package au.mccann.oztaxreturn.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.NotificationAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Notification;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.Utils;
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
    private NotificationAdapter notificationAdapter;

    @Override
    protected int getLayout() {
        return R.layout.notification_fragment;
    }

    @Override
    protected void initView() {
        rcvList = (RecyclerView) findViewById(R.id.rcv_list);
    }

    @Override
    protected void initData() {

        setTitle(getString(R.string.notification_title));
        appBarVisibility(true, false, 0);

        getNotification(DateTimeUtils.fromCalendarToDateNotification(Calendar.getInstance()),200);
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
        notificationAdapter = new NotificationAdapter(notifications, getActivity());
        rcvList.setAdapter(notificationAdapter);
    }


    private void getNotification(final String since, final int limmit) {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getNotitifications(UserManager.getUserToken(), since,limmit).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getNotification code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getNotification body : " + response.body());
                    notifications = (ArrayList<Notification>) response.body();
                    updateList();
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
