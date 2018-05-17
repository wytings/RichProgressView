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
import com.wytings.progress.widget.helper.TextHelper;
import com.wytings.progress.widget.helper.TextDrawType;

import java.util.Locale;

/**
 * Created on 2018/04/16 16:54.
 *
 * @author wytings
 */
public class SpecialSeekBar extends View {

    public static abstract class OnSeekChangeListener {
        public abstract void onChange(float number);

        public void onCancel() {

        }
    }

    LinearGradient linearGradient;
    final int barHeight;
    final Bitmap dotThumb;
    final int maxHeight;
    final int textSize;
    final int textPadding;
    final int seekBarBackgroundColor;
    final TextPaint textPaint;
    final RectF barRectF = new RectF();
    final RectF temporaryRectF = new RectF();
    final float roundRectRadius;
    final float dotPadding;
    final float dotRadius;
    final float thumbRadius;
    final TextHelper textHelper;
    float currentNumber = -1;
    float maxNumber = -1;
    String maxNumberText = "";
    OnSeekChangeListener changeListener;

    public SpecialSeekBar(Context context) {
        this(context, null);
    }

    public SpecialSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        textHelper = new TextHelper();
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, context.getResources().getDisplayMetrics());
        barHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        textPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
        roundRectRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        dotRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        dotPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, context.getResources().getDisplayMetrics());
        thumbRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, context.getResources().getDisplayMetrics());
        dotThumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_slider_thumb);
        maxHeight = (int) (barHeight + (thumbRadius - barHeight / 2) + textPadding + textSize);
        seekBarBackgroundColor = Color.parseColor("#1E2732");

        if (isInEditMode()) {
            setCurrentNumber(5);
            setMaxNumber(10);
        }

    }

    public void setChangeListener(OnSeekChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void setCurrentNumber(float currentNumber) {
        this.currentNumber = currentNumber;
        invalidate();
    }

    public float getCurrentNumber() {
        return currentNumber;
    }

    public float getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(float maxNumber) {
        this.maxNumber = maxNumber;
        this.maxNumberText = String.format(Locale.getDefault(), "%.1f packets", maxNumber);
        invalidate();
        if (currentNumber > maxNumber) {
            currentNumber = maxNumber / 2;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        linearGradient = new LinearGradient(getPaddingLeft()
                , getPaddingTop()
                , w - getPaddingRight()
                , getPaddingTop()
                , Color.parseColor("#80CDF9")
                , Color.parseColor("#499EF0")
                , Shader.TileMode.CLAMP);
        barRectF.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
        int verticalExtraDistance = (int) (thumbRadius - barHeight / 2);
        barRectF.top += verticalExtraDistance;
        barRectF.bottom = barRectF.top + barHeight;

        int horizontalExtraDistance = (int) (thumbRadius - dotPadding);
        barRectF.inset(horizontalExtraDistance, 0);


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
        textHelper.drawText(canvas,
                "0",
                textSize,
                Color.GRAY,
                barRectF.left, barRectF.top + textPadding,
                TextDrawType.LEFT_TOP);

        textHelper.drawText(canvas,
                maxNumberText,
                textSize,
                Color.GRAY,
                barRectF.right, barRectF.top + textPadding,
                TextDrawType.RIGHT_TOP);
    }

    private void drawSeekBar(Canvas canvas) {

        textPaint.setShader(linearGradient);

        canvas.drawRoundRect(barRectF, roundRectRadius, roundRectRadius, textPaint);

        int currentIntNumber = getMaxInt(currentNumber);
        int maxIntNumber = getMaxInt(maxNumber);
        int count = maxIntNumber + 1;
        float interval = (barRectF.width() - dotPadding * 2) / maxIntNumber;
        float centerY = barRectF.centerY();

        float currentDotCenterX = barRectF.left + dotPadding + currentIntNumber * interval;

        if (currentIntNumber < maxIntNumber) {
            resetPaint(seekBarBackgroundColor);
            temporaryRectF.set(barRectF);
            temporaryRectF.left = currentDotCenterX;
            canvas.drawRoundRect(temporaryRectF, roundRectRadius, roundRectRadius, textPaint);
        }

        resetPaint(Color.WHITE);
        for (int i = 0; i < count; i++) {
            canvas.drawCircle(barRectF.left + dotPadding + i * interval, centerY, dotRadius, textPaint);
        }

        canvas.drawBitmap(dotThumb, null, getDotRectF(currentDotCenterX, centerY), textPaint);
    }

    private int getMaxInt(float number) {
        return (int) Math.ceil(number);
    }

    private RectF getDotRectF(float currentDotCenterX, float centerY) {
        temporaryRectF.set(currentDotCenterX - thumbRadius, centerY - thumbRadius,
                currentDotCenterX + thumbRadius, centerY + thumbRadius);
        return temporaryRectF;
    }

    private void resetPaint(int color) {
        textPaint.reset();
        textPaint.setAntiAlias(true);
        textPaint.setColor(color);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                temporaryRectF.set(barRectF);
                temporaryRectF.inset(-100, -100);
                if (temporaryRectF.contains(event.getX(), event.getY())) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    dispatchTargetNumber(getFixedClickNumber(event));
                    return true;
                }
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                dispatchTargetNumber(getFixedClickNumber(event));
                return true;
            default:
                if (changeListener != null) {
                    changeListener.onCancel();
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                return super.onTouchEvent(event);
        }

    }

    private int getFixedClickNumber(MotionEvent event) {
        float interval = (barRectF.width() - dotPadding * 2) / getMaxInt(maxNumber);
        float number = (event.getX() - barRectF.left - dotPadding) / interval + 0.5f;
        return (int) number;
    }

    private boolean dispatchTargetNumber(int number) {
        int maxInt = getMaxInt(maxNumber);

        if (number < 0) {
            number = 0;
        } else if (number > maxInt) {
            number = maxInt;
        }

        int currentIntNumber = getMaxInt(currentNumber);
        if (number != currentIntNumber) {
            currentNumber = number == maxInt ? maxNumber : number;
            if (changeListener != null) {
                changeListener.onChange(currentNumber);
            }
            invalidate();
            return true;
        }

        return false;
    }

}
