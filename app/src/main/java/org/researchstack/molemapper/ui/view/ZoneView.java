package org.researchstack.molemapper.ui.view;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.widget.ImageView;

import org.researchstack.backbone.utils.LogExt;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.Mole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZoneView extends ImageView
{
    public static final int STATE_MOLE_CREATE = 0;
    public static final int STATE_MOLE_SELECT = 1;

    private int currentState = STATE_MOLE_SELECT;

    private MoleClickListener listener;

    private Paint untrackedPaint;
    private Paint trackedPaint;
    private Paint outdatedPaint;
    private Paint removedPaint;

    private List<Mole> moles;
    private int        radius;
    private int        touchPadding;

    private GestureDetector gestureDetector;
    private Mole            touchEventMole;
    private boolean         isTracking;

    public ZoneView(Context context)
    {
        super(context);
        init();
    }

    public ZoneView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ZoneView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ZoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        setHapticFeedbackEnabled(true);

        float density = getResources().getDisplayMetrics().density;

        moles = new ArrayList<>();

        gestureDetector = new GestureDetector(getContext(), new MoleOnGestureListener());

        radius = (int) (density * 16 + .5f);
        touchPadding = (int) (density * 32 + .5f);
        int dashGap = (int) (density * 5 + .5f);
        int dashLength = (int) (density * 5 + .5f);
        int circleStroke = (int) (density * 2 + .5f);

        untrackedPaint = new Paint();
        untrackedPaint.setColor(Color.WHITE);
        untrackedPaint.setAntiAlias(true);
        untrackedPaint.setPathEffect(new DashPathEffect(new float[] {dashGap, dashLength}, 0));
        untrackedPaint.setStrokeWidth(circleStroke);
        untrackedPaint.setStyle(Paint.Style.STROKE);

        trackedPaint = new Paint();
        trackedPaint.setColor(Color.WHITE);
        trackedPaint.setAntiAlias(true);
        trackedPaint.setStrokeWidth(circleStroke);
        trackedPaint.setStyle(Paint.Style.STROKE);

        removedPaint = new Paint();
        removedPaint.setColor(ContextCompat.getColor(getContext(),
                R.color.text_color_mole_inactive));
        removedPaint.setAntiAlias(true);
        removedPaint.setStrokeWidth(circleStroke);
        removedPaint.setStyle(Paint.Style.STROKE);

        outdatedPaint = new Paint();
        outdatedPaint.setColor(ContextCompat.getColor(getContext(), R.color.mm_color_mole_outdated));
        outdatedPaint.setAntiAlias(true);
        outdatedPaint.setStrokeWidth(circleStroke);
        outdatedPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(isTracking)
        {
            touchEventMole.moleX = (int) event.getX();
            touchEventMole.moleY = (int) event.getY();

            // We are no longer tracking motion event
            if(event.getAction() != MotionEvent.ACTION_MOVE)
            {
                isTracking = false;

                // If the ID is null then the mole has just been created. Ignore calling
                // onMoleLongClick as we do not need to save it just yet. The user needs to confirm
                // creation first by selecting "Next" in the bottom snack bar.
                if(touchEventMole.id != null)
                {
                    listener.onMoleLongClick(touchEventMole);
                }
            }

            postInvalidate();

            return true;
        }
        else
        {
            return gestureDetector.onTouchEvent(event);
        }
    }


    private boolean intersects(Mole mole, float x1, float y1)
    {
        float dist = (float) Math.sqrt(Math.pow(x1 - mole.moleX, 2) + Math.pow(y1 - mole.moleY, 2));

        return (int) dist <= radius + touchPadding;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // Draw moles
        for(Mole mole : moles)
        {
            int moleState = mole.getState();

            if(moleState == Mole.STATE_UNTRACKED)
            {
                canvas.drawCircle(mole.moleX, mole.moleY, radius, untrackedPaint);
            }
            else if(moleState == Mole.STATE_TRACKED)
            {
                canvas.drawCircle(mole.moleX, mole.moleY, radius, trackedPaint);
            }
            else if(moleState == Mole.STATE_REMOVED)
            {
                canvas.drawCircle(mole.moleX, mole.moleY, radius, removedPaint);
            }
            else if(moleState == Mole.STATE_TRACKED_OUTDATED)
            {
                outdatedPaint.setStyle(Paint.Style.FILL);
                outdatedPaint.setAlpha(77); // 30% alpha
                canvas.drawCircle(mole.moleX, mole.moleY, radius, outdatedPaint);

                outdatedPaint.setStyle(Paint.Style.STROKE);
                outdatedPaint.setAlpha(255); // 1000% alpha
                canvas.drawCircle(mole.moleX, mole.moleY, radius, outdatedPaint);
            }
        }
    }

    public void setCurrentState(int state)
    {
        if(currentState == state)
        {
            return;
        }

        if(currentState == STATE_MOLE_CREATE && state == STATE_MOLE_SELECT)
        {
            Iterator<Mole> iterator = moles.iterator();
            while(iterator.hasNext())
            {
                if(iterator.next().id == null)
                {
                    iterator.remove();
                }
            }
        }

        currentState = state;
        postInvalidate();
    }

    public List<Mole> getCreated()
    {
        List<Mole> created = new ArrayList<>();
        for(Mole mole : moles)
        {
            if(mole.id == null)
            {
                created.add(mole);
            }
        }
        return created;
    }

    public void setMoles(List<Mole> moles)
    {
        this.moles = moles == null ? new ArrayList<>() : new ArrayList<>(moles);

        postInvalidate();
    }

    public void addMole(Mole mole)
    {
        this.moles.add(mole);
        postInvalidate();
    }

    public void setOnMoleClickListener(MoleClickListener listener)
    {
        this.listener = listener;
    }

    public interface MoleClickListener
    {
        void onMoleClick(Mole mole);

        void onMoleLongClick(Mole mole);
    }

    private class MoleOnGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            touchEventMole = null;

            for(Mole mole : moles)
            {
                if(intersects(mole, e.getX(), e.getY()))
                {
                    touchEventMole = mole;
                }
            }

            LogExt.e(MoleOnGestureListener.class, "intersect = " + (touchEventMole != null));

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            if(touchEventMole != null)
            {
                LogExt.e(MoleOnGestureListener.class, "onLongPress " + touchEventMole.id);
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                isTracking = true;
                return;
            }

            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if(currentState == STATE_MOLE_CREATE)
            {
                Mole emptyMole = new Mole();
                emptyMole.moleX = (int) e.getX();
                emptyMole.moleY = (int) e.getY();
                moles.add(emptyMole);

                postInvalidate();
            }
            else
            {
                if(touchEventMole != null)
                {
                    LogExt.e(MoleOnGestureListener.class, "onMoleClick " + touchEventMole.id);
                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    listener.onMoleClick(touchEventMole);
                    touchEventMole = null;
                    return true;
                }
            }

            return super.onSingleTapConfirmed(e);
        }
    }
}
