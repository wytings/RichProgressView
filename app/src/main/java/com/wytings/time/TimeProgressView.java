package com.wytings.time;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rex.wei on 2018/04/13 19:56.
 *
 * @author rex.wei@yff.com
 */
public class TimeProgressView extends View {

    final TextPaint paint;
    final List<TimeData> dataList = new ArrayList<>();
    int titleTextSize, timeTextSize, dotTopPadding, dotBottomPadding, dotHorizontalPadding, dotRadius, lineHeight;

    public TimeProgressView(Context context) {
        this(context, null);
    }

    public TimeProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new TextPaint();
        titleTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, context.getResources().getDisplayMetrics());
        timeTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics());
        dotTopPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
        dotBottomPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        dotHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());
        dotRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());
        lineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        dataList.addAll(getTestData());
    }

    public Collection<? extends TimeData> getTestData() {
        List<TimeData> list = new LinkedList<>();
        list.add(new TimeData(System.currentTimeMillis(), "开始认购"));
        list.add(new TimeData(System.currentTimeMillis() + 24 * 60 * 60 * 1000, "认购截止"));
        list.add(new TimeData(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000, "公布日"));
        list.add(new TimeData(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000, "上市日"));
        return list;
    }

    public static class TimeData {
        long timestamp;
        String desc;

        TimeData(long timestamp, String desc) {
            this.timestamp = timestamp;
            this.desc = desc;
        }
    }


}
