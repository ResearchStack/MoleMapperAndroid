package org.researchstack.molemapper.ui.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.researchstack.molemapper.BuildConfig;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.Measurement;

import java.text.NumberFormat;

public class MeasurementView extends SubsamplingScaleImageView
{

    private static final boolean DEBUG_VIEW = false;
    private static final int     MAX_SCALE  = 12;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Field Headers
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private NumberFormat moleDiameterFormat;

    private int textSize;
    private int textOffset;
    private int edgeStroke;
    private int moleEdgeColor;

    private Paint circlePaint;
    private Paint textPaint;

    private String moleLabel;
    private float  moleX;
    private float  moleY;
    private float  moleRadius;

    private float startScale;
    private float startCenterY;
    private float startCenterX;

    // Debug vars
    private float touchX, touchY;

    public MeasurementView(Context context)
    {
        this(context, null);
    }

    public MeasurementView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setHapticFeedbackEnabled(true);
        setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        setMaxScale(MAX_SCALE);

        moleDiameterFormat = NumberFormat.getInstance();
        moleDiameterFormat.setMinimumFractionDigits(2);
        moleDiameterFormat.setMaximumFractionDigits(2);

        textSize = getResources().getDimensionPixelSize(R.dimen.mm_history_text_size);
        textOffset = getResources().getDimensionPixelSize(R.dimen.mm_history_text_offset);
        edgeStroke = getResources().getDimensionPixelSize(R.dimen.mm_history_stroke_edge_size);
        moleEdgeColor = ContextCompat.getColor(getContext(), R.color.mm_color_history_mole_edge);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(getContext(),
                R.color.mm_color_measure_text_color));
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        PointF sourceDownXY = viewToSourceCoord(event.getX(), event.getY());
        touchX = sourceDownXY.x;
        touchY = sourceDownXY.y;

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(getScale() == 0 || getCenter() == null)
        {
            return;
        }

        if(startScale == 0)
        {
            startScale = getScale();
        }

        if(startCenterX == 0)
        {
            startCenterX = getCenter().x;
        }

        if(startCenterY == 0)
        {
            startCenterY = getCenter().y;
        }

        // Debug drawing block, this draws the location of the viewport, mole, and reference objects
        if(BuildConfig.DEBUG && DEBUG_VIEW)
        {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(5);
            paint.setAlpha(128);
            paint.setStyle(Paint.Style.FILL);

            // Draw the width and
            canvas.drawRect(0, 0, getSWidth(), getSHeight(), paint);

            paint.setColor(Color.CYAN);
            paint.setAlpha(255);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, 0, getSWidth(), getSHeight(), paint);

            paint.setColor(Color.BLUE);
            canvas.drawCircle(moleX, moleY, moleRadius, paint);

            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            PointF start = viewToSourceCoord(0, 0);
            PointF end = viewToSourceCoord(getWidth(), getHeight());
            canvas.drawRect(start.x, start.y, end.x, end.y, paint);
            canvas.drawCircle(getCenter().x, getCenter().y, 3, paint);

            if(touchX != 0 && touchY != 0)
            {
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(touchX, touchY, 3, paint);
            }
        }

        // Calculate scale value that the user has performed
        float scale = getScale() / startScale;

        // Calculate scaled edge stroke
        float scaledEdgeStroke = scale * edgeStroke;

        // Calculate scaled text offset
        float scaledTextOffset = scale * textOffset;

        // Calculate scaled text size
        float scaledTextSize = textSize * scale;
        textPaint.setTextSize(scaledTextSize);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Draw mole measurement circle
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        PointF moleCenter = sourceToViewCoord(moleX, moleY);

        // Draw mole edge, we adjust the moleRadius to adjust for scale along with drawing as an
        // OUTER stroke
        circlePaint.setStrokeWidth(scaledEdgeStroke);
        circlePaint.setColor(moleEdgeColor);
        float adjustedMoleEdgeRadius =
                (getScale() * moleRadius) + circlePaint.getStrokeWidth() / 2f;
        canvas.drawCircle(moleCenter.x, moleCenter.y, adjustedMoleEdgeRadius, circlePaint);

        // Draw Mole Label
        float adjustedMoleTextY = moleCenter.y - (getScale() * moleRadius) - scaledTextOffset;
        canvas.drawText(moleLabel, moleCenter.x, adjustedMoleTextY, textPaint);

    }

    public void setMeasurement(Measurement measurement)
    {
        moleX = measurement.measurementX;
        moleY = measurement.measurementY;
        moleRadius = measurement.measurementDiameter / 2f;
        moleLabel = moleDiameterFormat.format(measurement.absoluteMoleDiameter) + "mm";
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        MeasurementSavedState ss = new MeasurementSavedState(superState);

        ImageViewState imageViewState = getState();

        ss.viewCenterX = imageViewState.getCenter().x;
        ss.viewCenterY = imageViewState.getCenter().y;
        ss.viewScale = imageViewState.getScale();

        ss.moleLabel = this.moleLabel;
        ss.moleX = this.moleX;
        ss.moleY = this.moleY;
        ss.moleRadius = this.moleRadius;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if(! (state instanceof MeasurementSavedState))
        {
            super.onRestoreInstanceState(state);
            return;
        }

        MeasurementSavedState ss = (MeasurementSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.moleLabel = ss.moleLabel;
        this.moleX = ss.moleX;
        this.moleY = ss.moleY;
        this.moleRadius = ss.moleRadius;

        setScaleAndCenter(ss.viewScale, new PointF(ss.viewCenterX, ss.viewCenterY));
    }

    private static class MeasurementSavedState extends BaseSavedState
    {

        private float viewCenterX;
        private float viewCenterY;
        private float viewScale;

        private String moleLabel;
        private float  moleX;
        private float  moleY;
        private float  moleRadius;


        //required field that makes Parcelables from a Parcel
        public static final Creator<MeasurementSavedState> CREATOR = new Creator<MeasurementView.MeasurementSavedState>()
        {
            public MeasurementView.MeasurementSavedState createFromParcel(Parcel in)
            {
                return new MeasurementView.MeasurementSavedState(in);
            }

            public MeasurementView.MeasurementSavedState[] newArray(int size)
            {
                return new MeasurementView.MeasurementSavedState[size];
            }
        };

        MeasurementSavedState(Parcelable superState)
        {
            super(superState);
        }

        private MeasurementSavedState(Parcel in)
        {
            super(in);

            viewCenterX = in.readFloat();
            viewCenterY = in.readFloat();
            viewScale = in.readFloat();

            moleLabel = in.readString();
            moleX = in.readFloat();
            moleY = in.readFloat();
            moleRadius = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);

            out.writeFloat(viewCenterX);
            out.writeFloat(viewCenterY);
            out.writeFloat(viewScale);

            out.writeString(moleLabel);
            out.writeFloat(moleX);
            out.writeFloat(moleY);
            out.writeFloat(moleRadius);
        }
    }
}
