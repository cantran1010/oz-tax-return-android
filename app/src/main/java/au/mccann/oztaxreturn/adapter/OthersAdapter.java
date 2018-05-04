package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.OtherResponse;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

/**
 * Created by cantran on 4/18/18.
 */

public class OthersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private ArrayList<OtherResponse> otherResponses;
    private Context context;
    private boolean isEdit;
    private boolean isExpend = true;
    private boolean onBind;

    public interface OnClickImageListener {
        void onClick(int position, int n);
    }

    private OnClickImageListener onClickImageListener;

    public void setOnClickImageListener(OnClickImageListener onClickImageListener) {
        this.onClickImageListener = onClickImageListener;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isExpend() {
        return isExpend;
    }

    public OthersAdapter(Context context, ArrayList<OtherResponse> otherResponses) {
        this.context = context;
        this.otherResponses = otherResponses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_footer, parent, false);
            return new FooterViewHolder(itemView);
        } else {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            onBind = true;
            headerViewHolder.rbYes.setChecked(isExpend);
            if (isEdit) {
                headerViewHolder.rbYes.setEnabled(true);
                headerViewHolder.rbNo.setEnabled(true);
            } else {
                headerViewHolder.rbYes.setEnabled(false);
                headerViewHolder.rbNo.setEnabled(false);
            }
        } else if (holder instanceof ItemViewHolder) {
            LogUtils.d("onBindViewHolder", otherResponses.toString() + "position" + position);
            OtherResponse otherResponse = otherResponses.get(position - 1);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (isExpend) {
                itemViewHolder.expandableLayout.setExpanded(true);
            } else itemViewHolder.expandableLayout.setExpanded(false);
            if (isEdit) {
                itemViewHolder.edtType.setEnabled(true);
                itemViewHolder.edtDes.setEnabled(true);
                itemViewHolder.edtAmount.setEnabled(true);
                itemViewHolder.grImage.setEnabled(true);
            } else {
                itemViewHolder.edtType.setEnabled(false);
                itemViewHolder.edtDes.setEnabled(false);
                itemViewHolder.edtAmount.setEnabled(false);
                itemViewHolder.grImage.setEnabled(false);
            }
            itemViewHolder.edtType.setText(otherResponse.getType());
            itemViewHolder.edtDes.setText(otherResponse.getDescription());
            itemViewHolder.edtAmount.setText(otherResponse.getAmount());
            if (otherResponse.getImages() == null || otherResponse.getImages().size() == 0) {
                final Image image = new Image();
                image.setId(0);
                image.setAdd(true);
                otherResponse.getImages().add(image);
            }
            ImageAdapter imageAdapter = new ImageAdapter(context, otherResponse.getImages());
            itemViewHolder.grImage.setAdapter(imageAdapter);
            itemViewHolder.grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int n, long id) {
                    if (onClickImageListener != null) onClickImageListener.onClick(position - 1, n);
                }
            });

            ((ItemViewHolder) holder).edtType.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    otherResponses.get(position - 1).setType(editable.toString().trim());
                }
            });
            ((ItemViewHolder) holder).edtDes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    otherResponses.get(position - 1).setDescription(editable.toString().trim());
                }
            });
            ((ItemViewHolder) holder).edtAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    otherResponses.get(position - 1).setAmount(editable.toString().trim());
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder itemViewHolder = (FooterViewHolder) holder;
            if (isEdit) itemViewHolder.flAdd.setEnabled(true);
            else {
                itemViewHolder.flAdd.setEnabled(false);
            }
            itemViewHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OtherResponse otherResponse = new OtherResponse();
                    otherResponse.setImages(new ArrayList<Image>());
                    otherResponse.setAttach(new ArrayList<Attachment>());
                    AddList(otherResponse);
                }
            });
        }
        onBind = false;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == otherResponses.size() + 1) {
            return TYPE_FOOTER;
        }
        return position + 1;
    }

    @Override
    public int getItemCount() {
        return otherResponses.size() + 2;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        RadioButtonCustom rbYes, rbNo;

        public HeaderViewHolder(View view) {
            super(view);
            rbYes = itemView.findViewById(R.id.rb_yes);
            rbNo = itemView.findViewById(R.id.rb_no);
            rbYes.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (!onBind) {
                isExpend = b;
                notifyDataSetChanged();
            }

        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        FloatingActionButton flAdd;

        public FooterViewHolder(View view) {
            super(view);
            flAdd = itemView.findViewById(R.id.add);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        ExpandableLayout expandableLayout;
        EdittextCustom edtType, edtDes, edtAmount;
        MyGridView grImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expand_layout);
            edtType = itemView.findViewById(R.id.edt_deduction_type);
            edtDes = itemView.findViewById(R.id.edt_description);
            edtAmount = itemView.findViewById(R.id.edt_education_amount);
            grImage = itemView.findViewById(R.id.gr_image);
        }
    }

    public void AddList(OtherResponse e) {
        if (otherResponses.size() > 3) return;
        otherResponses.add(e);
        notifyDataSetChanged();
    }


}