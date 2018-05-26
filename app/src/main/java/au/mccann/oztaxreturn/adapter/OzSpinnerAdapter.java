package au.mccann.oztaxreturn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.TextViewCustom;


/**
 * Created by CanTran on 11/2/17.
 */

public class OzSpinnerAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> list;
    private final int color;

    public OzSpinnerAdapter(Context context, List<String> list, int color) {
        this.context = context;
        this.list = list;
        this.color = color;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_spinner_hozo, null);
        TextViewCustom names = view.findViewById(R.id.textView);
        if (color == 1)
            names.setTextColor(ContextCompat.getColor(context, R.color.app_bg));
        else
            names.setTextColor(ContextCompat.getColor(context, R.color.income_conten));

        names.setText(list.get(i));
        return view;
    }

}