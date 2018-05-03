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
import au.mccann.oztaxreturn.model.Annuity;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

/**
 * Created by cantran on 4/18/18.
 */

public class AnnuitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;
    private ArrayList<Annuity> annuities;
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

    public AnnuitiesAdapter(Context context, ArrayList<Annuity> annuities) {
        this.context = context;
        this.annuities = annuities;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_annuities_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend_footer, parent, false);
            return new FooterViewHolder(itemView);
        } else return null;
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
            LogUtils.d("onBindViewHolder", annuities.toString() + "position" + position);
            final Annuity annuity = annuities.get(position - 1);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if (isExpend) {
                itemViewHolder.expandableLayout.setExpanded(true);
            } else itemViewHolder.expandableLayout.setExpanded(false);
            if (isEdit) {
                itemViewHolder.edtEdtTaxWidthheld.setEnabled(true);
                itemViewHolder.edtTaxed.setEnabled(true);
                itemViewHolder.edtUnTaxed.setEnabled(true);
                itemViewHolder.edtLumpTaxed.setEnabled(true);
                itemViewHolder.edtLumpUnTaxed.setEnabled(true);
                itemViewHolder.grImage.setEnabled(true);
            } else {
                itemViewHolder.edtEdtTaxWidthheld.setEnabled(false);
                itemViewHolder.edtTaxed.setEnabled(false);
                itemViewHolder.edtUnTaxed.setEnabled(false);
                itemViewHolder.edtLumpTaxed.setEnabled(false);
                itemViewHolder.edtLumpUnTaxed.setEnabled(false);
                itemViewHolder.grImage.setEnabled(false);
            }
            itemViewHolder.edtEdtTaxWidthheld.setText(annuity.getTaxWithheld());
            itemViewHolder.edtTaxed.setText(annuity.getTaxableComTaxed());
            itemViewHolder.edtUnTaxed.setText(annuity.getTaxableComUntaxed());
            itemViewHolder.edtLumpTaxed.setText(annuity.getArrearsTaxed());
            itemViewHolder.edtLumpUnTaxed.setText(annuity.getArrearsUntaxed());

            if (annuity.getImages() == null || annuity.getImages().size() == 0) {
                final Image image = new Image();
                image.setId(0);
                image.setAdd(true);
                annuity.getImages().add(image);
            }
            ImageAdapter imageAdapter = new ImageAdapter(context, annuity.getImages());
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
                    annuities.get(position - 1).setTaxWithheld(editable.toString().trim());
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
                    annuities.get(position - 1).setTaxableComTaxed(editable.toString().trim());
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
                    annuities.get(position - 1).setTaxableComUntaxed(editable.toString().trim());
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
                    annuities.get(position - 1).setArrearsTaxed(editable.toString().trim());
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
                    annuities.get(position - 1).setArrearsUntaxed(editable.toString().trim());
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder itemViewHolder = (FooterViewHolder) holder;
            itemViewHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Annuity dividend = new Annuity();
                    dividend.setTaxWithheld("");
                    dividend.setTaxableComTaxed("");
                    dividend.setTaxableComUntaxed("");
                    dividend.setArrearsTaxed("");
                    dividend.setArrearsUntaxed("");
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
        } else if (position == annuities.size() + 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return annuities.size() + 2;
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
                LogUtils.d("adapter", "isExpend :" + isExpend);
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
        EdittextCustom edtEdtTaxWidthheld, edtTaxed, edtUnTaxed, edtLumpTaxed, edtLumpUnTaxed;
        MyGridView grImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expand_layout);
            edtEdtTaxWidthheld = itemView.findViewById(R.id.edt_tax);
            edtTaxed = itemView.findViewById(R.id.edt_taxed);
            edtUnTaxed = itemView.findViewById(R.id.edt_untaxed);
            edtLumpTaxed = itemView.findViewById(R.id.edt_lump_taxed);
            edtLumpUnTaxed = itemView.findViewById(R.id.edt_lump_untaxed);
            grImage = itemView.findViewById(R.id.gr_image);
        }
    }

    public void AddList(Annuity annuity) {
        if (annuities.size() > 3) return;
        annuities.add(annuity);
        notifyDataSetChanged();
    }


}