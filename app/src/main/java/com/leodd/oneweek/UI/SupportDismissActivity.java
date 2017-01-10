package com.leodd.oneweek.UI;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by leodd on 2017/1/4.
 */

public class SupportDismissActivity extends Activity implements View.OnTouchListener {

    /** if childview support scroll
     must rewrite onDispatchTouchEvent
     and call this view requestDisallowInterceptTouchEvent, canDismiss = false
     **/
    public boolean canDismiss = true;

    private ViewGroup baseLayout;

    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;

    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setBaseLayout(ViewGroup baseLayout) {
        this.baseLayout = baseLayout;
        baseLayout.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (canDismiss) {
            // Get finger position on screen
            final int y = (int) event.getRawY();

            // switch on motion event type
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    // save default base layout height
                    defaultViewHeight = baseLayout.getHeight();

                    // init finger and view position
                    previousFingerPosition = y;
                    baseLayoutPosition = (int) baseLayout.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    // if user was doing a scroll up
                    if (isScrollingUp) {
                        // reset baselayout position
                        baseLayout.setY(0);
                        isScrollingUp = false;
                    }

                    if (isScrollingDown) {
                        baseLayout.setY(0);
                        baseLayout.getLayoutParams().height = defaultViewHeight;
                        baseLayout.requestLayout();
                        isScrollingDown = false;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!isClosing) {
                        int currentYPosition = (int) baseLayout.getY();

                        if (previousFingerPosition > y) {
                            if (!isScrollingUp)
                                isScrollingUp = true;

                            if (baseLayout.getHeight() < defaultViewHeight) {
                                baseLayout.getLayoutParams().height = baseLayout.getHeight();
                                baseLayout.requestLayout();
                            } else {
                                // action dismiss
                                if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                                    closeUpAndDismissActivity(currentYPosition);
                                    return true;
                                }
                            }
                            baseLayout.setY(baseLayout.getY() + (y - previousFingerPosition));
                        } else {
                            if (!isScrollingDown) {
                                isScrollingDown = true;
                            }

                            if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight) {
                                baseLayout.setY(0);
                                baseLayout.getLayoutParams().height = defaultViewHeight;
                                baseLayout.requestLayout();
                                isScrollingDown = false;
                            }else {
                                baseLayout.getLayoutParams().height = baseLayout.getHeight();
                                baseLayout.setY(baseLayout.getY() + (y - previousFingerPosition));
                                baseLayout.requestLayout();
                            }
                        }
                        // update position
                        previousFingerPosition = y;
                    }
                    break;
            }
            return true;
        } else
            return false;
    }

    public void closeUpAndDismissActivity(int currentPosition) {
        isClosing = true;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, -baseLayout.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
        positionAnimator.start();
    }

    public void closeDownAndDismissActivity(int currentPosition) {
        isClosing = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, screenHeight+baseLayout.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
        positionAnimator.start();
    }
}