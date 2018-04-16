package com.wytings.progress.widget.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/**
 * the position of (x,y) is always supposed to be {@link TextAxisType#LEFT_TOP} when calculating new position.
 * <p>
 * Created by rex.wei on 2018/1/3.
 *
 * @author rex.wei
 */

public class GraphTextHelper {

    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
    private final TextMetrics textMetrics = new TextMetrics();
    private final float density;

    public GraphTextHelper(Context context) {
        density = context.getResources().getDisplayMetrics().density;
    }

    public int dp(float dp) {
        return (int) (dp * density + 0.5);
    }


    private void drawText(Canvas canvas, String text, float textSize, int textColor, float x, float y) {
        textPaint.reset();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        canvas.drawText(text, x, y, textPaint);
    }


    public RectF drawText(Canvas canvas, String text, float textSize, int textColor, float x, float y, TextAxisType type) {
        TextMetrics metrics = getTextMetrics(text, x, y, textSize, type);
        drawText(canvas, text, textSize, textColor, metrics.x, metrics.y);
        return metrics.textRectF;
    }

    public RectF drawText(Canvas canvas, String text, float textSize, int textColor, float x, float y, TextAxisType type, int pointPadding) {
        TextMetrics metrics = getTextMetrics(text, x, y, textSize, type, pointPadding, 0);
        drawText(canvas, text, textSize, textColor, metrics.x, metrics.y);
        return metrics.textRectF;
    }


    private void drawTextRect(Canvas canvas, RectF textRectF, int borderColor, int background, int radius) {
        textPaint.reset();

        textPaint.setAntiAlias(true);
        textPaint.setColor(background);
        textPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(textRectF, radius, radius, textPaint);

        textPaint.setColor(borderColor);
        textPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(textRectF, radius, radius, textPaint);
    }

    public void drawTextArea(Canvas canvas, String text, float textSize, TextMetrics metrics, int textColor, int borderColor, int background) {
        drawTextArea(canvas, text, textSize, metrics, textColor, borderColor, background, 0);
    }

    public void drawTextArea(Canvas canvas, String text, float textSize, TextMetrics metrics, int textColor, int borderColor, int background, int radius) {
        drawTextRect(canvas, metrics.textRectF, borderColor, background, radius);
        drawText(canvas, text, textSize, textColor, metrics.x, metrics.y);
    }

    public TextMetrics getTextMetrics(String text, float x, float y, float textSize, TextAxisType axisType, int pointPadding, int rectPadding) {
        TextMetrics metrics = getTextMetrics(text, x, y, textSize, axisType);
        metrics = paddingTextMetricsPoint(metrics, axisType, pointPadding);
        metrics.inset(-rectPadding, -rectPadding);
        return metrics;
    }

    public TextMetrics getTextMetrics(String text, float x, float y, float textSize, TextAxisType axisType) {
        TextMetrics metrics = getTextMetrics(text, x, y, textSize, textMetrics);
        float[] xy = getOffset(metrics.textWidth, metrics.textHeight, axisType);
        metrics.offset(xy[0], xy[1]);
        return metrics;
    }

    private TextMetrics getTextMetrics(String text, float x, float y, float textSize, TextMetrics metrics) {

        textPaint.reset();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(textSize);

        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();

        metrics.textHeight = textHeight;
        metrics.textWidth = textWidth;

        textPaint.getFontMetrics(fontMetrics);

        metrics.x = x;
        metrics.y = y - fontMetrics.ascent;

        metrics.textRectF.set(x, y, x + textWidth, y + textHeight + fontMetrics.descent);

        return metrics;
    }

    private TextMetrics paddingTextMetricsPoint(TextMetrics metrics, TextAxisType axisType, int padding) {
        switch (axisType) {
            case LEFT_TOP:
                metrics.offset(padding, padding);
                break;
            case LEFT_CENTER:
                metrics.offset(padding, 0);
                break;
            case LEFT_BOTTOM:
                metrics.offset(padding, -padding);
                break;
            case RIGHT_TOP:
                metrics.offset(-padding, padding);
                break;
            case RIGHT_CENTER:
                metrics.offset(-padding, 0);
                break;
            case RIGHT_BOTTOM:
                metrics.offset(-padding, -padding);
                break;
            case CENTER_TOP:
                metrics.offset(0, padding);
                break;
            case CENTER_BOTTOM:
                metrics.offset(0, -padding);
                break;
            case CENTER:
                // ignore
                break;
            default:
                break;
        }
        return metrics;
    }

    public void drawMultiColorText(Canvas canvas, float x, float y, String[] strings, int[] colors, int textSize, TextAxisType axisType) {
        drawMultiColorText(canvas, x, y, strings, colors, textSize, axisType, -1);
    }

    public void drawMultiColorText(Canvas canvas, float x, float y, String[] strings, int[] colors, int textSize, TextAxisType axisType, int maxWidth) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < strings.length; i++) {
            int start = builder.length();
            builder.append(strings[i]);
            builder.setSpan(new ForegroundColorSpan(colors[i]), start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        textPaint.reset();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.LEFT);

        int width = getOptimalWidth(textPaint, textSize, builder.toString(), maxWidth);
        StaticLayout staticLayout = new StaticLayout(builder, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        int textWidth = staticLayout.getWidth();
        int textHeight = staticLayout.getHeight();
        float[] xy = translateXY(x, y, textWidth, textHeight, axisType);
        float textX = xy[0];
        float textY = xy[1];

        canvas.save();
        canvas.translate(textX, textY);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private int getOptimalWidth(TextPaint p, int textSize, String s, int maxWidth) {
        int width = (int) (p.measureText(s) + 0.5);
        if (maxWidth < 1) {
            return width;
        }

        while (width > maxWidth) {
            textSize -= 2;
            p.setTextSize(textSize);
            width = (int) (p.measureText(s) + 0.5);
        }

        return width;

    }

    private float[] translateXY(float x, float y, float textWidth, float textHeight, TextAxisType axisType) {
        if (axisType == null) {
            return new float[]{x, y};
        }

        float[] xyOffset = getOffset(textWidth, textHeight, axisType);
        return new float[]{x + xyOffset[0], y + xyOffset[1]};

    }

    private float[] getOffset(float textWidth, float textHeight, TextAxisType axisType) {

        if (axisType == null) {
            return new float[]{0, 0};
        }

        float offsetX = 0;
        float offsetY = 0;

        switch (axisType) {
            case LEFT_TOP:
                offsetX = 0;
                offsetY = 0;
                break;
            case LEFT_CENTER:
                offsetX = 0;
                offsetY = -textHeight / 2;
                break;
            case LEFT_BOTTOM:
                offsetX = 0;
                offsetY = -textHeight;
                break;
            case RIGHT_TOP:
                offsetX = -textWidth;
                offsetY = 0;
                break;
            case RIGHT_CENTER:
                offsetX = -textWidth;
                offsetY = -textHeight / 2;
                break;
            case RIGHT_BOTTOM:
                offsetX = -textWidth;
                offsetY = -textHeight;
                break;
            case CENTER_TOP:
                offsetX = -textWidth / 2;
                offsetY = 0;
                break;
            case CENTER_BOTTOM:
                offsetX = -textWidth / 2;
                offsetY = -textHeight;
                break;
            case CENTER:
                offsetX = -textWidth / 2;
                offsetY = -textHeight / 2;
                break;
            default:
                break;
        }
        return new float[]{offsetX, offsetY};
    }
}
