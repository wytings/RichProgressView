package com.wytings.progress.widget.helper;

import android.graphics.RectF;

/**
 * Created by rex.wei on 2017/4/10.
 */

public class TextMetrics {
    public float x;
    public float y;
    public float textWidth;
    public float textHeight;
    public RectF textRectF = new RectF();

    public TextMetrics offset(float dx, float dy) {
        x += dx;
        y += dy;
        textRectF.offset(dx, dy);
        return this;
    }

    public TextMetrics inset(float dx, float dy) {
        textRectF.inset(dx, dy);
        return this;
    }
}
