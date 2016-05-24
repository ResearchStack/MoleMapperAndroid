package org.researchstack.molemapper.ui.view;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.molemapper.BodyZoneActivity;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyMapView extends ImageView
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Absolute coordinates of back body zones
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final int STATE_BODY_FRONT = 0;
    public static final int STATE_BODY_BACK  = 1;
    public static final int STATE_BODY_HEAD  = 2;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Absolute coordinates of back body zones
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    Paint textPaint   = new Paint();
    Paint filledPaint = new Paint();

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Field Variables
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int currentSide;

    private SparseArray<Path>   paths           = new SparseArray<>();
    private SparseArray<Region> regions         = new SparseArray<>();
    private SparseArray<PointF> textCoordinates = new SparseArray<>();
    private SparseIntArray      moleCounts      = new SparseIntArray();

    public BodyMapView(Context context)
    {
        super(context);
        init();
    }

    public BodyMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BodyMapView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public BodyMapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setScaleType(ScaleType.CENTER_INSIDE);

        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12,
                getResources().getDisplayMetrics());

        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textPaint.setTextAlign(Paint.Align.CENTER);

        float shadowRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1,
                getResources().getDisplayMetrics());
        textPaint.setShadowLayer(shadowRadius, 0, 0, 0x66000000);

        filledPaint.setAntiAlias(true);
        filledPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        setSide(STATE_BODY_FRONT);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b)
    {
        boolean changed = super.setFrame(l, t, r, b);
        initPathsAndRegions();
        return changed;
    }

    private void initPathsAndRegions()
    {
        paths.clear();
        regions.clear();
        textCoordinates.clear();

        float[] matrixValues = new float[9];
        getImageMatrix().getValues(matrixValues);

        float bodyWidth = getDrawable().getBounds().width() * matrixValues[Matrix.MSCALE_X];
        float bodyHeight = getDrawable().getBounds().height() * matrixValues[Matrix.MSCALE_Y];
        float bodyTransX = matrixValues[Matrix.MTRANS_X];
        float bodyTransY = matrixValues[Matrix.MTRANS_Y];

        HashMap<Integer, ArrayList<PointF>> zoneArea = currentSide == STATE_BODY_BACK
                ? BodyZoneHelper.POINTS_BACK
                : BodyZoneHelper.POINTS_FRONT;

        for(Map.Entry<Integer, ArrayList<PointF>> pair : zoneArea.entrySet())
        {
            ArrayList<PointF> zone = pair.getValue();

            // Set points to our path
            Path path = new Path();
            for(int i = 0; i < zone.size(); i++)
            {
                PointF point = zone.get(i);
                float pX = bodyTransX + point.x * bodyWidth;
                float pY = bodyTransY + point.y * bodyHeight;

                if(i == 0)
                {
                    path.moveTo(pX, pY);
                }
                else
                {
                    path.lineTo(pX, pY);
                }
            }

            // Store path for future use
            path.close();
            paths.put(pair.getKey(), path);

            // Create and store region for future use
            Region region = new Region();
            region.setPath(path, new Region(0, 0, getWidth(), getHeight()));
            regions.put(pair.getKey(), region);

            // Create and store text positions
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            float centerY = (int) (bounds.centerY() -
                    ((textPaint.descent() + textPaint.ascent()) / 2));
            float centerX = bounds.centerX();

            // If we have some offsets, calculate those now
            HashMap<Integer, PointF> textOffsets = currentSide == STATE_BODY_BACK
                    ? BodyZoneHelper.TEXT_OFFSET_BACK
                    : BodyZoneHelper.TEXT_OFFSET_FRONT;

            if(textOffsets.containsKey(pair.getKey()))
            {
                centerX += bounds.width() * textOffsets.get(pair.getKey()).x;
                centerY += bounds.height() * textOffsets.get(pair.getKey()).y;
            }

            textCoordinates.put(pair.getKey(), new PointF(centerX, centerY));
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        for(int i = 0, size = moleCounts.size(); i < size; i++)
        {
            int key = moleCounts.keyAt(i);

            // Draw Highlight
            Path path = paths.get(key);
            if(path != null)
            {
                canvas.drawPath(path, filledPaint);
            }

            // Draw Text
            PointF textCoords = textCoordinates.get(key);
            if(textCoords != null)
            {
                canvas.drawText(Integer.toString(moleCounts.valueAt(i)),
                        textCoords.x,
                        textCoords.y,
                        textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            return true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            for(int i = 0, size = regions.size(); i < size; i++)
            {
                int zoneId = regions.keyAt(i);
                Region region = regions.get(zoneId);

                if(region.contains((int) event.getX(), (int) event.getY()))
                {
                    // User clicks head, show head-selection dialog
                    if(zoneId == BodyZoneHelper.FRONT_HEAD || zoneId == BodyZoneHelper.BACK_HEAD)
                    {
                        showHeadSelectionDialog();
                    }

                    // Else start BodyZoneActivity
                    else
                    {
                        Intent intent = BodyZoneActivity.newIntent(getContext(), zoneId);
                        getContext().startActivity(intent);
                    }

                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public void toggleSide()
    {
        setSide(currentSide == STATE_BODY_FRONT ? STATE_BODY_BACK : STATE_BODY_FRONT);
    }

    private void showHeadSelectionDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.dialog_head_zone, null);

        Dialog dialog = new AlertDialog.Builder(getContext(), R.style.Dialog_Body).show();

        // Dismiss on outside click
        layout.setOnClickListener(v -> dialog.dismiss());

        // Init views
        int front = ContextCompat.getColor(getContext(), R.color.mm_color_body_map_front);
        int back = ContextCompat.getColor(getContext(), R.color.mm_color_body_map_back);

        ImageView faceFront = (ImageView) layout.findViewById(R.id.face_front);
        TextView faceFrontLabel = (TextView) layout.findViewById(R.id.face_front_label);
        initZoneImageView(dialog, faceFront, faceFrontLabel);

        ImageView faceLeft = (ImageView) layout.findViewById(R.id.face_left);
        TextView faceLeftLabel = (TextView) layout.findViewById(R.id.face_left_label);
        initZoneImageView(dialog, faceLeft, faceLeftLabel);

        ImageView faceRight = (ImageView) layout.findViewById(R.id.face_right);
        TextView faceRightLabel = (TextView) layout.findViewById(R.id.face_right_label);
        initZoneImageView(dialog, faceRight, faceRightLabel);

        ImageView headTop = (ImageView) layout.findViewById(R.id.head_top);
        TextView headTopLabel = (TextView) layout.findViewById(R.id.head_top_label);
        initZoneImageView(dialog, headTop, headTopLabel);

        ImageView headBack = (ImageView) layout.findViewById(R.id.head_back);
        TextView headBackLabel = (TextView) layout.findViewById(R.id.head_back_label);
        initZoneImageView(dialog, headBack, headBackLabel);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setContentView(layout);
        dialog.getWindow().setAttributes(params);
    }

    private void initZoneImageView(Dialog dialog, ImageView imageView, TextView label)
    {
        int zone = Integer.parseInt((String) imageView.getTag());
        int moleCount = moleCounts.get(zone);
        boolean hasMoles = moleCount > 0;

        if(hasMoles)
        {
            label.setVisibility(View.VISIBLE);
            label.setText(Integer.toString(moleCount));
        }

        imageView.setActivated(hasMoles);
        imageView.setOnClickListener(v -> {
            Intent intent = BodyZoneActivity.newIntent(getContext(), zone);
            getContext().startActivity(intent);
            dialog.dismiss();
        });

    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        BodySavedState ss = new BodySavedState(superState);
        ss.state = currentSide;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if(! (state instanceof BodySavedState))
        {
            super.onRestoreInstanceState(state);
            return;
        }

        BodySavedState ss = (BodySavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        currentSide = ss.state;
    }

    public void setSide(int side)
    {
        currentSide = side;
        invalidateBodyDrawable();
        initPathsAndRegions();
        postInvalidate();
    }

    public int getCurrentSide()
    {
        return currentSide;
    }

    private void invalidateBodyDrawable()
    {
        boolean isFront = currentSide == STATE_BODY_FRONT;
        int color = ContextCompat.getColor(getContext(),
                isFront ? R.color.mm_color_body_map_front : R.color.mm_color_body_map_back);
        filledPaint.setColor(color);
        Drawable drawable = ContextCompat.getDrawable(getContext(),
                isFront ? R.drawable.body_front : R.drawable.body_back);
        setImageDrawable(drawable);
    }

    public void setMoleCount(List<Zone> zones)
    {
        moleCounts.clear();

        for(Zone zone : zones)
        {
            moleCounts.put(zone.id, zone.moles == null ? 0 : zone.moles.size());
        }

        int headCount = moleCounts.get(BodyZoneHelper.FACE_LEFT_SIDE) +
                moleCounts.get(BodyZoneHelper.FACE_RIGHT_SIDE) +
                moleCounts.get(BodyZoneHelper.HEAD_TOP) +
                moleCounts.get(BodyZoneHelper.FACE_FRONT) +
                moleCounts.get(BodyZoneHelper.HEAD_BACK);

        if(headCount > 0)
        {
            moleCounts.put(BodyZoneHelper.BACK_HEAD, headCount);
            moleCounts.put(BodyZoneHelper.FRONT_HEAD, headCount);
        }

        postInvalidate();
    }

    public void setMoleCount(int zoneId, int moleCount)
    {
        moleCounts.put(zoneId, moleCount);
        postInvalidate();
    }

    public void clearMoleCount()
    {
        moleCounts.clear();
        postInvalidate();
    }

    private static class BodySavedState extends BaseSavedState
    {
        int state;

        BodySavedState(Parcelable superState)
        {
            super(superState);
        }

        private BodySavedState(Parcel in)
        {
            super(in);
            state = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Parcelable.Creator<BodySavedState> CREATOR = new Parcelable.Creator<BodySavedState>()
        {
            public BodySavedState createFromParcel(Parcel in)
            {
                return new BodySavedState(in);
            }

            public BodySavedState[] newArray(int size)
            {
                return new BodySavedState[size];
            }
        };
    }

}
