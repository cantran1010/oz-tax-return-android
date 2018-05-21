package au.mccann.oztaxreturn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.OtherResponse;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.view.EditTextEasyMoney;
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
    private final ArrayList<OtherResponse> otherResponses;
    private final Context context;
    private boolean isEdit;
    private boolean isExpend;
    private boolean onBind;
    private List<String> types = new ArrayList<>();

    public interface OnClickImageListener {
        void onClick(int position, int n);
    }

    public interface OnSelectedListener {
        void selected(int position, String type);
    }

    private OnClickImageListener onClickImageListener;
    private OnSelectedListener onSelectedListener;

    public void setOnClickImageListener(OnClickImageListener onClickImageListener) {
        this.onClickImageListener = onClickImageListener;
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
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
        if (otherResponses != null && otherResponses.size() > 0) isExpend = true;
        types = Arrays.asList(context.getResources().getStringArray(R.array.string_array_other_type));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: {
                //Inflating header view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_header, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case TYPE_FOOTER: {
                //Inflating footer view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_footer, parent, false);
                return new FooterViewHolder(itemView);
            }
            default: {
                //Inflating recycle view item layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other, parent, false);
                return new ItemViewHolder(itemView);
            }
        }
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
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
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (isExpend) {
                itemViewHolder.expandableLayout.setExpanded(true);
            } else itemViewHolder.expandableLayout.setExpanded(false);
            if (isEdit) {
                itemViewHolder.spType.setEnabled(true);
                itemViewHolder.edtDes.setEnabled(true);
                itemViewHolder.edtAmount.setEnabled(true);
                itemViewHolder.grImage.setEnabled(true);
            } else {
                itemViewHolder.spType.setEnabled(false);
                itemViewHolder.edtDes.setEnabled(false);
                itemViewHolder.edtAmount.setEnabled(false);
                itemViewHolder.grImage.setEnabled(false);
            }
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

            OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(context, types);
            itemViewHolder.spType.setAdapter(dataNameAdapter);
            itemViewHolder.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (onSelectedListener != null)
                        onSelectedListener.selected(position - 1, types.get(i));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            for (int i = 0; i < types.size(); i++) {
                if (otherResponse.getType().equalsIgnoreCase(types.get(i))) {
                    itemViewHolder.spType.setSelection(i);
                    break;
                } else itemViewHolder.spType.setSelection(0);
            }
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
                    otherResponses.get(position - 1).setAmount(itemViewHolder.edtAmount.getValuesFloat());
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder itemViewHolder = (FooterViewHolder) holder;
            if (isExpend) {
                itemViewHolder.footerLayout.setVisibility(View.VISIBLE);
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
            } else
                itemViewHolder.footerLayout.setVisibility(View.GONE);
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
        final RadioButtonCustom rbYes;
        final RadioButtonCustom rbNo;

        HeaderViewHolder(View view) {
            super(view);
            rbYes = view.findViewById(R.id.rb_yes);
            rbNo = view.findViewById(R.id.rb_no);
            rbYes.setOnCheckedChangeListener(this);
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
        final FloatingActionButton flAdd;
        final RelativeLayout footerLayout;

        FooterViewHolder(View view) {
            super(view);
            flAdd = view.findViewById(R.id.add);
            footerLayout = view.findViewById(R.id.layout_footer);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        final ExpandableLayout expandableLayout;
        final Spinner spType;
        final EdittextCustom edtDes;
        final EditTextEasyMoney edtAmount;
        final MyGridView grImage;

        ItemViewHolder(View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expand_layout);
            spType = itemView.findViewById(R.id.sp_type);
            edtDes = itemView.findViewById(R.id.edt_description);
            edtAmount = itemView.findViewById(R.id.edt_education_amount);
            grImage = itemView.findViewById(R.id.gr_image);
        }
    }

    private void AddList(OtherResponse e) {
        if (otherResponses.size() > 2) return;
        otherResponses.add(e);
        notifyDataSetChanged();
    }


}