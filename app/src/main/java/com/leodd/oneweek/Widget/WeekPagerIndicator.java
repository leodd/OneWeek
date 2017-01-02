package com.leodd.oneweek.Widget;

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

import org.joda.time.DateTime;

import java.util.Date;

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
    private int mDayTextSize;
    private int mWeekTextSize;
    private float mOffsetOfDayText;
    private float mOffsetOfWeekText;
    private int mColor;

    private DateTime pivotDate;

    private int pivotPosition;
    private int currentPosition;//the current position will immediately reflect the change of position
    private int currentDatePosition;//the current date position indicates the position of the current date
    private float mPositionOffset;

    //this offset is a penalty to the current position
    //after adding this offset, the current position could be a indicator of the current day of week
    private int mIntrinsicOffset;

    private int dayOfWeek;

    private int mTouchSlope;
    private boolean mIgnoreTap;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private String[] mTabDayStr = new String[7];
    private String[] mTabDayStrOfLastWeek = new String[7];
    private String[] mTabDayStrOfNextWeek = new String[7];
    private String[] mTabWeekStr = new String[] {"SUN","MON","TUE","WED","THU","FRI","SAT"};

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
        currentPosition = position;
        mPositionOffset = positionOffset;
        invalidate();
    }

    public void setCurrentDate(Date date, int position) {
        if(mViewPager == null) {
            return;
        }

        pivotDate = new DateTime(date);
        pivotPosition = position;
        currentDatePosition = pivotPosition;

        dayOfWeek = pivotDate.getDayOfWeek() % 7;

        mIntrinsicOffset = (pivotPosition % 7) - (pivotDate.getDayOfWeek() % 7);
        assembleTabDateStr(pivotDate);
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

                currentDatePosition = position;

                DateTime currentDate = pivotDate.plusDays(currentDatePosition - pivotPosition);

                int temp = currentDate.getDayOfWeek() % 7;

                if((temp == 6 && dayOfWeek == 0) || (temp == 0 && dayOfWeek == 6)) {
                    dayOfWeek = temp;
                    assembleTabDateStr(currentDate);
                }

                dayOfWeek = temp;
            }
        });
    }

    private void assembleTabDateStr(DateTime currentDate) {
        DateTime sundayThisWeek = currentDate.plusDays(-dayOfWeek);

        for(int i = 0; i < 7; i++) {
            mTabDayStr[i] = Integer.toString(sundayThisWeek.plusDays(i).getDayOfMonth());

            mTabDayStrOfLastWeek[i] = Integer.toString(sundayThisWeek.plusDays(i - 7).getDayOfMonth());

            mTabDayStrOfNextWeek[i] = Integer.toString(sundayThisWeek.plusDays(i + 7).getDayOfMonth());
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

        if((currentPosition - mIntrinsicOffset) % 7 == 6) {
            //if the current position show that it is now on saturday
            //that means the week is changing

            if(currentDatePosition > currentPosition) {
                //if the real position is behind the current date position
                //it means that the week is changing to the last week
                drawUpDownFlag = 2;
            }
            else {
                //else, the week is changing to the next week
                drawUpDownFlag = 1;
            }
        }

        //translate the canvas if the week is changing
        if(drawUpDownFlag == 1) {
            canvas.translate(0, - (mPositionOffset * mHorizontalGap));
        }

        if(drawUpDownFlag == 2) {
            canvas.translate(0, (1 - mPositionOffset) * mHorizontalGap);
        }

        //draw those text
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

        //draw the indicator
        if(drawUpDownFlag == 0) {
            canvas.save();
            canvas.translate(((currentPosition - mIntrinsicOffset) % 7) * mGap + mPositionOffset * mGap + mGap / 2, 0);
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

        int temp = (currentDatePosition - mIntrinsicOffset) % 7;
        temp = x - temp;

        mViewPager.setCurrentItem(currentDatePosition + temp);
    }
}