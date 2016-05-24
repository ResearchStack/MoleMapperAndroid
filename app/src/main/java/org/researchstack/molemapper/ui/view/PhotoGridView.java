package org.researchstack.molemapper.ui.view;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class PhotoGridView extends View
{
    private Paint paint;
    private Paint cutoutPaint;


    public PhotoGridView(Context context)
    {
        super(context);
        init();
    }

    public PhotoGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PhotoGridView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public PhotoGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    int lineSize;

    private void init()
    {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        lineSize = (int) (getResources().getDisplayMetrics().density * 2 + .5f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(lineSize);
        paint.setAlpha(102);

        cutoutPaint = new Paint();
        cutoutPaint.setAntiAlias(true);
        cutoutPaint.setColor(Color.TRANSPARENT);
        cutoutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // Draw transparent black
        canvas.drawColor(0x66000000);

        // Draw cut-out
        canvas.drawRect(getPaddingLeft(),
                getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom(),
                cutoutPaint);

        // Set guide style
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x66FFFFFF);

        // Draw vertical guides
        canvas.drawRect(getPaddingLeft(), 0, getPaddingLeft() + lineSize, getHeight(), paint);
        canvas.drawRect(getWidth() - getPaddingRight() - lineSize,
                0,
                getWidth() - getPaddingRight(),
                getHeight(),
                paint);

        // Draw horizontal guides
        canvas.drawRect(0, getPaddingTop(), getWidth(), getPaddingTop() + lineSize, paint);
        canvas.drawRect(0,
                getHeight() - getPaddingBottom() - lineSize,
                getWidth(),
                getHeight() - getPaddingBottom(),
                paint);

        // Set guide box style
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);

        // Draw guide box
        float strokeOffset = (int) (paint.getStrokeWidth() / 2f + .5f);
        canvas.drawRect(getPaddingLeft() + strokeOffset,
                getPaddingTop() + strokeOffset,
                getWidth() - getPaddingRight() - strokeOffset,
                getHeight() - getPaddingBottom() - strokeOffset,
                paint);

    }
}
