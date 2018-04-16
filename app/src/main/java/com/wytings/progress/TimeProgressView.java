package com.wytings.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rex.wei on 2018/04/13 19:56.
 *
 * @author rex.wei@yff.com
 */
public class TimeProgressView extends View {

    private static final int POSITION_LEFT = 1;
    private static final int POSITION_MIDDLE = 2;
    private static final int POSITION_RIGHT = 3;

    final TextPaint paint;
    final float singleWidth;
    final RectF canvasRectF = new RectF();
    final RectF tempRectF = new RectF();
    final List<TimeData> dataList = new ArrayList<>();
    final GraphTextHelper textHelper;
    int descTextSize, timeTextSize, dotTopPadding, dotBottomPadding, dotHorizontalPadding, dotRadius, lineHeight;

    public TimeProgressView(Context context) {
        this(context, null);
    }

    public TimeProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        descTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, context.getResources().getDisplayMetrics());
        timeTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics());
        dotTopPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
        dotBottomPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        dotHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());
        dotRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());
        lineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        dataList.addAll(getTestData());

        textHelper = new GraphTextHelper(context);
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(timeTextSize);
        singleWidth = paint.measureText("yyyy-MM-dd");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int specMode = MeasureSpec.getMode(heightMeasureSpec);
        final int height;
        if (specMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = getPaddingTop() + descTextSize + dotTopPadding + dotRadius * 2 + dotBottomPadding + timeTextSize + getPaddingBottom();
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        final int systemDefaultMiniHeight = super.getSuggestedMinimumHeight();
        final int userDefaultMiniHeight = descTextSize + dotTopPadding + dotRadius * 2 + dotBottomPadding + timeTextSize;
        return Math.max(systemDefaultMiniHeight, userDefaultMiniHeight);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        final int systemDefaultMiniWidth = super.getSuggestedMinimumWidth();
        final int userDefaultMiniWidth = (int) (singleWidth * dataList.size() + 0.5f);
        return Math.max(systemDefaultMiniWidth, userDefaultMiniWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int leftPadding = getPaddingLeft();
        final int rightPadding = getPaddingRight();
        final int topPadding = getPaddingTop();
        final int bottomPadding = getPaddingBottom();
        canvasRectF.set(leftPadding, topPadding, w - rightPadding, h - bottomPadding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataList.isEmpty()) {
            return;
        }
        drawTimeTextBlock(canvas);
        drawTimeLine(canvas);
    }

    private void drawTimeLine(Canvas canvas) {
        if (dataList.size() < 2) {
            return;
        }

        final int centerY = lineCenterY();
        final int halfLineHeight = lineHeight / 2;
        for (int i = 0; i < dataList.size() - 1; i++) {
            TimeData data = dataList.get(i);
            TimeData next = dataList.get(i + 1);

            tempRectF.left = data.dotCenterX + dotRadius + dotHorizontalPadding;
            tempRectF.top = centerY - halfLineHeight;
            tempRectF.right = next.dotCenterX - dotRadius - dotHorizontalPadding;
            tempRectF.bottom = centerY + halfLineHeight;

            long currentTime = System.currentTimeMillis();
            if (currentTime < data.timestamp) {
                paint.setColor(Color.GRAY);
                canvas.drawRect(tempRectF, paint);
            } else if (data.timestamp <= currentTime && currentTime < next.timestamp) {
                paint.setColor(Color.GRAY);
                canvas.drawRect(tempRectF, paint);

                float percent = 1.0f * (System.currentTimeMillis() - data.timestamp) / (next.timestamp - data.timestamp);
                tempRectF.right = (int) (tempRectF.left + tempRectF.width() * percent);

                paint.setColor(Color.BLUE);
                canvas.drawRect(tempRectF, paint);
            } else {
                paint.setColor(Color.BLUE);
                canvas.drawRect(tempRectF, paint);
            }

        }

    }

    private void drawTimeTextBlock(Canvas canvas) {
        float width = canvasRectF.width();
        int size = dataList.size();

        if (size == 1) {
            drawSingleTimeBlock(dataList.get(0), canvasRectF.centerX(), POSITION_MIDDLE, canvas);
            return;
        }

        float interval = (width - singleWidth * size) / (size - 1);
        for (int i = 0; i < size; i++) {
            final TimeData data = dataList.get(i);
            final int positionType;
            final float anchorX;
            if (i == 0) {
                positionType = POSITION_LEFT;
                anchorX = canvasRectF.left;
            } else if (i == size - 1) {
                positionType = POSITION_RIGHT;
                anchorX = canvasRectF.right;
            } else {
                positionType = POSITION_MIDDLE;
                anchorX = canvasRectF.left + (singleWidth + interval) * i + singleWidth / 2;
            }
            drawSingleTimeBlock(data, anchorX, positionType, canvas);
        }
    }

    private void drawSingleTimeBlock(TimeData data, float anchorX, int positionType, Canvas canvas) {
        float dotCenterX = anchorX;
        switch (positionType) {
            case POSITION_LEFT:
                dotCenterX += dotRadius;
                textHelper.drawText(canvas, data.timeString, timeTextSize, Color.WHITE, anchorX, canvasRectF.bottom, TextAxisType.LEFT_BOTTOM);
                textHelper.drawText(canvas, data.desc, descTextSize, Color.WHITE, anchorX, canvasRectF.top, TextAxisType.LEFT_TOP);
                break;

            case POSITION_RIGHT:
                dotCenterX -= dotRadius;
                textHelper.drawText(canvas, data.desc, descTextSize, Color.WHITE, anchorX, canvasRectF.top, TextAxisType.RIGHT_TOP);
                textHelper.drawText(canvas, data.timeString, timeTextSize, Color.WHITE, anchorX, canvasRectF.bottom, TextAxisType.RIGHT_BOTTOM);
                break;

            case POSITION_MIDDLE:
                textHelper.drawText(canvas, data.desc, descTextSize, Color.WHITE, anchorX, canvasRectF.top, TextAxisType.CENTER_TOP);
                textHelper.drawText(canvas, data.timeString, timeTextSize, Color.WHITE, anchorX, canvasRectF.bottom, TextAxisType.CENTER_BOTTOM);
                break;
        }

        paint.setColor(System.currentTimeMillis() >= data.timestamp ? Color.BLUE : Color.GRAY);
        canvas.drawCircle(dotCenterX, lineCenterY(), dotRadius, paint);
        data.dotCenterX = dotCenterX;
    }

    private int lineCenterY() {
        return (int) (canvasRectF.top + descTextSize + dotTopPadding + dotRadius);
    }

    public Collection<? extends TimeData> getTestData() {
        List<TimeData> list = new LinkedList<>();
        list.add(new TimeData(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000L, "醒来"));
        list.add(new TimeData(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000L, "下床"));
        list.add(new TimeData(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L, "洗漱"));
        list.add(new TimeData(System.currentTimeMillis() + 40 * 24 * 60 * 60 * 1000L, "出门"));
        return list;
    }

    public static class TimeData {
        final long timestamp;
        final String timeString;
        final String desc;
        float dotCenterX;

        TimeData(long timestamp, String desc) {
            this.timestamp = timestamp;
            this.desc = desc;
            if (timestamp <= 0) {
                this.timeString = "--";
            } else {
                this.timeString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(timestamp));
            }
        }
    }

}
