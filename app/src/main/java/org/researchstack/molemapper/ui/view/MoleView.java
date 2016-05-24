package org.researchstack.molemapper.ui.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;

import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.researchstack.molemapper.BuildConfig;
import org.researchstack.molemapper.R;

public class MoleView extends SubsamplingScaleImageView
{

    private static final boolean DEBUG_VIEW = false;
    private static final int     MAX_SCALE  = 12;

    public interface MeasurementFocusChangedListener
    {
        void onFocusChanged(int currentSelected);
    }

    private MeasurementFocusChangedListener focusListener;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Current selected constants
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final int REFERENCE = 0;
    public static final int MOLE      = 1;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Field Headers
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private int textSize;
    private int highlightStroke;
    private int edgeStroke;
    private int moleHighlightColor;
    private int moleEdgeColor;
    private int referenceHighlightColor;
    private int referenceEdgeColor;

    private int currentSelected = MOLE;
    private boolean intersectedMeasureObject;
    private float   touchOffsetX;
    private float   touchOffsetY;

    private Paint circlePaint;
    private Paint textPaint;

    private float referenceX;
    private float referenceY;
    private float referenceRadius;
    private String referenceLabel = "REFERENCE";
    private String moleLabel      = "MOLE";

    private float moleX;
    private float moleY;
    private float moleRadius;

    // Debug vars
    private float touchX, touchY;

    public MoleView(Context context)
    {
        this(context, null);
    }

    public MoleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setHapticFeedbackEnabled(true);
        setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        setMaxScale(MAX_SCALE);
        setOnImageEventListener(new ImageEventListener()
        {
            @Override
            public void onImageLoaded()
            {
                // default mole pos
                if(moleX == 0 || moleY == 0)
                {
                    PointF moleXY = viewToSourceCoord(getWidth() * .25f, getHeight() * .5f);
                    moleX = moleXY.x;
                    moleY = moleXY.y;
                }

                // default ref pos
                if(referenceX == 0 || referenceY == 0)
                {
                    PointF referenceXY = viewToSourceCoord(getWidth() * .75f, getHeight() * .5f);
                    referenceX = referenceXY.x;
                    referenceY = referenceXY.y;
                }
            }
        });

        textSize = getResources().getDimensionPixelSize(R.dimen.mm_measure_text_size);
        highlightStroke = getResources().getDimensionPixelSize(R.dimen.mm_measure_stroke_highlight_size);
        edgeStroke = getResources().getDimensionPixelSize(R.dimen.mm_measure_stroke_edge_size);
        referenceRadius = getResources().getDimensionPixelSize(R.dimen.mm_measure_initial_reference_radius);
        moleRadius = getResources().getDimensionPixelSize(R.dimen.mm_measure_initial_mole_radius);
        moleHighlightColor = ContextCompat.getColor(getContext(),
                R.color.mm_color_measure_mole_highlight);
        moleEdgeColor = ContextCompat.getColor(getContext(), R.color.mm_color_measure_mole_edge);
        referenceHighlightColor = ContextCompat.getColor(getContext(),
                R.color.mm_color_measure_reference_highlight);
        referenceEdgeColor = ContextCompat.getColor(getContext(),
                R.color.mm_color_measure_reference_edge);

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


        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            PointF sourceDownXY = viewToSourceCoord(event.getX(), event.getY());
            touchX = sourceDownXY.x;
            touchY = sourceDownXY.y;

            float scale = getScale() / startScale;
            float sourceHighlightStroke = highlightStroke / scale;

            // Check if touch event intersects mole
            touchOffsetX = sourceDownXY.x - moleX;
            touchOffsetY = sourceDownXY.y - moleY;

            float distanceFromMole = (float) Math.sqrt(
                    Math.pow(touchOffsetX, 2) + Math.pow(touchOffsetY, 2));

            if(distanceFromMole <= moleRadius + sourceHighlightStroke)
            {
                if(currentSelected != MOLE)
                {
                    focusListener.onFocusChanged(MOLE);
                }
                currentSelected = MOLE;
                intersectedMeasureObject = true;
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                return true;
            }

            // Check if touch event intersects reference
            touchOffsetX = sourceDownXY.x - referenceX;
            touchOffsetY = sourceDownXY.y - referenceY;

            float distanceFromRef = (float) Math.sqrt(
                    Math.pow(touchOffsetX, 2) + Math.pow(touchOffsetY, 2));

            if(distanceFromRef <= referenceRadius + sourceHighlightStroke)
            {
                if(currentSelected != REFERENCE)
                {
                    focusListener.onFocusChanged(REFERENCE);
                }
                currentSelected = REFERENCE;
                intersectedMeasureObject = true;
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                return true;
            }

