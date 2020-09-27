package ru.gravo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import ru.gravo.App;
import ru.gravo.R;
import ru.gravo.screens.history.models.HistoryDateModel;

/**
 *
 */

public class CalendarDateView extends android.support.v7.widget.AppCompatTextView {

    private HistoryDateModel model;
    private float noteLineWidth;

    //FIXME: make private and add setter|getter
    protected Paint paint;
    protected RectF rect;

    public CalendarDateView(Context context) {
        super(context);
        initialize();
    }

    public CalendarDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
        setupAttributes(context, attrs, 0);
    }

    public CalendarDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
        setupAttributes(context, attrs, defStyleAttr);
    }

    private void initialize() {
        paint = new Paint();
        paint.setAntiAlias(true);
        rect = new RectF();
        model = null;
        noteLineWidth = 1;
    }

    private void setupAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().
                obtainStyledAttributes(attrs, R.styleable.CalendarDateView, defStyleAttr, 0);

        try {
            setNoteLineWidth(a.getDimension(R.styleable.CalendarDateView_note_line_width, 1));
        } finally {
            a.recycle();
        }
    }


    public void setModel(HistoryDateModel model) {
        this.model = model;
        invalidate();
    }

    protected HistoryDateModel getModel() {
        return model;
    }

    public void setNoteLineWidth(float value) {
        noteLineWidth = value;
    }

    public float getNoteLineWidth() {
        return noteLineWidth;
    }

    private void setPaintNoteData(HistoryDateModel dateModel) {
        if (dateModel.isFromTable()) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        else {
            paint.setStyle(Paint.Style.STROKE);
        }
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeWidth(getNoteLineWidth());
        paint.setColor(dateModel.getNoteColor());
    }

    private void drawRectPart(Canvas canvas, RectF rect, boolean isFilled) {
        if (isFilled) {
            canvas.drawRect(rect, paint);
        }
        else {
            canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint.Style paintStyle = paint.getStyle();
        HistoryDateModel dateModel = getModel();
        if (dateModel != null) {
            float strokeWidthHalf = paint.getStrokeWidth() / 2.0f;

            setPaintNoteData(dateModel);
            switch (dateModel.getStatus()) {
                case HistoryDateModel.EMPTY_STATUS:
                    break;
                case HistoryDateModel.SINGLE_STATUS:
                    //FIXME: Square view only?
                    float radius = getHeight() / 2.0f - strokeWidthHalf;
                    canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, radius, paint);
                    break;
                case HistoryDateModel.START_RANGE_STATUS:
                    rect.set(getWidth() / 2.0f, strokeWidthHalf, getWidth(), getHeight() - strokeWidthHalf);
                    drawRectPart(canvas, rect, dateModel.isFromTable());
                    rect.set((getWidth() - getHeight()) / 2 + strokeWidthHalf, strokeWidthHalf, (getWidth() - getHeight()) / 2 + getHeight() - strokeWidthHalf, getHeight() - strokeWidthHalf);
                    canvas.drawArc(rect, 90, 180, false, paint);
                    break;
                case HistoryDateModel.MIDDLE_RANGE_STATUS:
                    rect.set(0, strokeWidthHalf, getWidth(), getHeight() - strokeWidthHalf);
                    drawRectPart(canvas, rect, dateModel.isFromTable());
                    break;
                case HistoryDateModel.END_RANGE_STATUS:
                    rect.set(0, strokeWidthHalf, getWidth() / 2.0f, getHeight() - strokeWidthHalf);
                    drawRectPart(canvas, rect, dateModel.isFromTable());
                    rect.set((getWidth() - getHeight()) / 2 + strokeWidthHalf, strokeWidthHalf, (getWidth() - getHeight()) / 2 + getHeight() - strokeWidthHalf, getHeight() - strokeWidthHalf);
                    canvas.drawArc(rect, 270, 180, false, paint);
                    break;
            }

            //FIXME: temp code
            float markSize = 2;
            if (dateModel.isMarked()) {
                //rect.set(getWidth() / 2.0f - markSize, getHeight() / 4.0f - markSize, getWidth() / 2.0f + markSize, getHeight() / 4.0f + markSize);
                paint.setColor(dateModel.getMarkColor());
                canvas.drawCircle(getWidth() / 2.0f, (3.0f * getHeight() + paint.getTextSize()) / 4.0f, markSize, paint);
            }
            //else{ nop }

            setTextColor(dateModel.getTextColor());
        }
        //else{ nop }
        paint.setStyle(paintStyle);
        super.onDraw(canvas);
    }
}
