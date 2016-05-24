package org.researchstack.molemapper.ui;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

public class StatusBarUtils
{
    public static void adjustForTransparentStatusBar(View... views)
    {
        int statusBarHeight = getStatusBarHeight(views[0].getContext());

        for(View v : views)
        {
            RelativeLayout.MarginLayoutParams params = (RelativeLayout.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = statusBarHeight;
            v.requestLayout();
        }
    }

    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        else
        {
            result = (int) (context.getResources().getDisplayMetrics().density * 25 + .5f);
        }
        return result;
    }
}
