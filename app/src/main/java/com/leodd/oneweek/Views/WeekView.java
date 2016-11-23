package com.leodd.oneweek.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.DayType;

/**
 * Created by Leod
 * on 2016/4/30.
 */
public class WeekView extends View{
    //config list
    private int mWeek;
    private boolean mTouchable;
    private int mBackGroundColor;
    private int mSelectedBackGroundColor;
    private int mTextColor;
    private int mSelectedTextColor;
    private float mRadiusOfCorner;
    private float mInnerMargin;
    private float mTextSize;
    private boolean isShowBackground;

    //default values
    private final int DEFAULT_WEEK = DayType.MONDAY |
            DayType.TUESDAY |
            DayType.WEDNESDAY |
            DayType.THURSDAY |
            DayType.FRIDAY |
            DayType.SATURDAY |
            DayType.SUNDAY;
    private final boolean DEFAULT_TOUCH_ABILITY = false;
    private final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private final int DEFAULT_SELECTED_BACKGROUND_COLOR = Color.BLACK;
    private final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private final int DEFAULT_SELECTED_TEXT_COLOR = Color.WHITE;
    private final int DEFAULT_RADIUS_OF_CORNER = 10;
    private final int DEFAULT_INNER_MARGIN = 0;
    private final int DEFAULT_TEXT_SIZE = 20;
    private final boolean DEFAULT_IS_SHOW_BACKGROUND = false;

    private final String[] nameOfWeek = new String[] {"周一","周二","周三","周四","周五","周六","周日"};

    private float mWidthOfTheBackground;
    private float mHeightOfTheBackground;

    private float mStartPositionOfText;
    private float mInnerBottom;
    private float mInnerTop;
    private float mInnerStart;
    private float mInnerEnd;

    private float mInnerRadiusOfCorner;

    private float mGap;

    private Paint mPaint;
    private RectF mRectF;

    private int mTouchSlope;
    private boolean mIgnoreTap;
    private float mInitialMotionX;
    private float mInitialMotionY;

    public WeekView(Context context) {
        super(context);
        init(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
        handleTypedArray(context, attrs);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if(attrs == null) {return;}

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeekView);
        mWeek = typedArray.getInteger(R.styleable.WeekView_week, DEFAULT_WEEK);
        mTouchable = typedArray.getBoolean(R.styleable.WeekView_touch_ability, DEFAULT_TOUCH_ABILITY);
        mBackGroundColor = typedArray.getColor(R.styleable.WeekView_background_color, DEFAULT_BACKGROUND_COLOR);
        mSelectedBackGroundColor = typedArray.getColor(R.styleable.WeekView_selected_background_color, DEFAULT_SELECTED_BACKGROUND_COLOR);
        mTextColor = typedArray.getColor(R.styleable.WeekView_text_color, DEFAULT_TEXT_COLOR);
        mSelectedTextColor = typedArray.getColor(R.styleable.WeekView_selected_text_color, DEFAULT_SELECTED_TEXT_COLOR);
        mRadiusOfCorner = typedArray.getDimensionPixelSize(R.styleable.WeekView_radius_of_corner, DEFAULT_RADIUS_OF_CORNER);
        mInnerMargin = typedArray.getDimensionPixelSize(R.styleable.WeekView_inner_margin, DEFAULT_INNER_MARGIN);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.WeekView_text_size, DEFAULT_TEXT_SIZE);
        isShowBackground = typedArray.getBoolean(R.styleable.WeekView_is_show_background, DEFAULT_IS_SHOW_BACKGROUND);

        typedArray.recycle();
    }

    private boolean checkWeek(int day) {
        return (day <= 6 && day >= 0) && ((mWeek & (1<<day)) != 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidthOfTheBackground = (float) w;
        mHeightOfTheBackground = (float) h;

        mGap = (w - (2 * mInnerMargin)) / 7;

        mStartPositionOfText = mInnerMargin + (mGap / 2);

        mInnerStart = mInnerMargin;
        mInnerEnd = w - mInnerMargin;
        mInnerTop = mInnerMargin;
        mInnerBottom = h - mInnerMargin;

        mInnerRadiusOfCorner = mRadiusOfCorner - mInnerMargin;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.FILL);

        if(isShowBackground) {
            mPaint.setColor(mBackGroundColor);
            mRectF.set(0, 0, mWidthOfTheBackground, mHeightOfTheBackground);
            canvas.drawRoundRect(mRectF, mRadiusOfCorner, mRadiusOfCorner, mPaint);
        }

        mPaint.setColor(mSelectedBackGroundColor);
        for (int i = 0; i <= 6; i++) {
            if(checkWeek(i)) {
                int k = i + 1;
                for (; k <=6; k++) {
                    if(!checkWeek(k)) {break;}
                }
                mRectF.set(mInnerStart + (mGap * i), mInnerTop, mInnerStart + (mGap * k), mInnerBottom);
                canvas.drawRoundRect(mRectF, mInnerRadiusOfCorner, mInnerRadiusOfCorner, mPaint);
                i = k;
            }
        }

        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);
        float textAlignPosition = ((mInnerTop + mInnerBottom) / 2) - ((mPaint.descent() + mPaint.ascent()) / 2);
        for (int i = 0; i <= 6; i++) {
            if(checkWeek(i)) {
                mPaint.setColor(mSelectedTextColor);
            }
            else {
                mPaint.setColor(mTextColor);
            }
            canvas.drawText(nameOfWeek[i], mStartPositionOfText + (mGap * i), textAlignPosition, mPaint);
        }
    }

    public void setWeek(int day) {
        mWeek = day;
        postInvalidate();
    }

    public int getWeek() {
        return mWeek;
    }

    private void reverseCertainDay(int day) {
        if(day <= 6 && day >= 0) {
            mWeek ^= (1 << day);
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if(!mTouchable || (action != MotionEvent.ACTION_DOWN && mIgnoreTap)) {
            return false;
        }

        final float x = event.getX();
        final float y = event.getY();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = x;
                mInitialMotionY = y;
                mIgnoreTap = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if(Math.abs(mInitialMotionX - x) > mTouchSlope ||
                        Math.abs(mInitialMotionY - y) > mTouchSlope) {
                    mIgnoreTap = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                float tempX = event.getX() - mInnerMargin;
                int position = (int) (tempX / mGap);
                position = position > 6 ? 6 : position;
                reverseCertainDay(position);
                break;
        }

        return true;
    }
}
