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
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.leodd.oneweek.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Leodd
 * on 2016/5/7.
 */
public class TimePickerView extends View {

    private Context context;
    Handler handler;
    private TimePickerListener mListener;
    private GestureDetector mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mSimpleGestureListener;
    private Timer mTimer;
    private List<String> arrayList;
    private boolean isLoop;
    private float mRadiusOfCorner;
    private int textSize;
    private Paint paintBG;
    private Paint paintText;
    private Paint paintIndicator;
    private int position;
    float scrollX;
    private int numVisible;
    private float mWidth;
    private float mHeight;
    private float midPoint;
    float gap;
    private float maxX;
    private float minX;
    private RectF rectF;
    private float previousTouchX;

    private int mBackgroundColor;
    private int mIndicatorColor;
    private int mTextColor;

    //default values
    private final boolean DEFAULT_IS_LOOP = false;
    private final int DEFAULT_GAP = 100;
    private final int DEFAULT_RADIUS_OF_CORNER = 20;
    private final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    private final int DEFAULT_INDICATOR_COLOR = Color.YELLOW;
    private final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private final int DEFAULT_TEXT_SIZE = 36;

    public interface TimePickerListener {
        void onSelect(int position);
    }

    public TimePickerView(Context context) {
        super(context);
        init(context, null);
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;

        handleTypedArray(context, attrs);

        position = 0;
        scrollX = 0;

        paintBG = new Paint();
        paintBG.setAntiAlias(true);
        paintBG.setColor(mBackgroundColor);
        paintBG.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setTextSize(textSize);
        paintText.setColor(mTextColor);
        paintText.setTypeface(Typeface.MONOSPACE);
        paintText.setTextAlign(Paint.Align.CENTER);

        paintIndicator = new Paint();
        paintIndicator.setAntiAlias(true);
        paintIndicator.setColor(mIndicatorColor);
        paintIndicator.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        rectF = new RectF();

        handler = new MessageHandler(this);
        mSimpleGestureListener = new TimePickerGestureListener(this);
        mGestureDetector = new GestureDetector(context, mSimpleGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if(attrs == null) {return;}

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimePickerView);
        isLoop = typedArray.getBoolean(R.styleable.TimePickerView_tm_is_loop, DEFAULT_IS_LOOP);
        gap = typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tm_gap, DEFAULT_GAP);
        mBackgroundColor = typedArray.getColor(R.styleable.TimePickerView_tm_background_color, DEFAULT_BACKGROUND_COLOR);
        mIndicatorColor = typedArray.getColor(R.styleable.TimePickerView_tm_indicator_color, DEFAULT_INDICATOR_COLOR);
        mTextColor = typedArray.getColor(R.styleable.TimePickerView_tm_text_color, DEFAULT_TEXT_COLOR);
        mRadiusOfCorner = typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tm_radius_of_corner, DEFAULT_RADIUS_OF_CORNER);
        textSize = typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tm_text_size, DEFAULT_TEXT_SIZE);

        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        midPoint = w / 2;
        numVisible = (int) (w / gap);
        numVisible ++;
    }

    private void computeBounds() {
        if(arrayList == null) {
             return;
        }

        maxX = (arrayList.size() - 1) * gap;
        minX = 0;

        if(isLoop) {
            maxX += gap / 2;
            minX -= gap / 2;
        }
    }

    public void setArrayList(List<String> list) {
        arrayList = list;
        computeBounds();
        invalidate();
    }

    public void setListener(TimePickerListener listener) {
        mListener = listener;
    }

    public TimePickerListener getListener() {
        return mListener;
    }

    private void computeScrollX() {
        if(isLoop) {
            if(scrollX > maxX) {
                scrollX = scrollX - maxX + minX;
            }
            else if(scrollX < minX) {
                scrollX = maxX - minX + scrollX;
            }
        }
        else {
            if(scrollX > maxX) {
                scrollX = maxX;
            }
            else if(scrollX < minX) {
                scrollX = minX;
            }
        }
    }

    private void computeCurrentPosition() {
        if(arrayList == null) {
            return;
        }

        position = (int)(scrollX / gap);
        if(scrollX - (position * gap) > gap / 2) {
            position ++;
        }

        position = cycleRemainder(position, arrayList.size());
    }

    public int getCurrentPosition() {
        return position;
    }

    public void setCurrentPosition(int position) {
        if(position >= 0 && position < arrayList.size()) {
            cancelTimer();
            scrollX = position * gap;
            this.position = position;
        }
    }

    private int cycleRemainder(int value, int divider) {
        int temp = value % divider;
        if(temp >= 0) {
            return temp;
        }
        else {
            return divider + temp;
        }
    }

    public void fling(float velocity) {
        cancelTimer();
        mTimer = new Timer();
        mTimer.schedule(new FlyingTimer(this, velocity * 0.01f), 0L, 5L);
    }

    public void endScroll() {
        cancelTimer();
        mTimer = new Timer();
        mTimer.schedule(new EndScrollTimer(this), 0L, 5L);
    }

    public void cancelTimer() {
        if(mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(arrayList == null) {
            return;
        }

        computeScrollX();
        computeCurrentPosition();

        final int halfNumVisible = (numVisible / 2) + 1;
        final float textAlign = mHeight / 2 - (paintText.descent() + paintText.ascent()) / 2;
        //anchor为当前position对应对象的位置
        float anchor = midPoint + (position * gap - scrollX);

        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        canvas.drawText(arrayList.get(position), anchor, textAlign, paintText);

        if(isLoop) {
            for(int i = 1; i < halfNumVisible; i++) {
                canvas.drawText(arrayList.get(cycleRemainder(position - i, arrayList.size())), anchor - (gap * i), textAlign, paintText);
                canvas.drawText(arrayList.get(cycleRemainder(position + i, arrayList.size())), anchor + (gap * i), textAlign, paintText);
            }
        }
        else {
            for(int i = 1; i < halfNumVisible; i++) {
                if(position - i >= 0) {
                    canvas.drawText(arrayList.get(position - i), anchor - (gap * i), textAlign, paintText);
                }

                if(position + i < arrayList.size()) {
                    canvas.drawText(arrayList.get(position + i), anchor + (gap * i), textAlign, paintText);
                }
            }
        }

        rectF.set(midPoint - (gap / 2), 0, midPoint + (gap / 2), mHeight);
        canvas.drawRect(rectF, paintIndicator);

        rectF.set(0,0,mWidth,mHeight);
        canvas.drawRoundRect(rectF, mRadiusOfCorner, mRadiusOfCorner, paintBG);

        canvas.restoreToCount(sc);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = mGestureDetector.onTouchEvent(event);

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelTimer();
                previousTouchX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                scrollX += previousTouchX - event.getRawX();
                previousTouchX = event.getRawX();
                break;
            case MotionEvent.ACTION_UP:
            default:
                if(!eventConsumed && event.getAction() == MotionEvent.ACTION_UP) {
                    endScroll();
                }
                break;
        }

        invalidate();
        return true;
    }
}

