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
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Dividend;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.view.EditTextEasyMoney;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

/**
 * Created by cantran on 4/18/18.
 */

public class DividendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private final ArrayList<Dividend> dividends;
    private final Context context;
    private boolean isEdit;
    private boolean isExpend;
    private boolean onBind;

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

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isExpend() {
        return isExpend;
    }

    public DividendAdapter(Context context, ArrayList<Dividend> dividends) {
        this.context = context;
        this.dividends = dividends;
        if (dividends != null && dividends.size() > 0) isExpend = true;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: {
                //Inflating header view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend_header, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case TYPE_FOOTER: {
                //Inflating footer view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_footer, parent, false);
                return new FooterViewHolder(itemView);
            }
            default: {
                //Inflating recycle view item layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend, parent, false);
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
            onBind = false;
            if (isEdit) {
                headerViewHolder.rbYes.setEnabled(true);
                headerViewHolder.rbNo.setEnabled(true);
            } else {
                headerViewHolder.rbYes.setEnabled(false);
                headerViewHolder.rbNo.setEnabled(false);
            }
        } else if (holder instanceof ItemViewHolder) {
            LogUtils.d("onBindViewHolder", dividends.toString() + "position" + position);
            final Dividend dividend = dividends.get(position - 1);
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (isExpend) {
                itemViewHolder.expandableLayout.setExpanded(true);
            } else itemViewHolder.expandableLayout.setExpanded(false);
            if (isEdit) {
                itemViewHolder.edtCompanyName.setEnabled(true);
                itemViewHolder.edtUnFrankDividends.setEnabled(true);
                itemViewHolder.edtFrankDividends.setEnabled(true);
                itemViewHolder.edtFrankingCredits.setEnabled(true);
                itemViewHolder.edtTaxWidthheld.setEnabled(true);
                itemViewHolder.imgDelete.setEnabled(true);
            } else {
                itemViewHolder.edtCompanyName.setEnabled(false);
                itemViewHolder.edtUnFrankDividends.setEnabled(false);
                itemViewHolder.edtFrankDividends.setEnabled(false);
                itemViewHolder.edtFrankingCredits.setEnabled(false);
                itemViewHolder.edtTaxWidthheld.setEnabled(false);
                itemViewHolder.imgDelete.setEnabled(false);
            }
            itemViewHolder.edtCompanyName.setText(dividend.getCompanyName());
            itemViewHolder.edtUnFrankDividends.setText(dividend.getUnfranked());
            itemViewHolder.edtFrankDividends.setText(dividend.getFranked());
            itemViewHolder.edtFrankingCredits.setText(dividend.getFrankingCredits());
            itemViewHolder.edtTaxWidthheld.setText(dividend.getTaxWithheld());
            itemViewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRemoveItem != null) onRemoveItem.onDelete(position - 1);
                }
            });
            if (dividend.getImages() == null || dividend.getImages().size() == 0) {
                final Image image = new Image();
                image.setId(0);
                image.setAdd(true);
                dividend.getImages().add(image);
            }
            final ImageAdapter imageAdapter = new ImageAdapter(context, dividend.getImages());
            itemViewHolder.grImage.setAdapter(imageAdapter);
            imageAdapter.setRemove(isEdit);
            itemViewHolder.grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int n, long id) {
                    if (dividend.getImages().get(n).isAdd && !imageAdapter.isRemove()) return;
                    if (onClickImageListener != null) onClickImageListener.onClick(position - 1, n);
                }
            });

            ((ItemViewHolder) holder).edtCompanyName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    dividends.get(position - 1).setCompanyName(charSequence.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            ((ItemViewHolder) holder).edtUnFrankDividends.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dividends.get(position - 1).setUnfranked(itemViewHolder.edtUnFrankDividends.getValueString());
                }
            });
            ((ItemViewHolder) holder).edtFrankDividends.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dividends.get(position - 1).setFranked(itemViewHolder.edtFrankDividends.getValueString());
                }
            });
            ((ItemViewHolder) holder).edtFrankingCredits.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dividends.get(position - 1).setFrankingCredits(itemViewHolder.edtFrankingCredits.getValueString());
                }
            });
            ((ItemViewHolder) holder).edtTaxWidthheld.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dividends.get(position - 1).setTaxWithheld(itemViewHolder.edtTaxWidthheld.getValueString());
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
                        Dividend dividend = new Dividend();
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
        } else if (position == dividends.size() + 1) {
            return TYPE_FOOTER;
        }
        return position + 1;
    }

    @Override
    public int getItemCount() {
        return dividends.size() + 2;
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
        final EdittextCustom edtCompanyName;
        final EditTextEasyMoney edtUnFrankDividends;
        final EditTextEasyMoney edtFrankDividends;
        final EditTextEasyMoney edtFrankingCredits;
        final EditTextEasyMoney edtTaxWidthheld;
        final MyGridView grImage;
        final ImageView imgDelete;

        ItemViewHolder(View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expand_layout);
            imgDelete = itemView.findViewById(R.id.img_delete);
            edtCompanyName = itemView.findViewById(R.id.edt_dividends_bank_name);
            edtUnFrankDividends = itemView.findViewById(R.id.edt_unfrank_dividends);
            edtFrankDividends = itemView.findViewById(R.id.edt_franked_dividends);
            edtFrankingCredits = itemView.findViewById(R.id.edt_franking_credits);
            edtTaxWidthheld = itemView.findViewById(R.id.edt_tax_widthheld);
            grImage = itemView.findViewById(R.id.gr_image);

        }
    }

    private void AddList(Dividend dividend) {
        if (dividends.size() > 2) return;
        dividends.add(dividend);
        notifyDataSetChanged();
    }


}