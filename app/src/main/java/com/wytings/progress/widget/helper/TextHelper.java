package com.wytings.progress.widget.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

/**
 * (x,y) based on {@link TextDrawType#LEFT_TOP}
 * <p>
 * Created on 2018/1/3.
 *
 * @author wytings
 */

public class TextHelper {

    private final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint.FontMetrics paintFontMetrics = new Paint.FontMetrics();
    private final TextDrawMetrics drawMetrics = new TextDrawMetrics();

    public void drawText(Canvas canvas, String text, float textSize, int textColor, float x, float y, TextDrawType type) {
        TextDrawMetrics metrics = getTextMetrics(text, x, y, textSize, type);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        canvas.drawText(text, metrics.x, metrics.y, paint);
    }


    private TextDrawMetrics getTextMetrics(String text, float x, float y, float textSize, TextDrawType axisType) {
        TextDrawMetrics metrics = getTextMetrics(text, x, y, textSize, drawMetrics);
        float[] xy = calculateOffset(metrics.width, metrics.height, axisType);
        metrics.offset(xy[0], xy[1]);
        return metrics;
    }

    private TextDrawMetrics getTextMetrics(String text, float x, float y, float textSize, TextDrawMetrics metrics) {

        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);

        float textWidth = paint.measureText(text);
        float textHeight = paint.getTextSize();
        metrics.height = textHeight;
        metrics.width = textWidth;
        paint.getFontMetrics(paintFontMetrics);
        metrics.x = x;
        metrics.y = y - paintFontMetrics.ascent;
        metrics.rectF.set(x, y, x + textWidth, y + textHeight + paintFontMetrics.descent);

        return metrics;
    }

    private float[] calculateOffset(float width, float height, TextDrawType type) {

        if (type == null) {
            return new float[]{0, 0};
        }

        float offset_x = 0;
        float offset_y = 0;

        switch (type) {
            case LEFT_TOP:
                offset_x = 0;
                offset_y = 0;
                break;
            case LEFT_CENTER:
                offset_x = 0;
                offset_y = -height / 2;
                break;
            case LEFT_BOTTOM:
                offset_x = 0;
                offset_y = -height;
                break;
            case CENTER_TOP:
                offset_x = -width / 2;
                offset_y = 0;
                break;
            case CENTER_BOTTOM:
                offset_x = -width / 2;
                offset_y = -height;
                break;
            case CENTER:
                offset_x = -width / 2;
                offset_y = -height / 2;
                break;
            case RIGHT_TOP:
                offset_x = -width;
                offset_y = 0;
                break;
            case RIGHT_CENTER:
                offset_x = -width;
                offset_y = -height / 2;
                break;
            case RIGHT_BOTTOM:
                offset_x = -width;
                offset_y = -height;
                break;
            default:
                break;
        }
        return new float[]{offset_x, offset_y};
    }
}
