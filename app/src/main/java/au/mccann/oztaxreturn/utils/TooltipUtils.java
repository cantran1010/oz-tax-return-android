package au.mccann.oztaxreturn.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import au.mccann.oztaxreturn.R;

/**
 * Created by CanTran on 4/19/18.
 */
public class TooltipUtils {

    public static void showToolTipView(Context context, View anchorView, int gravity, CharSequence text, int backgroundColor) {
        showToolTipView(context, anchorView, gravity, text, backgroundColor, 0L);
    }

    private static void showToolTipView(Context context, final View anchorView, int gravity, CharSequence text, int backgroundColor, long delay) {
        if (anchorView.getTag() != null) {
            ((ToolTipView) anchorView.getTag()).remove();
            anchorView.setTag(null);
            return;
        }

        ToolTip toolTip = createToolTip(context, text, backgroundColor);
        ToolTipView toolTipView = createToolTipView(context, toolTip, anchorView, gravity);
        if (delay > 0L) {
            toolTipView.showDelayed(delay);
        } else {
            toolTipView.show();
        }
        anchorView.setTag(toolTipView);

        toolTipView.setOnToolTipClickedListener(new ToolTipView.OnToolTipClickedListener() {
            @Override
            public void onToolTipClicked(ToolTipView toolTipView) {
                anchorView.setTag(null);
            }
        });
    }

    private static ToolTip createToolTip(Context context, CharSequence text, int backgroundColor) {
        Resources resources = context.getResources();
        int padding = resources.getDimensionPixelSize(R.dimen.tooltip_padding);
        int textSize = resources.getDimensionPixelSize(R.dimen.tooltip_size);
        int radius = resources.getDimensionPixelSize(R.dimen.tooltip_radius);
        return new ToolTip.Builder()
                .withText(text)
                .withTextColor(Color.WHITE)
                .withTextSize(textSize)
                .withBackgroundColor(backgroundColor)
                .withPadding(padding, padding, padding, padding)
                .withCornerRadius(radius)
                .build();
    }

    private static ToolTipView createToolTipView(Context context, ToolTip toolTip, View anchorView, int gravity) {
        return new ToolTipView.Builder(context)
                .withAnchor(anchorView)
                .withToolTip(toolTip)
                .withGravity(gravity)
                .build();
    }
}
