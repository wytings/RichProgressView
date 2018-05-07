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
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.wytings.progress.R;
import com.wytings.progress.widget.helper.GraphTextHelper;
import com.wytings.progress.widget.helper.TextAxisType;

import java.util.Locale;

/**
 * Created by rex.wei on 2018/04/16 16:54.
 *
 * @author rex.wei@yff.com
 */
public class MarkSeekBar extends View {

    private static final String TAG = MarkSeekBar.class.getSimpleName();

    public static abstract class OnNumberChangeListener {
        public abstract void onChange(float number);

        public void onUp() {

        }
    }

    LinearGradient blueLinearGradient;
    final int seekBarHeight;
    final Bitmap seekBarThumb;
    final int maxHeight;
    final int textSize;
    final int textPadding;
    final int seekBarBackgroundColor;
    final TextPaint paint;
    final RectF seekBarRectF = new RectF();
    final RectF tempRectF = new RectF();
    final float roundRectRadius;
    final float dotPadding;
    final float dotRadius;
    final float thumbRadius;
    final GraphTextHelper textHelper;
    float currentNumber = -1;
    float maxNumber = -1;
    String maxText = "";
    OnNumberChangeListener numberChangeListener;

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
        thumbRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, context.getResources().getDisplayMetrics());
        seekBarThumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_slider_thumb);
        maxHeight = (int) (seekBarHeight + (thumbRadius - seekBarHeight / 2) + textPadding + textSize);
        seekBarBackgroundColor = Color.parseColor("#1E2732");

        if (isInEditMode()) {
            setCurrentNumber(5);
            setMaxNumber(10);
        }

    }

    public void setNumberChangeListener(OnNumberChangeListener numberChangeListener) {
        this.numberChangeListener = numberChangeListener;
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
        this.maxText = String.format(Locale.getDefault(), "%.1f packets", maxNumber);
        invalidate();
        if (currentNumber > maxNumber) {
            currentNumber = maxNumber / 2;
        }
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
        int verticalExtraDistance = (int) (thumbRadius - seekBarHeight / 2);
        seekBarRectF.top += verticalExtraDistance;
        seekBarRectF.bottom = seekBarRectF.top + seekBarHeight;

        int horizontalExtraDistance = (int) (thumbRadius - dotPadding);
        seekBarRectF.inset(horizontalExtraDistance, 0);


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

        int currentIntNumber = ceilNumber(currentNumber);
        int maxIntNumber = ceilNumber(maxNumber);
        int count = maxIntNumber + 1;
        float interval = (seekBarRectF.width() - dotPadding * 2) / maxIntNumber;
        float centerY = seekBarRectF.centerY();

        float currentDotCenterX = seekBarRectF.left + dotPadding + currentIntNumber * interval;

        if (currentIntNumber < maxIntNumber) {
            resetPaint(seekBarBackgroundColor);
            tempRectF.set(seekBarRectF);
            tempRectF.left = currentDotCenterX;
            canvas.drawRoundRect(tempRectF, roundRectRadius, roundRectRadius, paint);
        }

        resetPaint(Color.WHITE);
        for (int i = 0; i < count; i++) {
            canvas.drawCircle(seekBarRectF.left + dotPadding + i * interval, centerY, dotRadius, paint);
        }

        canvas.drawBitmap(seekBarThumb, null, getDotRectF(currentDotCenterX, centerY), paint);
    }

    private int ceilNumber(float number) {
        return (int) Math.ceil(number);
    }

    private RectF getDotRectF(float currentDotCenterX, float centerY) {
        tempRectF.set(currentDotCenterX - thumbRadius, centerY - thumbRadius,
                currentDotCenterX + thumbRadius, centerY + thumbRadius);
        return tempRectF;
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
                tempRectF.set(seekBarRectF);
                tempRectF.inset(-100, -100);
                if (tempRectF.contains(event.getX(), event.getY())) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    postTargetNumber(getClickNumber(event));
                    return true;
                }
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                postTargetNumber(getClickNumber(event));
                return true;
            default:
                if (numberChangeListener != null) {
                    numberChangeListener.onUp();
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                return super.onTouchEvent(event);
        }

    }

    private int getClickNumber(MotionEvent event) {
        float interval = (seekBarRectF.width() - dotPadding * 2) / ceilNumber(maxNumber);
        float number = (event.getX() - seekBarRectF.left - dotPadding) / interval + 0.5f;
        return (int) number;
    }

    private boolean postTargetNumber(int number) {
        int maxInt = ceilNumber(maxNumber);
        Log.i(TAG, "number = " + number + ", maxInt = " + maxInt);

        if (number < 0) {
            number = 0;
        } else if (number > maxInt) {
            number = maxInt;
        }

        int currentIntNumber = ceilNumber(currentNumber);
        if (number != currentIntNumber) {
            currentNumber = number == maxInt ? maxNumber : number;
            if (numberChangeListener != null) {
                numberChangeListener.onChange(currentNumber);
            }
            invalidate();
            return true;
        }

        return false;
    }

}
