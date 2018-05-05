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
import au.mccann.oztaxreturn.model.Dividend;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.utils.LogUtils;
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
    private static final int TYPE_ITEM = 2;
    private ArrayList<Dividend> dividends;
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

    public DividendAdapter(Context context, ArrayList<Dividend> dividends) {
        this.context = context;
        this.dividends = dividends;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_footer, parent, false);
            return new FooterViewHolder(itemView);
        } else {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
            Dividend dividend = dividends.get(position - 1);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (isExpend) {
                itemViewHolder.expandableLayout.setExpanded(true);
            } else itemViewHolder.expandableLayout.setExpanded(false);
            if (isEdit) {
                itemViewHolder.edtCompanyName.setEnabled(true);
                itemViewHolder.edtUnFrankDividends.setEnabled(true);
                itemViewHolder.edtFrankDividends.setEnabled(true);
                itemViewHolder.edtFrankingCredits.setEnabled(true);
                itemViewHolder.edtTaxWidthheld.setEnabled(true);
                itemViewHolder.grImage.setEnabled(true);
            } else {
                itemViewHolder.edtCompanyName.setEnabled(false);
                itemViewHolder.edtUnFrankDividends.setEnabled(false);
                itemViewHolder.edtFrankDividends.setEnabled(false);
                itemViewHolder.edtFrankingCredits.setEnabled(false);
                itemViewHolder.edtTaxWidthheld.setEnabled(false);
                itemViewHolder.grImage.setEnabled(false);
            }
            itemViewHolder.edtCompanyName.setText(dividend.getCompanyName());
            itemViewHolder.edtUnFrankDividends.setText(dividend.getUnfranked());
            itemViewHolder.edtFrankDividends.setText(dividend.getUnfranked());
            itemViewHolder.edtFrankingCredits.setText(dividend.getFrankingCredits());
            itemViewHolder.edtTaxWidthheld.setText(dividend.getTaxWithheld());
            if (dividend.getImages() == null || dividend.getImages().size() == 0) {
                final Image image = new Image();
                image.setId(0);
                image.setAdd(true);
                dividend.getImages().add(image);
            }
            ImageAdapter imageAdapter = new ImageAdapter(context, dividend.getImages());
            itemViewHolder.grImage.setAdapter(imageAdapter);
            itemViewHolder.grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int n, long id) {
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
                    dividends.get(position - 1).setUnfranked(editable.toString().trim());
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
                    dividends.get(position - 1).setFranked(editable.toString().trim());
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
                    dividends.get(position - 1).setFrankingCredits(editable.toString().trim());
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
                    dividends.get(position - 1).setTaxWithheld(editable.toString().trim());
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder itemViewHolder = (FooterViewHolder) holder;
            itemViewHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dividend dividend = new Dividend();
                    dividend.setImages(new ArrayList<Image>());
                    dividend.setAttach(new ArrayList<Attachment>());
                    AddList(dividend);
                }
            });
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
        EdittextCustom edtCompanyName, edtUnFrankDividends, edtFrankDividends, edtFrankingCredits, edtTaxWidthheld;
        MyGridView grImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expand_layout);
            edtCompanyName = itemView.findViewById(R.id.edt_dividends_bank_name);
            edtUnFrankDividends = itemView.findViewById(R.id.edt_unfrank_dividends);
            edtFrankDividends = itemView.findViewById(R.id.edt_franked_dividends);
            edtFrankingCredits = itemView.findViewById(R.id.edt_franking_credits);
            edtTaxWidthheld = itemView.findViewById(R.id.edt_tax_widthheld);
            grImage = itemView.findViewById(R.id.gr_image);
        }
    }

    public void AddList(Dividend dividend) {
        if (dividends.size() > 3) return;
        dividends.add(dividend);
        notifyDataSetChanged();
    }


}