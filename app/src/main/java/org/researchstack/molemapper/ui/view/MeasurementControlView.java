package org.researchstack.molemapper.ui.view;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;

public class MeasurementControlView extends AppCompatImageButton
{
    private Handler handler = new Handler();
    private GestureDetector gestureDetector;

    public MeasurementControlView(Context context)
    {
        super(context);
        init();
    }

    public MeasurementControlView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MeasurementControlView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setHapticFeedbackEnabled(true);

        gestureDetector = new GestureDetector(getContext(), new ControlGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            handler.removeCallbacks(longClickRunnable);
        }

        return gestureDetector.onTouchEvent(event);
    }

    private Runnable longClickRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            performLongClick();
            handler.postDelayed(this, 30);
        }
    };

    private class ControlGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            handler.post(longClickRunnable);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            callOnClick();
            return true;
        }
    }
}
