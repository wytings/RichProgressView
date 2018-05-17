package com.wytings.progress.widget.helper;

import android.graphics.RectF;

/**
 * Created on 2017/4/10.
 *
 * @author wytings
 */

public class TextDrawMetrics {
    public float x;
    public float y;
    public float width;
    public float height;
    public RectF rectF = new RectF();

    public TextDrawMetrics offset(float dx, float dy) {
        x += dx;
        y += dy;
        rectF.offset(dx, dy);
        return this;
    }
}
