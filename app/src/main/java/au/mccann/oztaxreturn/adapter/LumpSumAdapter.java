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

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.LumpSum;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

/**
 * Created by cantran on 4/18/18.
 */

public class LumpSumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private final ArrayList<LumpSum> lumpSums;
    private final Context context;
    private boolean isEdit;
    private boolean isExpend;
    private boolean onBind;

    public interface OnClickImageListener {
        void onClick(int position, int n);
    }

    public interface OnclickDateListener {
        void onClick(int position);
    }

    private OnClickImageListener onClickImageListener;
    private OnclickDateListener onclickDateListener;

    public void setOnClickImageListener(OnClickImageListener onClickImageListener) {
        this.onClickImageListener = onClickImageListener;
    }

    public void setOnclickDateListener(OnclickDateListener onclickDateListener) {
        this.onclickDateListener = onclickDateListener;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isExpend() {
        return isExpend;
    }

    public LumpSumAdapter(Context context, ArrayList<LumpSum> lumpSums) {
        this.context = context;
        this.lumpSums = lumpSums;
        if (lumpSums != null && lumpSums.size() > 0) isExpend = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: {
                //Inflating header view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lump_sums_header, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case TYPE_FOOTER: {
                //Inflating footer view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_footer, parent, false);
                return new FooterViewHolder(itemView);
            }
            default: {
                //Inflating recycle view item layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lump_sums, parent, false);
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
            LogUtils.d("onBindViewHolder", lumpSums.toString() + "position" + position);
            final LumpSum lumpSum = lumpSums.get(position - 1);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (isExpend) {
                itemViewHolder.expandableLayout.setExpanded(true);
            } else itemViewHolder.expandableLayout.setExpanded(false);
            if (isEdit) {
                itemViewHolder.edtEdtTaxWidthheld.setEnabled(true);
                itemViewHolder.edtTaxed.setEnabled(true);
                itemViewHolder.edtUnTaxed.setEnabled(true);
                itemViewHolder.edtPayerAbn.setEnabled(true);
                itemViewHolder.edtPaymenDate.setEnabled(true);
                itemViewHolder.grImage.setEnabled(true);
            } else {
                itemViewHolder.edtEdtTaxWidthheld.setEnabled(false);
                itemViewHolder.edtTaxed.setEnabled(false);
                itemViewHolder.edtUnTaxed.setEnabled(false);
                itemViewHolder.edtPayerAbn.setEnabled(false);
                itemViewHolder.edtPaymenDate.setEnabled(false);
                itemViewHolder.grImage.setEnabled(false);
            }
            itemViewHolder.edtEdtTaxWidthheld.setText(lumpSum.getTaxWithheld());
            itemViewHolder.edtTaxed.setText(lumpSum.getTaxableComTaxed());
            itemViewHolder.edtUnTaxed.setText(lumpSum.getTaxableComUntaxed());
            itemViewHolder.edtPayerAbn.setText(lumpSum.getPayerAbn());
            itemViewHolder.edtPaymenDate.setText(lumpSum.getPaymentDate());

            if (lumpSum.getImages() == null || lumpSum.getImages().size() == 0) {
                final Image image = new Image();
                image.setId(0);
                image.setAdd(true);
                lumpSum.getImages().add(image);
            }
            ImageAdapter imageAdapter = new ImageAdapter(context, lumpSum.getImages());
            itemViewHolder.grImage.setAdapter(imageAdapter);
            itemViewHolder.grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int n, long id) {
                    if (onClickImageListener != null) onClickImageListener.onClick(position - 1, n);
                }
            });

            ((ItemViewHolder) holder).edtEdtTaxWidthheld.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    lumpSums.get(position - 1).setTaxWithheld(editable.toString().trim());
                }
            });
            ((ItemViewHolder) holder).edtTaxed.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    lumpSums.get(position - 1).setTaxableComTaxed(editable.toString().trim());
                }
            });
            ((ItemViewHolder) holder).edtUnTaxed.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    lumpSums.get(position - 1).setTaxableComUntaxed(editable.toString().trim());
                }
            });
            ((ItemViewHolder) holder).edtPayerAbn.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    lumpSums.get(position - 1).setPayerAbn(editable.toString().trim());
                }
            });
            ((ItemViewHolder) holder).edtPaymenDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onclickDateListener != null) onclickDateListener.onClick(position - 1);

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
                        LumpSum dividend = new LumpSum();
                        dividend.setTaxWithheld(null);
                        dividend.setTaxableComTaxed(null);
                        dividend.setTaxableComUntaxed(null);
                        dividend.setPayerAbn(null);
                        dividend.setPaymentDate(null);
                        dividend.setImages(new ArrayList<Image>());
                        dividend.setAttach(new ArrayList<Attachment>());
                        AddList(dividend);
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
        } else if (position == lumpSums.size() + 1) {
            return TYPE_FOOTER;
        }
        return position + 1;
    }

    @Override
    public int getItemCount() {
        return lumpSums.size() + 2;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        final RadioButtonCustom rbYes;
        final RadioButtonCustom rbNo;

        HeaderViewHolder(View view) {
            super(view);
            rbYes = itemView.findViewById(R.id.rb_yes);
            rbNo = itemView.findViewById(R.id.rb_no);
            rbYes.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (!onBind) {
                isExpend = b;
                LogUtils.d("adapter", "isExpend :" + isExpend);
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
        final EdittextCustom edtEdtTaxWidthheld;
        final EdittextCustom edtTaxed;
        final EdittextCustom edtUnTaxed;
        final EdittextCustom edtPayerAbn;
        final EdittextCustom edtPaymenDate;
        final MyGridView grImage;

        ItemViewHolder(View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expand_layout);
            edtEdtTaxWidthheld = itemView.findViewById(R.id.edt_tax_widthheld);
            edtTaxed = itemView.findViewById(R.id.edt_taxed);
            edtUnTaxed = itemView.findViewById(R.id.edt_untaxed);
            edtPayerAbn = itemView.findViewById(R.id.edt_payer_abn);
            edtPaymenDate = itemView.findViewById(R.id.edt_payment_date);
            grImage = itemView.findViewById(R.id.gr_image);
        }
    }

    private void AddList(LumpSum lumpSum) {
        if (lumpSums.size() > 2) return;
        lumpSums.add(lumpSum);
        notifyDataSetChanged();

    }


}