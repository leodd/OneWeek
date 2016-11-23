package com.leodd.oneweek.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.DateObject;
import com.leodd.oneweek.Utils.DateUtil;
import com.leodd.oneweek.Utils.DayType;

/**
 * Created by Leodd
 * on 2016/5/20.
 */
public class WeekPagerIndicator extends View {
    private Context mContext;
    private ViewPager mViewPager;
    private float mRadiusOfCorner;
    private float mGap;
    private float mHorizontalGap;
    private float mWidth;
    private float mHeight;
    private int mCurrentWeek;
    private int mDayTextSize;
    private int mWeekTextSize;
    private float mOffsetOfDayText;
    private float mOffsetOfWeekText;
    private int mColor;
    private DateObject mCurrentDate;
    private int mCurrentDatePosition;
    private int mPagerPosition;
    private int mPosition;
    private float mPositionOffset;
    private int mIntrinsicOffset;
    private int mTouchSlope;
    private boolean mIgnoreTap;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private String[] mTabDayStr = new String[] {"1/1", "1/2", "1/3", "1/4", "1/5", "1/6", "1/7"};
    private String[] mTabDayStrOfLastWeek = new String[] {"1/1", "1/2", "1/3", "1/4", "1/5", "1/6", "1/7"};
    private String[] mTabDayStrOfNextWeek = new String[] {"1/1", "1/2", "1/3", "1/4", "1/5", "1/6", "1/7"};
    private final String[] mTabWeekStr = new String[] {"周一","周二","周三","周四","周五","周六","周日"};

    private Paint paintText;
    private Paint paintWeekText;
    private Paint paintIndicator;
    private RectF rectF;

    private final int DEFAULT_RADIUS_OF_CORNER = 10;
    private final int DEFAULT_COLOR = Color.WHITE;
    private final int DEFAULT_DAY_TEXT_SIZE = 30;
    private final int DEFAULT_WEEK_TEXT_SIZE = 20;

    public WeekPagerIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public WeekPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WeekPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        handleTypedArray(context, attrs);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(mColor);
        paintText.setTextSize(mDayTextSize);
        paintText.setTypeface(Typeface.MONOSPACE);
        paintText.setTextAlign(Paint.Align.CENTER);

        paintWeekText = new Paint();
        paintWeekText.setAntiAlias(true);
        paintWeekText.setColor(mColor);
        paintWeekText.setTextSize(mWeekTextSize);
        paintWeekText.setTypeface(Typeface.MONOSPACE);
        paintWeekText.setTextAlign(Paint.Align.CENTER);

        paintIndicator = new Paint();
        paintIndicator.setAntiAlias(true);
        paintIndicator.setColor(mColor);
        paintIndicator.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        rectF = new RectF();

        mTouchSlope = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if(attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeekPagerIndicator);

        mDayTextSize = typedArray.getDimensionPixelSize(R.styleable.WeekPagerIndicator_wpi_day_text_size, DEFAULT_DAY_TEXT_SIZE);
        mWeekTextSize = typedArray.getDimensionPixelSize(R.styleable.WeekPagerIndicator_wpi_week_text_size, DEFAULT_WEEK_TEXT_SIZE);
        mColor = typedArray.getColor(R.styleable.WeekPagerIndicator_wpi_color, DEFAULT_COLOR);
        mRadiusOfCorner = typedArray.getDimension(R.styleable.WeekPagerIndicator_wpi_radius_of_corner,DEFAULT_RADIUS_OF_CORNER);

