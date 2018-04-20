package com.wytings.progress.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.wytings.progress.R;
import com.wytings.progress.widget.helper.GraphTextHelper;
import com.wytings.progress.widget.helper.TextAxisType;

/**
 * Created by rex.wei on 2018/04/16 16:54.
 *
 * @author rex.wei@yff.com
 */
public class MarkSeekBar extends View {

    LinearGradient blueLinearGradient;
    final int seekBarHeight;
    final Bitmap seekBarThumb;
    final int maxHeight;
    final int textSize;
    final int textPadding;
    final int seekBarBackgroundColor;
    final TextPaint paint;
    final int maxNumber = 9;
    final RectF seekBarRectF = new RectF();
    final RectF tempRectF = new RectF();
    final float roundRectRadius;
    final float dotPadding;
    final float dotRadius;
    int currentNumber = 1;
    final float thumbRadius;
    final GraphTextHelper textHelper;
    String maxText = maxNumber + "倍";

    public MarkSeekBar(Context context) {
        this(context, null);
    }

    public MarkSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        textHelper = new GraphTextHelper(context);
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, context.getResources().getDisplayMetrics());
        seekBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        textPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
        roundRectRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        dotRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        dotPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, context.getResources().getDisplayMetrics());
        thumbRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        seekBarThumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_thumb);
        maxHeight = (int) (seekBarHeight + (thumbRadius - seekBarHeight / 2) + textPadding + textSize);
        seekBarBackgroundColor = Color.parseColor("#1E2732");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        blueLinearGradient = new LinearGradient(getPaddingLeft()
                , getPaddingTop()
                , w - getPaddingRight()
                , getPaddingTop()
                , Color.parseColor("#80CDF9")
                , Color.parseColor("#499EF0")
                , Shader.TileMode.CLAMP);
        seekBarRectF.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
        int extraDistance = (int) (thumbRadius - seekBarHeight / 2);
        seekBarRectF.top += extraDistance;
        seekBarRectF.bottom = seekBarRectF.top + seekBarHeight;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int specMode = MeasureSpec.getMode(heightMeasureSpec);
        final int height;
        if (specMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = getPaddingTop() + maxHeight + getPaddingBottom();
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSeekBar(canvas);
        drawText(canvas);

    }

    private void drawText(Canvas canvas) {
        textHelper.drawText(canvas, "0", textSize, Color.GRAY, seekBarRectF.left, seekBarRectF.top + textPadding, TextAxisType.LEFT_TOP);
        textHelper.drawText(canvas, maxText, textSize, Color.GRAY, seekBarRectF.right, seekBarRectF.top + textPadding, TextAxisType.RIGHT_TOP);
    }

    private void drawSeekBar(Canvas canvas) {
        paint.setShader(blueLinearGradient);
        canvas.drawRoundRect(seekBarRectF, roundRectRadius, roundRectRadius, paint);

        int count = maxNumber + 1;
        float interval = (seekBarRectF.width() - dotPadding * 2) / maxNumber;
        float centerY = seekBarRectF.centerY();

        float currentDotCenterX = seekBarRectF.left + dotPadding + currentNumber * interval;

        if (currentNumber < 9) {
            resetPaint(seekBarBackgroundColor);
            tempRectF.set(seekBarRectF);
            tempRectF.left = currentDotCenterX;
            canvas.drawRoundRect(tempRectF, roundRectRadius, roundRectRadius, paint);
        }

        resetPaint(Color.WHITE);
        for (int i = 0; i < count; i++) {
            canvas.drawCircle(seekBarRectF.left + dotPadding + i * interval, centerY, dotRadius, paint);
        }

        tempRectF.set(currentDotCenterX - thumbRadius, centerY - thumbRadius,
                currentDotCenterX + thumbRadius, centerY + thumbRadius);
        tempRectF.offset(0, 6);
        canvas.drawBitmap(seekBarThumb, null, tempRectF, paint);
    }

    private void resetPaint(int color) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(color);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return handleDownEvent(event);
            case MotionEvent.ACTION_MOVE:
                return handleMoveEvent(event);
            default:
                return super.onTouchEvent(event);
        }

    }

    private int getClickNumber(MotionEvent event) {
        float interval = (seekBarRectF.width() - dotPadding * 2) / maxNumber;
        return (int) ((event.getX() - seekBarRectF.left - dotPadding) / interval + 0.5f);
    }

    private boolean postTargetNumber(int number) {
        if (number < 0) {
            number = 0;
        } else if (number > maxNumber) {
            number = maxNumber;
        }

        if (number != currentNumber) {
            currentNumber = number;
            invalidate();
            return true;
        }

        return false;
    }

    private boolean handleDownEvent(MotionEvent event) {
        tempRectF.set(seekBarRectF);
        tempRectF.inset(-100, -100);
        if (tempRectF.contains(event.getX(), event.getY())) {
            postTargetNumber(getClickNumber(event));
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean handleMoveEvent(MotionEvent event) {
        postTargetNumber(getClickNumber(event));
        return super.onTouchEvent(event);
    }

}
