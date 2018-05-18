package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.PxUtils;
import au.mccann.oztaxreturn.utils.Utils;

/**
 * Created by LongBui on 4/19/2017.
 */

public class ImageAdapter extends ArrayAdapter<Image> {
    private static final String TAG = ImageAdapter.class.getName();
    private final ArrayList<Image> images;
    private boolean isRemove = true;

    public boolean isRemove() {
        return isRemove;
    }

    public void setRemove(boolean remove) {
        isRemove = remove;
    }

    public interface RemoveListener {
        public void remove(int position);
    }

    private RemoveListener removeListener;

    public RemoveListener getRemoveListener() {
        return removeListener;
    }

    public void setRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public ImageAdapter(Context _context, ArrayList<Image> images) {
        super(_context, R.layout.item_image, images);
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final Image item = getItem(position);

        LogUtils.d(TAG, "getView , item : " + (item != null ? item.toString() : null));

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_image, parent, false);
            holder = new ViewHolder();
            holder.imgImage = convertView.findViewById(R.id.img_image);
            holder.imgAdd = convertView.findViewById(R.id.img_add);
            holder.imgRemove = convertView.findViewById(R.id.img_remove);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (item != null && item.isAdd) {
            holder.imgImage.setVisibility(View.GONE);
            holder.imgAdd.setVisibility(View.VISIBLE);
            holder.imgRemove.setVisibility(View.GONE);
        } else {
            holder.imgImage.setVisibility(View.VISIBLE);
//            Utils.displayImageCenterCrop(getContext(), holder.imgImage, item != null ? item.getPath() : null);
            Utils.displayImageRounded(getContext(), holder.imgImage, item.getPath(), (int) PxUtils.pxFromDp(getContext(), 5), 0);

            holder.imgAdd.setVisibility(View.GONE);
            holder.imgRemove.setVisibility(View.VISIBLE);
        }

        holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRemove){
                    images.remove(position);
                    notifyDataSetChanged();
                }

                if (removeListener != null) removeListener.remove(position);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        ImageView imgImage, imgAdd, imgRemove;
    }
}
