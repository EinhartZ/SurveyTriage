package com.qaelum.einhart.qaelumsurvey;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Einhart on 10/14/2016.
 */

public class TouchBlackHoleView extends View {
    private boolean touch_disabled= false;

    public TouchBlackHoleView(Context context) {
        super(context);
    }

    public TouchBlackHoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchBlackHoleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TouchBlackHoleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touch_disabled;
    }

    public void disable_touch(boolean b) {
        touch_disabled = b;
    }
}
