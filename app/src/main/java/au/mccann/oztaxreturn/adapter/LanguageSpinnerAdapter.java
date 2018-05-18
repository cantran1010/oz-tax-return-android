package au.mccann.oztaxreturn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.rest.response.Language;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.TextViewCustom;


/**
 * Created by CanTran on 11/2/17.
 */

public class LanguageSpinnerAdapter extends BaseAdapter {
    private final Context context;
    private final List<Language> list;

    public LanguageSpinnerAdapter(Context context, List<Language> list) {
        this.context = context;
        this.list = list;
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
        view = LayoutInflater.from(context).inflate(R.layout.item_spinner_language, null);
        TextViewCustom tvCountry = view.findViewById(R.id.tv_country);
        ImageView imgFlag = view.findViewById(R.id.img_flag);
        tvCountry.setText(list.get(i).getName());
        Utils.displayImage(context, imgFlag, list.get(i).getIcon());
        return view;
    }

}