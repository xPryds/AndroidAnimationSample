package br.com.buildoneapps.animationsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ClockView extends View {


    private int height = 0;
    private int fontSize = 0;
    private int padding;
    private int width = 0;
    private int numeralSpacing = 0;
    private boolean isInit;
    private Paint paint;
    private int hourHandTruncation = 0;
    private int handTruncation = 0;
    private int radius = 0;
    private Rect rect = new Rect();
    private float strokeWidth = 2;
    private int clockColor = R.color.colorPrimary;

    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initClock();
        }
        drawNumeral(canvas);
        postInvalidateDelayed(500);
        invalidate();
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics());
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        handTruncation = min / 20;
        hourHandTruncation = min / 7;
        paint = new Paint();
        isInit = true;
        CornerPathEffect cornerPathEffect =
                new CornerPathEffect(5);
        paint.setPathEffect(cornerPathEffect);
    }

    private void drawNumeral(Canvas canvas) {
        paint.setTextSize(fontSize);
        // Clock radius
        double r = Math.min(getWidth(), getHeight()) / 2 - padding;
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(ContextCompat.getColor(getContext(), clockColor));
        paint.setAntiAlias(true);
        int tickLen = 00;  // short tick
        int medTickLen = 20;  // at 5-minute intervals
        int longTickLen = 20; // at the quarters
        for (int i = 1; i <= 60; i++) {
            // default tick length is short
            int len = tickLen;
            if (i % 15 == 0) {
                // Longest tick on quarters (every 15 ticks)
                len = longTickLen;
            } else if (i % 5 == 0) {
                // Medium ticks on the '5's (every 5 ticks)
                len = medTickLen;
            }
            double di = (double) i; // tick num as double for easier math

            // Get the angle from 12 O'Clock to this tick (radians)
            double angleFrom12 = di / 60.0 * 2.0 * Math.PI;

            // Get the angle from 3 O'Clock to this tick
            // Note: 3 O'Clock corresponds with zero angle in unit circle
            // Makes it easier to do the math.
            double angleFrom3 = Math.PI / 2.0 - angleFrom12;
            int cX = width / 2;
            int cY = height / 2;
            float x = (float) (cX + Math.cos(angleFrom3) * r);
            float y = (float) (cY - Math.sin(angleFrom3) * r);
            float xStop = (float) (cX + Math.cos(angleFrom3) * (r - len));
            float yStop = (float) (cY - Math.sin(angleFrom3) * (r - len));
            canvas.drawLine(x, y, xStop, yStop, paint);
        }
    }


    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setClockColor(int clockColor) {
        this.clockColor = clockColor;
    }
}
