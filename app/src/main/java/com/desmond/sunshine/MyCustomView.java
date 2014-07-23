package com.desmond.sunshine;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by desmond on 23/7/14.
 */
public class MyCustomView extends View {

    public MyCustomView(Context context) {
        super(context);
    }

    public MyCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int myHeight, myWidth;

//        // Height
//        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        if (hSpecMode == MeasureSpec.EXACTLY) {
//            myHeight = hSpecSize;
//        } else if (hSpecMode == MeasureSpec.AT_MOST) {
//
//        }
//
//        // Width
//
//
//        setMeasuredDimension(myWidth, myHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return super.dispatchPopulateAccessibilityEvent(event);
    }
}
