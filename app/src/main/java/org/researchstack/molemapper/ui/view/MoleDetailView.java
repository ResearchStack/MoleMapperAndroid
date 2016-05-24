package org.researchstack.molemapper.ui.view;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.MoleDetail;

/**
 * Created by bradleymcdermott on 2/22/16.
 */
public class MoleDetailView extends RelativeLayout
{
    TextView  moleName;
    TextView  lastChecked;
    TextView  zoneName;
    TextView  moleMeasurement;
    ImageView statusIcon;

    public MoleDetailView(Context context)
    {
        super(context);
    }

    public MoleDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MoleDetailView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        moleName = (TextView) findViewById(R.id.mole_name);
        lastChecked = (TextView) findViewById(R.id.mole_last_checked);
        zoneName = (TextView) findViewById(R.id.mole_zone);
        moleMeasurement = (TextView) findViewById(R.id.mole_measurement);
        statusIcon = (ImageView) findViewById(R.id.mole_progress);
    }

    public void setModel(MoleDetail detail, boolean listMode)
    {
        Context context = moleName.getContext();

        moleName.setText(detail.moleName);
        zoneName.setText(BodyZoneHelper.getTitle(moleName.getContext(), detail.zoneId));
        lastChecked.setText(detail.getLastCheckedText(context));
        moleMeasurement.setText(detail.getMeasurementText(context));
        statusIcon.setVisibility(listMode ? View.VISIBLE : View.GONE);

        int darkGray = ContextCompat.getColor(context, R.color.text_color_mole_active);
        int lightGray = ContextCompat.getColor(context, R.color.text_color_mole_inactive);
        int orange = ContextCompat.getColor(context, R.color.mm_orange);

        if(! listMode)
        {
            int blue = ContextCompat.getColor(context, R.color.mm_colorPrimary);

            moleName.setTextColor(blue);
            zoneName.setTextColor(blue);
            lastChecked.setTextColor(blue);
            moleMeasurement.setTextColor(blue);
            statusIcon.setVisibility(View.GONE);

            View moleTitleContainer = findViewById(R.id.mole_title_container);
            RelativeLayout.MarginLayoutParams params = (MarginLayoutParams) moleTitleContainer.getLayoutParams();
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.rsb_padding_medium);
            moleTitleContainer.requestLayout();

        }
        else if(detail.state == MoleDetail.State.UP_TO_DATE)
        {
            Drawable drawable = VectorDrawableCompat.create(context.getResources(),
                    R.drawable.vic_check_20dp,
                    null);
            statusIcon.setImageDrawable(drawable);

            zoneName.setTextColor(darkGray);
            lastChecked.setTextColor(darkGray);
            moleMeasurement.setTextColor(darkGray);
        }
        else
        {
            Drawable drawable = getTintedDrawable(context, R.drawable.rss_ic_circle_16dp, orange);
            statusIcon.setImageDrawable(drawable);

            zoneName.setTextColor(darkGray);
            lastChecked.setTextColor(lightGray);
            moleMeasurement.setTextColor(lightGray);
        }
    }

    @NonNull
    private Drawable getTintedDrawable(Context context, @DrawableRes int drawableRes, int color)
    {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        return tintDrawable(drawable, color);
    }

    @NonNull
    private Drawable tintDrawable(Drawable drawable, int color)
    {
        Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapped, color);
        return wrapped;
    }
}