        typedArray.recycle();
    }

    private void trigger(int position, float positionOffset) {
        mPosition = position;
        mPositionOffset = positionOffset;
        invalidate();
    }

    public void setCurrentDate(DateObject date, int position) {
        mCurrentDate = date;
        mCurrentWeek = DateUtil.getWeekByDate(mCurrentDate);
        mCurrentDatePosition = position;
        mPagerPosition = position;
        mIntrinsicOffset = (mCurrentDatePosition % 7) - weekToNum(mCurrentWeek);
        assembleTabDateStr();
        invalidate();
    }

    public void setViewPager(ViewPager vp) {
        mViewPager = vp;

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                trigger(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                mPagerPosition = position;

                assembleTabDateStr();
            }
        });
    }

    private void assembleTabDateStr() {
        DateObject DateOfPage = DateUtil.getOffsetDate(mCurrentDate, mPagerPosition - mCurrentDatePosition);
        int offset = weekToNum(DateUtil.getWeekByDate(DateOfPage));

        DateObject temp;
        for(int i = 0; i < 7; i++) {
            temp = DateUtil.getOffsetDate(DateOfPage, i - offset);
            mTabDayStr[i] = temp.getDay() + "";

            temp = DateUtil.getOffsetDate(DateOfPage, i - offset - 7);
            mTabDayStrOfLastWeek[i] = temp.getDay() + "";

            temp = DateUtil.getOffsetDate(DateOfPage, i - offset + 7);
            mTabDayStrOfNextWeek[i] = temp.getDay() + "";
        }
    }

    private int weekToNum(int week) {
        switch(week) {
            case DayType.MONDAY:
                return 0;
            case DayType.TUESDAY:
                return 1;
            case DayType.WEDNESDAY:
                return 2;
            case DayType.THURSDAY:
                return 3;
            case DayType.FRIDAY:
                return 4;
            case DayType.SATURDAY:
                return 5;
            case DayType.SUNDAY:
                return 6;
            default:
                return 0;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mGap = mWidth / 8;
        mHorizontalGap = mHeight;

        mOffsetOfDayText = mHeight / 6;

        mOffsetOfWeekText = mHeight / 5;

        rectF.set(0, 0, mGap, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        final float textAlign = mHeight / 2 - (paintText.descent() + paintText.ascent()) / 2;

        int drawUpDownFlag = 0;

        if((mPosition - mIntrinsicOffset) % 7 == 6) {
            if(mPosition - mPagerPosition == 0) {
                drawUpDownFlag = 1;
            }
            else {
                drawUpDownFlag = 2;
            }
        }

        if(drawUpDownFlag == 1) {
            canvas.translate(0, - (mPositionOffset * mHorizontalGap));
        }

        if(drawUpDownFlag == 2) {
            canvas.translate(0, (1 - mPositionOffset) * mHorizontalGap);
        }

        for(int i = 0; i < 7; i++) {
            canvas.drawText(mTabDayStr[i], mGap * (i + 1), textAlign + mOffsetOfDayText, paintText);
            canvas.drawText(mTabWeekStr[i], mGap * (i + 1), textAlign - mOffsetOfWeekText, paintWeekText);

            if(drawUpDownFlag == 1) {
                canvas.drawText(mTabDayStrOfNextWeek[i], mGap * (i + 1), textAlign + mHorizontalGap + mOffsetOfDayText, paintText);
                canvas.drawText(mTabWeekStr[i], mGap * (i + 1), textAlign + mHorizontalGap - mOffsetOfWeekText, paintWeekText);
            }

            if(drawUpDownFlag == 2) {
                canvas.drawText(mTabDayStrOfLastWeek[i], mGap * (i + 1), textAlign - mHorizontalGap + mOffsetOfDayText, paintText);
                canvas.drawText(mTabWeekStr[i], mGap * (i + 1), textAlign - mHorizontalGap - mOffsetOfWeekText, paintWeekText);
            }
        }

        if(drawUpDownFlag == 0) {
            canvas.save();
            canvas.translate(((mPosition - mIntrinsicOffset) % 7) * mGap + mPositionOffset * mGap + mGap / 2, 0);
            canvas.drawRoundRect(rectF, mRadiusOfCorner, mRadiusOfCorner, paintIndicator);
            canvas.restore();
        }
        else {
            if(drawUpDownFlag == 1) {
                canvas.save();
                canvas.translate(6 * mGap + mGap / 2, 0);
                canvas.drawRoundRect(rectF, mRadiusOfCorner, mRadiusOfCorner, paintIndicator);
                canvas.restore();

                canvas.save();
                canvas.translate(mGap / 2, mHorizontalGap);
                canvas.drawRoundRect(rectF, mRadiusOfCorner, mRadiusOfCorner, paintIndicator);
                canvas.restore();
            }

            if(drawUpDownFlag == 2) {
                canvas.save();
                canvas.translate(mGap / 2, 0);
                canvas.drawRoundRect(rectF, mRadiusOfCorner, mRadiusOfCorner, paintIndicator);
                canvas.restore();

                canvas.save();
                canvas.translate(6 * mGap + mGap / 2, - mHorizontalGap);
                canvas.drawRoundRect(rectF, mRadiusOfCorner, mRadiusOfCorner, paintIndicator);
                canvas.restore();
            }
        }

        canvas.restoreToCount(sc);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if(action != MotionEvent.ACTION_DOWN && mIgnoreTap) {
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
                if(x > mWidth - (mGap/2) ||
                        x < mGap/2) {
                    break;
                }
                float tempX = x - (mGap / 2);
                tempX = tempX / mGap;
                changePage((int) tempX);
        }

        return true;
    }

    private void changePage(int x) {
        if(x > 6 || x < 0) {
            return;
        }

        int temp = (mPagerPosition - mIntrinsicOffset) % 7;
        temp = x - temp;

        mViewPager.setCurrentItem(mPagerPosition + temp);
    }
}