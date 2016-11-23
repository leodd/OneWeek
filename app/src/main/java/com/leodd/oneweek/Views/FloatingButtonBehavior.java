package com.leodd.oneweek.Views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Leodd
 * on 2016/5/27.
 */
public class FloatingButtonBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    public FloatingButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
