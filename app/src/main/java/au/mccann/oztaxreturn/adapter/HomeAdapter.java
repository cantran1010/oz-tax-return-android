package au.mccann.oztaxreturn.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by LongBui on 4/18/18.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private static final String TAG = HomeAdapter.class.getName();
    private ArrayList<ApplicationResponse> applicationResponses;

    public HomeAdapter(ArrayList<ApplicationResponse> applicationResponses) {
        this.applicationResponses = applicationResponses;
    }

    public interface OnClickListener {
        public void onClick();
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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ApplicationResponse applicationResponse = applicationResponses.get(position);

        holder.tvName.setText(applicationResponse.getPayerName());
        holder.tvYear.setText(applicationResponse.getFinancialYear());

        holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClick();
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

        public MyViewHolder(View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvName = itemView.findViewById(R.id.tv_app_name);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvButton = itemView.findViewById(R.id.tv_button);
            buttonLayout = itemView.findViewById(R.id.layout_right);
        }

    }

}
