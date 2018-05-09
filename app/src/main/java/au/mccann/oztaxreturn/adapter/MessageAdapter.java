package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.BaseActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.model.Message;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.PxUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.TextViewCustom;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LongBui on 4/18/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private static final String TAG = MessageAdapter.class.getName();
    private ArrayList<Message> messages;
    private Context context;

    public MessageAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
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
                .inflate(R.layout.item_message, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Message message = messages.get(position);

        if (message.isManager) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);

            Utils.displayImageAvatar(context, holder.imgLeftAvata, message.getAvatarUrl());
            holder.tvLeftTimeAgo.setText(DateTimeUtils.getTimeAgo(message.getCreatedAt(), context));

            if (message.getAttachment() != null) {
                holder.imgLeft.setVisibility(View.VISIBLE);
                holder.tvLeftContent.setVisibility(View.GONE);
                Utils.displayImageRounded(context, holder.imgLeft, message.getAttachment().getUrl(), (int) PxUtils.pxFromDp(context, 10), 0);
                holder.imgLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PreviewImageActivity.class);
                        intent.putExtra(Constants.EXTRA_IMAGE_PATH, message.getAttachment().getUrl());
                        ((BaseActivity) context).startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
                    }
                });
            } else {
                holder.imgLeft.setVisibility(View.GONE);
                holder.tvLeftContent.setVisibility(View.VISIBLE);
                holder.tvLeftContent.setText(message.getMessage());
            }

        } else {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);

            holder.tvRightTimeAgo.setText(DateTimeUtils.getTimeAgo(message.getCreatedAt(), context));

            if (message.getAttachment() != null) {
                holder.imgRight.setVisibility(View.VISIBLE);
                holder.tvRightContent.setVisibility(View.GONE);
                Utils.displayImageRounded(context, holder.imgRight, message.getAttachment().getUrl(), (int) PxUtils.pxFromDp(context, 10), 0);
                holder.imgRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PreviewImageActivity.class);
                        intent.putExtra(Constants.EXTRA_IMAGE_PATH, message.getAttachment().getUrl());
                        ((BaseActivity) context).startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
                    }
                });

            } else {
                holder.imgRight.setVisibility(View.GONE);
                holder.tvRightContent.setVisibility(View.VISIBLE);
                holder.tvRightContent.setText(message.getMessage());
            }
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextViewCustom tvLeftContent, tvLeftTimeAgo, tvRightContent, tvRightTimeAgo;
        CircleImageView imgLeftAvata;
        ImageView imgLeft, imgRight;
        LinearLayout leftLayout, rightLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgLeftAvata = itemView.findViewById(R.id.img_left_avata);
            tvLeftContent = itemView.findViewById(R.id.tv_left_content);
            tvLeftTimeAgo = itemView.findViewById(R.id.tv_left_time_ago);
            tvRightContent = itemView.findViewById(R.id.tv_right_content);
            tvRightTimeAgo = itemView.findViewById(R.id.tv_right_time_ago);
            leftLayout = itemView.findViewById(R.id.left_layout);
            rightLayout = itemView.findViewById(R.id.right_layout);
            imgLeft = itemView.findViewById(R.id.img_left);
            imgRight = itemView.findViewById(R.id.img_right);
        }

    }


}