            // Pass event to parent if no intersect
            return super.onTouchEvent(event);
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(intersectedMeasureObject)
            {
                PointF sourceDownXY = viewToSourceCoord(event.getX(), event.getY());
                touchX = sourceDownXY.x;
                touchY = sourceDownXY.y;

                if(currentSelected == MOLE)
                {
                    moleX = sourceDownXY.x - touchOffsetX;
                    moleX = moleX < 0 ? 0 : moleX > getSWidth() ? getSWidth() : moleX;

                    moleY = sourceDownXY.y - touchOffsetY;
                    moleY = moleY < 0 ? 0 : moleY > getSHeight() ? getSHeight() : moleY;

                    postInvalidate();
                    return true;
                }
                else if(currentSelected == REFERENCE)
                {
                    referenceX = sourceDownXY.x - touchOffsetX;
                    referenceX = referenceX < 0
                            ? 0
                            : referenceX > getSWidth() ? getSWidth() : referenceX;

                    referenceY = sourceDownXY.y - touchOffsetY;
                    referenceY = referenceY < 0
                            ? 0
                            : referenceY > getSHeight() ? getSHeight() : referenceY;

                    postInvalidate();
                    return true;
                }
            }

            // Pass event to parent if no intersect
            return super.onTouchEvent(event);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            touchX = 0;
            touchY = 0;
            touchOffsetX = 0;
            touchOffsetY = 0;
            intersectedMeasureObject = false;
        }

        return super.onTouchEvent(event);
    }

    private float startScale;
    private float startCenterY;
    private float startCenterX;

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

            paint.setColor(Color.GREEN);
            paint.setAlpha(127);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(referenceX, referenceY, referenceRadius, paint);

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

        // Calculate scaled highlight stroke
        float scaledHighlightStroke = scale * highlightStroke;

        // Calculate scaled text size
        float scaledTextSize = textSize * scale;
        textPaint.setTextSize(scaledTextSize);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Draw Reference measurement circle
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        PointF referenceCenter = sourceToViewCoord(referenceX, referenceY);

        // Draw reference highlight circle, we adjust the referenceRadius to adjust for scale along with drawing as an
        // OUTER stroke
        circlePaint.setStrokeWidth(scaledHighlightStroke);
        circlePaint.setColor(referenceHighlightColor);
        float adjustedReferenceHighlightRadius =
                (getScale() * referenceRadius) + circlePaint.getStrokeWidth() / 2f;
        canvas.drawCircle(referenceCenter.x,
                referenceCenter.y,
                adjustedReferenceHighlightRadius,
                circlePaint);

        // Draw reference edge, we adjust the referenceRadius to adjust for scale along with drawing as an
        // OUTER stroke
        circlePaint.setStrokeWidth(scaledEdgeStroke);
        circlePaint.setColor(referenceEdgeColor);
        float adjustedReferenceEdgeRadius =
                (getScale() * referenceRadius) + circlePaint.getStrokeWidth() / 2f;
        canvas.drawCircle(referenceCenter.x,
                referenceCenter.y,
                adjustedReferenceEdgeRadius,
                circlePaint);

        // Draw reference Label
        float adjustedReferenceTextY =
                referenceCenter.y + (getScale() * referenceRadius) + scaledHighlightStroke -
                        ((textPaint.descent() + textPaint.ascent()) / 2.5f);
        canvas.drawText(referenceLabel, referenceCenter.x, adjustedReferenceTextY, textPaint);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Draw mole measurement circle
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        PointF moleCenter = sourceToViewCoord(moleX, moleY);

        // Draw mole highlight circle, we adjust the moleRadius to adjust for scale along with drawing as an
        // OUTER stroke
        circlePaint.setStrokeWidth(scaledHighlightStroke);
        circlePaint.setColor(moleHighlightColor);
        float adjustedMoleHighlightRadius =
                (getScale() * moleRadius) + circlePaint.getStrokeWidth() / 2f;
        canvas.drawCircle(moleCenter.x, moleCenter.y, adjustedMoleHighlightRadius, circlePaint);

        // Draw mole edge, we adjust the moleRadius to adjust for scale along with drawing as an
        // OUTER stroke
        circlePaint.setStrokeWidth(scaledEdgeStroke);
        circlePaint.setColor(moleEdgeColor);
        float adjustedMoleEdgeRadius =
                (getScale() * moleRadius) + circlePaint.getStrokeWidth() / 2f;
        canvas.drawCircle(moleCenter.x, moleCenter.y, adjustedMoleEdgeRadius, circlePaint);

        // Draw Mole Label
        float adjustedMoleTextY = moleCenter.y + (getScale() * moleRadius) + scaledHighlightStroke -
                ((textPaint.descent() + textPaint.ascent()) / 2.5f);
        canvas.drawText(moleLabel, moleCenter.x, adjustedMoleTextY, textPaint);

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

        ss.currentSelected = this.currentSelected;

        ss.referenceLabel = this.referenceLabel;
        ss.referenceX = this.referenceX;
        ss.referenceY = this.referenceY;
        ss.referenceRadius = this.referenceRadius;

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

        this.currentSelected = ss.currentSelected;

        this.referenceLabel = ss.referenceLabel;
        this.referenceX = ss.referenceX;
        this.referenceY = ss.referenceY;
        this.referenceRadius = ss.referenceRadius;

        this.moleLabel = ss.moleLabel;
        this.moleX = ss.moleX;
        this.moleY = ss.moleY;
        this.moleRadius = ss.moleRadius;

        setScaleAndCenter(ss.viewScale, new PointF(ss.viewCenterX, ss.viewCenterY));
    }

    public int getCurrentSelected()
    {
        return currentSelected;
    }

    public void setCurrentSelected(int selected)
    {
        currentSelected = selected;
        postInvalidate();
    }

    public float getRadius(int type)
    {
        return type == MOLE ? moleRadius : referenceRadius;
    }

    public void setRadius(float byAmount)
    {
        setRadius(currentSelected, byAmount);
    }

    /**
     * @param type  the object you want to change radius of
     * @param value the amount, in mm, you want the radius to change
     */
    public void setRadius(int type, float value)
    {
        if(value == 0)
        {
            return;
        }

        if(type == MOLE)
        {
            moleRadius = value / getScale();
        }
        else
        {
            referenceRadius = value / getScale();
        }

        postInvalidate();
    }

    public void changeRadius(float byAmount)
    {
        changeRadius(currentSelected, byAmount);
    }

    public void changeRadius(int type, float byAmount)
    {
        if(type == MOLE)
        {
            if(moleRadius + byAmount < 0)
            {
                return;
            }

            moleRadius += byAmount / getScale();
        }
        else
        {
            if(referenceRadius + byAmount < 0)
            {
                return;
            }

            referenceRadius += byAmount / getScale();
        }

        postInvalidate();
    }

    public void changeXPosition(float byAmount)
    {
        changeXPosition(currentSelected, byAmount);
    }

    public void changeXPosition(int type, float byAmount)
    {
        if(type == MOLE)
        {
            if(moleX + byAmount < 0)
            {
                return;
            }

            moleX += byAmount / getScale();
        }
        else
        {
            if(referenceX + byAmount < 0)
            {
                return;
            }

            referenceX += byAmount / getScale();
        }

        postInvalidate();
    }

    public void changeYPosition(float byAmount)
    {
        changeYPosition(currentSelected, byAmount);
    }

    public void changeYPosition(int type, float byAmount)
    {
        if(type == MOLE)
        {
            if(moleY + byAmount < 0)
            {
                return;
            }

            moleY += byAmount / getScale();
        }
        else
        {
            if(referenceY + byAmount < 0)
            {
                return;
            }

            referenceY += byAmount / getScale();
        }

        postInvalidate();
    }

    public void setMeasurementFocusListener(MeasurementFocusChangedListener focusListener)
    {
        this.focusListener = focusListener;
    }


    /**
     * @return Reference diameter in DP
     */
    public float getReferenceDiameter()
    {
        return referenceRadius * 2;
    }

    /**
     * @return Reference x-axis position in DP
     */
    public float getReferenceX()
    {
        return referenceX;
    }

    /**
     * @return Reference y-axis position in DP
     */
    public float getReferenceY()
    {
        return referenceY;
    }

    /**
     * @return Mole diameter in DP
     */
    public float getMeasurementDiameter()
    {
        return moleRadius * 2;
    }

    /**
     * @return Mole x-axis position in DP
     */
    public float getMeasurementX()
    {
        return moleX;
    }

    /**
     * @return Mole y-axis position in DP
     */
    public float getMeasurementY()
    {
        return moleY;
    }

    public void setReferenceLabel(String referenceLabel)
    {
        this.referenceLabel = referenceLabel.toUpperCase();
        postInvalidate();
    }

    public void setMoleLabel(String moleLabel)
    {
        this.moleLabel = moleLabel;
        postInvalidate();
    }

    public static class ImageEventListener implements OnImageEventListener
    {

        @Override
        public void onReady()
        {

        }

        @Override
        public void onImageLoaded()
        {

        }

        @Override
        public void onPreviewLoadError(Exception e)
        {

        }

        @Override
        public void onImageLoadError(Exception e)
        {

        }

        @Override
        public void onTileLoadError(Exception e)
        {

        }
    }

    private static class MeasurementSavedState extends BaseSavedState
    {

        private float viewCenterX;
        private float viewCenterY;
        private float viewScale;

        private int currentSelected;

        private String referenceLabel;
        private float  referenceX;
        private float  referenceY;
        private float  referenceRadius;

        private String moleLabel;
        private float  moleX;
        private float  moleY;
        private float  moleRadius;


        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<MeasurementSavedState> CREATOR = new Parcelable.Creator<MeasurementSavedState>()
        {
            public MeasurementSavedState createFromParcel(Parcel in)
            {
                return new MeasurementSavedState(in);
            }

            public MeasurementSavedState[] newArray(int size)
            {
                return new MeasurementSavedState[size];
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

            currentSelected = in.readInt();

            referenceLabel = in.readString();
            referenceX = in.readFloat();
            referenceY = in.readFloat();
            referenceRadius = in.readFloat();

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

            out.writeInt(currentSelected);

            out.writeString(referenceLabel);
            out.writeFloat(referenceX);
            out.writeFloat(referenceY);
            out.writeFloat(referenceRadius);

            out.writeString(moleLabel);
            out.writeFloat(moleX);
            out.writeFloat(moleY);
            out.writeFloat(moleRadius);
        }
    }
}
