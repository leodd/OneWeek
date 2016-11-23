package com.leodd.oneweek.Activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.Toast;

import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.AnimationLoader;
import com.leodd.oneweek.Utils.DateObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.Calendar;

/**
 * Created by Leodd
 * on 2016/6/29.
 */
public class CopyDialog extends Dialog implements View.OnClickListener {
    public interface OnClickListener {
        void onConfirm(DateObject date);

        void onCancel();
    }

    private View mDialogView;
    private Animation mDialogShowAnim;
    private AnimationSet mDialogExitAnim;
    private Button mConfirmButton;
    private Button mCancelButton;
    private OnClickListener mOnClickListener;
    private Context mContext;
    private MaterialCalendarView widget;
    private CalendarDay calendarDay;
    private DateObject mDate;

    public CopyDialog(Context context) {
        super(context, R.style.popup_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        mContext = context;
        mDialogShowAnim = AnimationLoader.loadAnimation(context, R.anim.dialog_popup_anim);
        mDialogExitAnim = (AnimationSet) AnimationLoader.loadAnimation(context, R.anim.dialog_exit_anim);

        mDialogExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        CopyDialog.super.dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_copy);

        setCanceledOnTouchOutside(true);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mConfirmButton = (Button) findViewById(R.id.button_confirm_copy);
        mConfirmButton.setOnClickListener(this);
        widget = (MaterialCalendarView) findViewById(R.id.calendarView_copy);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        widget.setTitleFormatter(new MonthArrayTitleFormatter(getContext().getResources().getTextArray(R.array.custom_months)));
        widget.setWeekDayFormatter(new ArrayWeekDayFormatter(getContext().getResources().getTextArray(R.array.custom_weekdays)));
        widget.state().edit().setFirstDayOfWeek(Calendar.MONDAY).commit();
    }

    @Override
    protected void onStart() {
        mDialogView.startAnimation(mDialogShowAnim);

        widget.setCurrentDate(calendarDay);
        widget.setSelectedDate(calendarDay);
    }

    public CopyDialog setDate(DateObject date) {
        if(date != null) {
            mDate = date;
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.YEAR, date.getYear());
            instance.set(Calendar.MONTH, date.getMonth() - 1);
            instance.set(Calendar.DAY_OF_MONTH, date.getDay());
            calendarDay = CalendarDay.from(instance);
        }
        return this;
    }

    public CopyDialog setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;

        return this;
    }

    @Override
    public void cancel() {
        mOnClickListener.onCancel();
        dismissWithAnimation();
    }

    private void dismissWithAnimation() {
        mDialogView.startAnimation(mDialogExitAnim);
    }

    private boolean assembleCalendarDay() {
        if(widget.getSelectedDate() == null) {
            return false;
        }

        calendarDay = widget.getSelectedDate();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_copy:
                if (mOnClickListener != null) {
                    if (assembleCalendarDay()) {
                        int year = calendarDay.getYear();
                        int month = calendarDay.getMonth() + 1;
                        int day = calendarDay.getDay();

                        DateObject date = new DateObject(year, month, day);
                        if(mDate != null && mDate.isEqualTo(date)) {
                            Toast.makeText(getContext(), "不能复制到当前日期", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mOnClickListener.onConfirm(date);
                        dismissWithAnimation();
                    }
                }
                else {
                    dismissWithAnimation();
                }
                break;

            default: break;
        }
    }
}
