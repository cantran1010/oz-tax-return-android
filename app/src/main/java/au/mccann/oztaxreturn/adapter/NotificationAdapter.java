package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Notification;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.TextViewCustom;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LongBui on 4/18/18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private static final String TAG = NotificationAdapter.class.getName();
    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
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
                .inflate(R.layout.item_notification, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Notification notification = notifications.get(position);

        Utils.displayImageAvatar(context, holder.imgAvata, notification.getManagerAvatar());

        holder.tvContent.setText(Utils.getContentNotification(context, notification));
        holder.tvTimeAgo.setText(DateTimeUtils.getTimeAgo(notification.getCreatedAt(), context));

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextViewCustom tvContent, tvTimeAgo;
        CircleImageView imgAvata;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgAvata = itemView.findViewById(R.id.img_avata);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTimeAgo = itemView.findViewById(R.id.tv_time);
        }
    }


}
