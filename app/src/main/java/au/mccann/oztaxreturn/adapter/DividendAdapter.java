package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
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

    public interface OnClickImageListener {
        void onClick(int position);
    }

    public interface OnClickAddListener {
        void addClick(int position);
    }

    private OnClickAddListener onClickAddListener;

    private OnClickImageListener onClickImageListener;

    public OnClickAddListener getOnClickAddListener() {
        return onClickAddListener;
    }

    public void setOnClickAddListener(OnClickAddListener onClickAddListener) {
        this.onClickAddListener = onClickAddListener;
    }

    public OnClickImageListener getOnClickImageListener() {
        return onClickImageListener;
    }

    public void setOnClickImageListener(OnClickImageListener onClickImageListener) {
        this.onClickImageListener = onClickImageListener;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public DividendAdapter(Context context, ArrayList<Dividend> dividends) {
        this.context = context;
        this.dividends = dividends;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dividend_header, parent, false);
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
            headerViewHolder.rbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                    }
                }
            });
        } else if (holder instanceof ItemViewHolder) {
            LogUtils.d("onBindViewHolder", dividends.size() + "position" + position);
            Dividend dividend = dividends.get(position-1);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.edtCompanyName.setText(dividend.getCompanyName());
            itemViewHolder.edtUnFrankDividends.setText(dividend.getUnfranked());
            itemViewHolder.edtFrankDividends.setText(dividend.getUnfranked());
            itemViewHolder.edtFrankingCredits.setText(dividend.getFrankingCredits());
            itemViewHolder.edtTaxWidthheld.setText(dividend.getTaxWithheld());
            itemViewHolder.edtCompanyName.setText(dividend.getCompanyName());
            if (dividend.getImages().size() == 0) {
                final Image image = new Image();
                image.setId(0);
                image.setAdd(true);
                dividend.getImages().add(image);
            }
            ImageAdapter imageAdapter = new ImageAdapter(context, dividend.getImages());
            itemViewHolder.grImage.setAdapter(imageAdapter);
            itemViewHolder.grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (onClickImageListener != null) onClickImageListener.onClick(position);
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder itemViewHolder = (FooterViewHolder) holder;
            itemViewHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickAddListener != null) onClickAddListener.addClick(position);
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
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dividends.size() + 2;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        RadioButtonCustom rbYes;

        public HeaderViewHolder(View view) {
            super(view);
            rbYes = itemView.findViewById(R.id.rb_yes);
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

}