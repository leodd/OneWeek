package com.leodd.oneweek.Activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.leodd.oneweek.Beans.PlanBean;
import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.AnimationLoader;
import com.leodd.oneweek.Utils.DayType;
import com.leodd.oneweek.Utils.StringUtil;
import com.leodd.oneweek.Utils.TimeObject;
import com.leodd.oneweek.Views.TimePickerView;
import com.leodd.oneweek.Views.WeekView;

/**
 * Created by Leodd
 * on 2016/5/2.
 */
public class NewPlanDialog extends Dialog implements View.OnClickListener {
    public interface OnClickListener {
        void onConfirm(PlanBean item);

        void onCancel();
    }

    private View mDialogView;
    private Animation mDialogShowAnim;
    private AnimationSet mDialogExitAnim;
    private EditText mContentText;
    private TimePickerView mHourPicker;
    private RadioButton mRBMin00;
    private RadioButton mRBMin15;
    private RadioButton mRBMin30;
    private RadioButton mRBMin45;
    private Button mConfirmButton;
    private Button mCancelButton;
    private WeekView mWeekView;
    private CheckBox mCBIsAlarm;
    private OnClickListener mOnClickListener;
    private PlanBean mPlanBean;
    private Context mContext;
    private int hour;
    private int minute;

    private boolean isRecycle = false;
    private int dayOfRecycle = DayType.MONDAY |
            DayType.TUESDAY |
            DayType.WEDNESDAY |
            DayType.THURSDAY |
            DayType.FRIDAY |
            DayType.SATURDAY |
            DayType.SUNDAY;

    public NewPlanDialog(Context context) {
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
                        NewPlanDialog.super.dismiss();
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
        setContentView(R.layout.dialog_new_plan);

        setCanceledOnTouchOutside(true);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mContentText = (EditText) findViewById(R.id.content_text);
        mHourPicker = (TimePickerView) findViewById(R.id.time_picker_hour_picker);
        mRBMin00 = (RadioButton) findViewById(R.id.radio_button_00min);
        mRBMin15 = (RadioButton) findViewById(R.id.radio_button_15min);
        mRBMin30 = (RadioButton) findViewById(R.id.radio_button_30min);
        mRBMin45 = (RadioButton) findViewById(R.id.radio_button_45min);
        mConfirmButton = (Button) findViewById(R.id.button_confirm);
        mConfirmButton.setOnClickListener(this);
        mWeekView = (WeekView) findViewById(R.id.week_view_selector);
        mCBIsAlarm = (CheckBox) findViewById(R.id.check_box_is_alarm);

        mHourPicker.setArrayList(StringUtil.seriesCreater(0, 24));
    }

    @Override
    protected void onStart() {
        mDialogView.startAnimation(mDialogShowAnim);

        if(mPlanBean != null) {
            mContentText.setText(mPlanBean.getContent());

            hour = mPlanBean.getTime().getHour();
            minute = mPlanBean.getTime().getMinute();

            mHourPicker.setCurrentPosition(hour);
            if(minute < 15) {
                minute = 0;
                mRBMin00.setChecked(true);
            }
            else if(minute < 30) {
                minute = 15;
                mRBMin15.setChecked(true);
            }
            else if(minute < 45) {
                minute = 30;
                mRBMin30.setChecked(true);
            }
            else if(minute < 60) {
                minute = 45;
                mRBMin45.setChecked(true);
            }

            if(mPlanBean.isRecycle()) {
                isRecycle = true;
                mWeekView.setWeek(mPlanBean.getWeek());
            }
            else {
                isRecycle = false;
            }

            if(mPlanBean.isAlarm()) {
                mCBIsAlarm.setChecked(true);
            }
            else {
                mCBIsAlarm.setChecked(false);
            }
        }
        else {
            mHourPicker.setCurrentPosition(12);
            mWeekView.setWeek(dayOfRecycle);
        }

        if(isRecycle) {
            mWeekView.setVisibility(View.VISIBLE);
        }
        else {
            mWeekView.setVisibility(View.GONE);
        }
    }

    public NewPlanDialog setPlanBean(PlanBean item) {
        if(item != null) {
            mPlanBean = item;
        }
        return this;
    }

    public NewPlanDialog setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;

        return this;
    }

    public NewPlanDialog setRecycle(boolean flag) {
        isRecycle = flag;

        return this;
    }

    public NewPlanDialog setDayOfRecycle(int week) {
        dayOfRecycle = week;

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

    private boolean assemblePlanBean() {
        if(mPlanBean == null) {
            mPlanBean = new PlanBean();
        }

        hour = mHourPicker.getCurrentPosition();
        if(mRBMin00.isChecked()) {
            minute = 0;
        }
        else if(mRBMin15.isChecked()) {
            minute = 15;
        }
        else if(mRBMin30.isChecked()) {
            minute = 30;
        }
        else if(mRBMin45.isChecked()) {
            minute = 45;
        }

        TimeObject time = new TimeObject(hour, minute);
        mPlanBean.setTime(time);

        if(isRecycle) {
            mPlanBean.setRecycle(true);

            if((mWeekView.getWeek() & DayType.MASK) == 0) {
                Toast.makeText(mContext,"循环为空~",Toast.LENGTH_SHORT).show();
                return false;
            }

            mPlanBean.setWeek(mWeekView.getWeek());
        }
        else {
            mPlanBean.setRecycle(false);
        }

        if(mCBIsAlarm.isChecked()) {
            mPlanBean.setAlarm(true);
        }
        else {
            mPlanBean.setAlarm(false);
        }

        String contentStr = mContentText.getText().toString();
        if (contentStr.equals("")) {
            Toast.makeText(mContext,"内容为空，找不到对象，找不到对象~",Toast.LENGTH_SHORT).show();
            return false;
        }
        mPlanBean.setContent(contentStr);

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm:
                if (mOnClickListener != null) {
                    if (assemblePlanBean()) {
                        mOnClickListener.onConfirm(mPlanBean);
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
