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

/**
 * Created by rex.wei on 2018/04/16 16:54.
 *
 * @author rex.wei@yff.com
 */
public class ScaleSeekBar extends View {

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

    public ScaleSeekBar(Context context) {
        this(context, null);
    }

    public ScaleSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, context.getResources().getDisplayMetrics());
        seekBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        textPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, context.getResources().getDisplayMetrics());
        roundRectRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        dotRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        dotPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, context.getResources().getDisplayMetrics());
        thumbRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        seekBarThumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_thumb);
        maxHeight = Math.max(seekBarThumb.getHeight(), seekBarHeight) + textPadding + textSize;
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
        int extraDistance = seekBarThumb.getHeight() / 2 - seekBarHeight / 2;
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


    private boolean handleDownEvent(MotionEvent event) {
        return false;
    }

    private boolean handleMoveEvent(MotionEvent event) {
        return false;
    }

}
