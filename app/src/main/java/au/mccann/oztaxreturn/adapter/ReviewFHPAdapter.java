package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.BaseActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.rest.response.ReviewPrivateResponse;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EditTextEasyMoney;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.MyGridView;

/**
 * Created by cantran on 4/18/18.
 */

public class ReviewFHPAdapter extends RecyclerView.Adapter<ReviewFHPAdapter.MyViewHolder> {
    private static final String TAG = ReviewFHPAdapter.class.getName();
    private ArrayList<ReviewPrivateResponse> reviewPrivateResponses;
    private Context context;
    private boolean isEdit;

    public interface PickImageListener {
        void doPick(int position);
    }

    public interface OnRemoveItem {
        void onDelete(int position);
    }

    private PickImageListener pickImageListener;
    private OnRemoveItem onRemoveItem;

    public ReviewFHPAdapter(ArrayList<ReviewPrivateResponse> reviewPrivateResponses, Context context) {
        this.reviewPrivateResponses = reviewPrivateResponses;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_family_health_private, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int positionBig) {
        LogUtils.d("onBindViewHolder", "edit" + isEdit());
//        EdittextCustom edtHealth, edtMembership, edtPremiums, edtGovernment, edtDay;
//        MyGridView myGridView;

        final ReviewPrivateResponse reviewPrivateResponse = reviewPrivateResponses.get(positionBig);
        holder.edtHealth.setText(reviewPrivateResponse.getInsurer());
        holder.edtMembership.setText(reviewPrivateResponse.getMembershipNo());
        holder.edtPremiums.setText(reviewPrivateResponse.getPremiumsPaid());
        holder.edtGovernment.setText(reviewPrivateResponse.getGovRebateReceived());
//        if (reviewPrivateResponse.getDaysCovered() != 0)
        holder.edtDay.setText(String.valueOf(reviewPrivateResponse.getDaysCovered()));

        if (isEdit()) {
            holder.edtHealth.setEnabled(true);
            holder.edtMembership.setEnabled(true);
            holder.edtPremiums.setEnabled(true);
            holder.edtGovernment.setEnabled(true);
            holder.edtDay.setEnabled(true);
            holder.imgDelete.setEnabled(true);
        } else {
            holder.edtHealth.setEnabled(false);
            holder.edtMembership.setEnabled(false);
            holder.edtPremiums.setEnabled(false);
            holder.edtGovernment.setEnabled(false);
            holder.edtDay.setEnabled(false);
            holder.imgDelete.setEnabled(false);
        }
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRemoveItem != null) onRemoveItem.onDelete(holder.getAdapterPosition());
            }
        });

        holder.edtHealth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty())
                    reviewPrivateResponses.get(holder.getAdapterPosition()).setInsurer(editable.toString().trim());
            }
        });

        holder.edtMembership.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty())
                    reviewPrivateResponses.get(holder.getAdapterPosition()).setMembershipNo(editable.toString().trim());
            }
        });

        holder.edtPremiums.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty())
                    reviewPrivateResponses.get(holder.getAdapterPosition()).setPremiumsPaid(holder.edtPremiums.getValuesFloat());
            }
        });

        holder.edtGovernment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                reviewPrivateResponses.get(holder.getAdapterPosition()).setGovRebateReceived(holder.edtGovernment.getValuesFloat());
            }
        });

        holder.edtDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty())
                    reviewPrivateResponses.get(holder.getAdapterPosition()).setDaysCovered(Integer.valueOf(editable.toString().trim()));
            }
        });

        // update grid
        final ArrayList<Image> images = new ArrayList<>();
        //images
        if (images.size() == 0) {
            final Image image = new Image();
            image.setId(-1);
            image.setAdd(true);
            images.add(image);
        }

        if (reviewPrivateResponse.getAttachments() != null && reviewPrivateResponse.getAttachments().size() > 0)
            for (Attachment attachment : reviewPrivateResponse.getAttachments()) {
                final Image image = new Image();
                image.setId(attachment.getId());
                image.setAdd(false);
                image.setPath(attachment.getUrl());
                images.add(0, image);
            }

        ImageAdapter imageAdapter = new ImageAdapter(context, images);
        holder.myGridView.setAdapter(imageAdapter);
        imageAdapter.setRemove(isEdit());

        imageAdapter.setRemoveListener(new ImageAdapter.RemoveListener() {
            @Override
            public void remove(int position) {
                LogUtils.d(TAG, "setRemoveListener , position : " + position);

                if (isEdit())
                    reviewPrivateResponse.getAttachments().remove(reviewPrivateResponse.getAttachments().size() - position - 1);
            }
        });

        holder.myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (images.get(position).isAdd) {
                    if (isEdit()) {
                        if (images.size() >= 10) {
                            Utils.showLongToast(context, context.getString(R.string.max_image_attach_err, 9), true, false);
                        } else {
                            if (getPickImageListener() != null)
                                getPickImageListener().doPick(holder.getAdapterPosition());
                        }
                    }
                } else {
                    Intent intent = new Intent(context, PreviewImageActivity.class);
                    intent.putExtra(Constants.EXTRA_IMAGE_PATH, images.get(position).getPath());
                    ((BaseActivity) context).startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return reviewPrivateResponses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        EdittextCustom edtHealth, edtMembership, edtDay;
        private EditTextEasyMoney edtPremiums, edtGovernment;
        MyGridView myGridView;
        ImageView imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            edtHealth = itemView.findViewById(R.id.edt_health);
            edtMembership = itemView.findViewById(R.id.edt_membership);
            imgDelete = itemView.findViewById(R.id.img_delete);
            edtPremiums = itemView.findViewById(R.id.edt_premium);
            edtGovernment = itemView.findViewById(R.id.edt_government);
            edtDay = itemView.findViewById(R.id.edt_day);
            myGridView = itemView.findViewById(R.id.gr_image);
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public PickImageListener getPickImageListener() {
        return pickImageListener;
    }

    public void setPickImageListener(PickImageListener pickImageListener) {
        this.pickImageListener = pickImageListener;
    }

    public void setOnRemoveItem(OnRemoveItem onRemoveItem) {
        this.onRemoveItem = onRemoveItem;
    }


}
