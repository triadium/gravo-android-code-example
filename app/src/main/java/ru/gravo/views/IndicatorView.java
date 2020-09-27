package ru.gravo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import ru.gravo.R;

/**
 *
 */

public class IndicatorView extends View {

    private boolean isActive = false;

    public void setActive(boolean active) {
        isActive = active;
        invalidate();
    }

    public IndicatorView(Context context) {
        super(context);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(ContextCompat.getColor(getContext(),
                isActive ? R.color.colorAccent : R.color.colorPrimaryDark));
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, p);
    }
}