final class MessageHandler extends Handler {
    public static final int WHAT_INVALIDATE_VIEW = 1000;
    public static final int WHAT_END_FLING = 2000;
    public static final int WHAT_ITEM_SELECTED = 3000;

    private TimePickerView timePicker;

    public MessageHandler(TimePickerView timePicker) {
        super();
        this.timePicker = timePicker;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_INVALIDATE_VIEW:
                timePicker.invalidate();
                break;
            case WHAT_END_FLING:
                timePicker.endScroll();
                break;
            case WHAT_ITEM_SELECTED:
                if(timePicker.getListener() != null) {
                    timePicker.getListener().onSelect(timePicker.getCurrentPosition());
                }
                break;
        }
    }
}

final class TimePickerGestureListener extends GestureDetector.SimpleOnGestureListener {

    private TimePickerView timePicker;

    public TimePickerGestureListener(TimePickerView timePicker) {
        super();
        this.timePicker = timePicker;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        timePicker.cancelTimer();
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        timePicker.fling(velocityX);
        return true;
    }
}

final class FlyingTimer extends TimerTask {

    TimePickerView timePicker;
    private float velocity;
    private float acceleration;
    private float coefficient;
    //使position增加的方向为true
    private boolean direction;

    public FlyingTimer(TimePickerView timePicker, float startVelocity) {
        this.timePicker = timePicker;
        velocity = Math.abs(startVelocity);
        if(velocity > 30) {
            velocity = 30;
        }
        direction = startVelocity < 0;
        acceleration = 1;
        coefficient = 0.5f;
    }

    @Override
    public void run() {
        velocity -= acceleration * coefficient;

        if(velocity < 0) {
            timePicker.cancelTimer();
            timePicker.handler.sendEmptyMessage(MessageHandler.WHAT_END_FLING);
            return;
        }

        if(direction) {
            timePicker.scrollX += velocity;
        }
        else {
            timePicker.scrollX -= velocity;
        }

        timePicker.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_VIEW);
    }
}

final class EndScrollTimer extends TimerTask {

    TimePickerView timePicker;
    private float velocity;
    private float acceleration;
    private float startPoint;
    private float endPoint;
    private float coefficient;
    //使position增加的方向为true
    private boolean direction;

    public EndScrollTimer(TimePickerView timePicker) {
        this(timePicker, 0f);
    }

    public EndScrollTimer(TimePickerView timePicker, float startVelocity) {
        this.timePicker = timePicker;
        velocity = startVelocity;
        acceleration = 1;
        coefficient = 0.5f;
        endPoint = this.timePicker.getCurrentPosition() * this.timePicker.gap;
        startPoint = this.timePicker.scrollX;
        direction = (endPoint - startPoint) > 0;
    }

    @Override
    public void run() {
        velocity += acceleration * coefficient;

        if(direction) {
            if(startPoint > endPoint) {
                timePicker.scrollX = endPoint;
                timePicker.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED);
                timePicker.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_VIEW);
                timePicker.cancelTimer();
                return;
            }
            startPoint += velocity;
            timePicker.scrollX = startPoint;
        }
        else {
            if(startPoint < endPoint) {
                timePicker.scrollX = endPoint;
                timePicker.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED);
                timePicker.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_VIEW);
                timePicker.cancelTimer();
                return;
            }
            startPoint -= velocity;
            timePicker.scrollX = startPoint;
        }

        timePicker.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_VIEW);
    }
}