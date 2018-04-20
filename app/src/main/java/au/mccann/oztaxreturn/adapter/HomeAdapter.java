package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.view.TextViewCustom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/18/18.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private static final String TAG = HomeAdapter.class.getName();
    private ArrayList<ApplicationResponse> applicationResponses;
    private Context context;

    public HomeAdapter(ArrayList<ApplicationResponse> applicationResponses, Context context) {
        this.applicationResponses = applicationResponses;
        this.context = context;
    }

    public interface OnClickListener {
        public void onClick(int position);
    }

    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final ApplicationResponse applicationResponse = applicationResponses.get(position);

        holder.tvName.setText(applicationResponse.getPayerName());
        holder.tvYear.setText(applicationResponse.getFinancialYear());

        holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClick(position);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showOkAndCancelDialog(context, context.getString(R.string.delete_application_title), context.getString(R.string.delete_application), context.getString(R.string.Yes), context.getString(R.string.No), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doDeleteApplication(position);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationResponses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextViewCustom tvStatus, tvName, tvYear, tvButton;
        RelativeLayout buttonLayout;
        ImageView imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvName = itemView.findViewById(R.id.tv_app_name);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvButton = itemView.findViewById(R.id.tv_button);
            buttonLayout = itemView.findViewById(R.id.layout_right);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    private void doDeleteApplication(final int position) {
        ProgressDialogUtils.showProgressDialog(context);

        ApiClient.getApiService().deleteApplication(UserManager.getUserToken(), applicationResponses.get(position).getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LogUtils.d(TAG, "doDeleteApplication , body : " + response.body());
                LogUtils.d(TAG, "doDeleteApplication , code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_NO_CONTENT) {
                    applicationResponses.remove(position);
                    notifyDataSetChanged();
                } else {
                    DialogUtils.showRetryDialog(context, new AlertDialogOkAndCancel.AlertDialogListener() {
                        @Override
                        public void onSubmit() {
                            doDeleteApplication(position);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
                ProgressDialogUtils.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogUtils.showRetryDialog(context, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doDeleteApplication(position);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                ProgressDialogUtils.dismissProgressDialog();
            }
        });
    }

}
