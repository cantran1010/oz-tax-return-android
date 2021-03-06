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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Annuity;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.view.EditTextEasyMoney;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

/**
 * Created by cantran on 4/18/18.
 */

public class AnnuitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private final ArrayList<Annuity> annuities;
    private final Context context;
    private boolean isExpend;
    private boolean onBind;
    private boolean edit;

    public interface OnClickImageListener {
        void onClick(int position, int n);
    }

    public interface OnRemoveItem {
        void onDelete(int position);
    }

    private OnClickImageListener onClickImageListener;

    private OnRemoveItem onRemoveItem;

    public void setOnClickImageListener(OnClickImageListener onClickImageListener) {
        this.onClickImageListener = onClickImageListener;
    }

    public void setOnRemoveItem(OnRemoveItem onRemoveItem) {
        this.onRemoveItem = onRemoveItem;
    }


    public boolean isExpend() {
        return isExpend;
    }

    public AnnuitiesAdapter(Context context, ArrayList<Annuity> annuities, boolean edit) {
        this.context = context;
        this.annuities = annuities;
        this.edit = edit;
        if (annuities != null && annuities.size() > 0) isExpend = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: {
                //Inflating header view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_annuities_header, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case TYPE_FOOTER: {
                //Inflating footer view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_footer, parent, false);
                return new FooterViewHolder(itemView);
            }
            default: {
                //Inflating recycle view item layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_annuity, parent, false);
                return new ItemViewHolder(itemView);
            }
        }
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            onBind = true;
            headerViewHolder.rbYes.setChecked(isExpend);
            onBind = false;
            headerViewHolder.rbYes.setEnabled(edit);
            headerViewHolder.rbNo.setEnabled(edit);

        } else if (holder instanceof ItemViewHolder) {
            LogUtils.d("onBindViewHolder", annuities.toString() + "position" + position);
            final Annuity annuity = annuities.get(position - 1);
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.expandableLayout.setExpanded(isExpend);
            itemViewHolder.edtEdtTaxWidthheld.setEnabled(edit);
            itemViewHolder.edtTaxed.setEnabled(edit);
            itemViewHolder.edtUnTaxed.setEnabled(edit);
            itemViewHolder.edtLumpTaxed.setEnabled(edit);
            itemViewHolder.edtLumpUnTaxed.setEnabled(edit);
            itemViewHolder.imgDelete.setEnabled(edit);

            itemViewHolder.edtEdtTaxWidthheld.setText(annuity.getTaxWithheld());
            itemViewHolder.edtTaxed.setText(annuity.getTaxableComTaxed());
            itemViewHolder.edtUnTaxed.setText(annuity.getTaxableComUntaxed());
            itemViewHolder.edtLumpTaxed.setText(annuity.getArrearsTaxed());
            itemViewHolder.edtLumpUnTaxed.setText(annuity.getArrearsUntaxed());
            itemViewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRemoveItem != null) onRemoveItem.onDelete(position - 1);
                }
            });
            if (annuity.getImages() == null || annuity.getImages().size() == 0) {
                final Image image = new Image();
                image.setId(0);
                image.setAdd(true);
                annuity.getImages().add(image);
            }
            final ImageAdapter imageAdapter = new ImageAdapter(context, annuity.getImages());
            itemViewHolder.grImage.setAdapter(imageAdapter);
            imageAdapter.setRemove(edit);
            itemViewHolder.grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int n, long id) {
                    if (annuity.getImages().get(n).isAdd && !imageAdapter.isRemove()) return;
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
                    annuities.get(position - 1).setTaxWithheld(itemViewHolder.edtEdtTaxWidthheld.getValuesFloat());
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
                    annuities.get(position - 1).setTaxableComTaxed(itemViewHolder.edtTaxed.getValuesFloat());
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
                    annuities.get(position - 1).setTaxableComUntaxed(itemViewHolder.edtUnTaxed.getValuesFloat());
                }
            });
            ((ItemViewHolder) holder).edtLumpTaxed.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    annuities.get(position - 1).setArrearsTaxed(itemViewHolder.edtLumpTaxed.getValuesFloat());
                }
            });
            ((ItemViewHolder) holder).edtLumpUnTaxed.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    annuities.get(position - 1).setArrearsUntaxed(itemViewHolder.edtLumpUnTaxed.getValuesFloat());
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder itemViewHolder = (FooterViewHolder) holder;
            if (isExpend) {
                itemViewHolder.footerLayout.setVisibility(View.VISIBLE);
                itemViewHolder.flAdd.setEnabled(edit);
                itemViewHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Annuity dividend = new Annuity();
                        dividend.setTaxWithheld(null);
                        dividend.setTaxableComTaxed(null);
                        dividend.setTaxableComUntaxed(null);
                        dividend.setArrearsTaxed(null);
                        dividend.setArrearsUntaxed(null);
                        dividend.setImages(new ArrayList<Image>());
                        dividend.setAttach(new ArrayList<Attachment>());
                        AddList(dividend);
                    }
                });
            } else
                itemViewHolder.footerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == annuities.size() + 1) {
            return TYPE_FOOTER;
        }
        return position + 1;
    }

    @Override
    public int getItemCount() {
        return annuities.size() + 2;
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
        final EditTextEasyMoney edtEdtTaxWidthheld;
        final EditTextEasyMoney edtTaxed;
        final EditTextEasyMoney edtUnTaxed;
        final EditTextEasyMoney edtLumpTaxed;
        final EditTextEasyMoney edtLumpUnTaxed;
        final ImageView imgDelete;
        final MyGridView grImage;

        ItemViewHolder(View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expand_layout);
            imgDelete = itemView.findViewById(R.id.img_delete);
            edtEdtTaxWidthheld = itemView.findViewById(R.id.edt_tax);
            edtTaxed = itemView.findViewById(R.id.edt_taxed);
            edtUnTaxed = itemView.findViewById(R.id.edt_untaxed);
            edtLumpTaxed = itemView.findViewById(R.id.edt_lump_taxed);
            edtLumpUnTaxed = itemView.findViewById(R.id.edt_lump_untaxed);
            grImage = itemView.findViewById(R.id.gr_image);
        }
    }

    private void AddList(Annuity annuity) {
        if (annuities.size() > 2) return;
        annuities.add(annuity);
        notifyDataSetChanged();
    }


}