package com.leodd.oneweek.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.leodd.oneweek.BO.IPlanBO;
import com.leodd.oneweek.BO.PlanBO;
import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.CalendarDate;
import com.leodd.oneweek.Utils.DayOfWeek;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

/**
 * Created by leodd on 2016/12/30.
 * this activity is for editing plans
 *
 * several things can be done in this activity:
 *
 * 1.editing the text content
 * 2.editing the time
 * 3.set if it is a repeated plan
 * 4.providing repeat choice if it is repeated
 * 5.providing the choice of alarming
 * 6.cancel button on the upper left conner
 * 7.confirm button on the upper right conner
 * 8.delete button on the bottom(only when it is editing mode)
 *
 * notice that two mode exist in this activity:
 * 1.create mode
 * outer function should provide a argument of creation mode
 * and the activity will return a plan object and a mode number for notifying the UI to update
 *
 * 2.edit mode
 * outer function should provide a argument of edit mode
 * and the activity will return a plan object and a mode number for notifying the UI to update
 */

public class PlanEditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {


    public static final int MODE_INSERT = 0;
    public static final int MODE_UPDATE = 1;
    public static final int MODE_DELETE = 2;
    public static final int MODE_NONE = 3;

    public static final int MODE_CREATE = 0;
    public static final int MODE_EDIT = 1;

    public static final String ARG_MODE = "mode";
    public static final String ARG_PLAN_ID = "plan_id";
    public static final String ARG_IS_RECYCLE = "is_recycle";
    public static final String ARG_DATE = "date";

    private int mode;

    private Plan plan;

    private IPlanBO planBO;

    private AppCompatImageButton goBackButton;
    private AppCompatImageButton confirmButton;

    private EditText contentEditText;

    private TextView timeTextView;

    private CheckBox repeatCheckBox;

    private AppCompatCheckBox[] dayOfWeekCheckBoxes;
    private ViewGroup dayOfWeekContainer;

    private CheckBox alarmCheckBox;

    private AppCompatImageButton deleteButton;

    private CalendarDate pendingDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.plan_edit_activity);

        //getting the mode of whether to create or to edit
        mode = getIntent().getIntExtra(ARG_MODE, MODE_CREATE);

        planBO = new PlanBO(getApplicationContext());

        goBackButton = (AppCompatImageButton) findViewById(R.id.plan_edit_go_back);
        confirmButton = (AppCompatImageButton) findViewById(R.id.plan_edit_confirm);
        contentEditText = (EditText) findViewById(R.id.plan_edit_content);
        timeTextView = (TextView) findViewById(R.id.plan_edit_time);
        repeatCheckBox = (CheckBox) findViewById(R.id.plan_edit_repeat_checkbox);
        dayOfWeekContainer = (ViewGroup) findViewById(R.id.plan_edit_dayofweek_picker);
        alarmCheckBox = (CheckBox) findViewById(R.id.plan_edit_alarm_checkbox);
        deleteButton = (AppCompatImageButton) findViewById(R.id.plan_edit_delete);

        initializeContent(mode);
    }

    /**
     * initialize content according to the mode
     * @param mode depends whether it is create mode or edit mode
     */
    private void initializeContent(int mode) {
        String dateString = getIntent().getStringExtra(ARG_DATE);

        if(dateString.compareTo("") != 0) {
            CalendarDate current = new CalendarDate();
            pendingDate = new CalendarDate(dateString + " " + current.getTimeString());
        }
        else {
            pendingDate = new CalendarDate();
        }

        if(mode == MODE_CREATE) {
            //if it is create mode, that means we don't need to attaining the plan from data base
            //but instead, we have to create a new one
            plan = new Plan();
            plan.setDayOfWeek(0);
            plan.setRecycle(false);

            plan.setDate(pendingDate);
        }
        else if (mode == MODE_EDIT) {
            //if it is edit mode, use the id from the caller to search the plan
            int id = getIntent().getIntExtra(ARG_PLAN_ID, -1);
            boolean isRecycle = getIntent().getBooleanExtra(ARG_IS_RECYCLE, true);

            if(isRecycle) {
                plan = planBO.getRecyclePlanByID(id);
            }
            else {
                plan = planBO.getNormalPlanByID(id);
            }
        }

        //set content
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(MODE_NONE, null);
                finish();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        contentEditText.setText(plan.getContent());

        timeTextView.setText(plan.getDate().getTimeString());
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        PlanEditActivity.this,
                        plan.getDate().hour(),
                        plan.getDate().minute(),
                        0,
                        true
                );

                if(android.os.Build.VERSION.SDK_INT >= 23) {
                    tpd.setAccentColor(getResources().getColor(R.color.colorPrimary, null));
                }
                else {
                    tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                }
                tpd.setVersion(TimePickerDialog.Version.VERSION_1);
                tpd.show(getFragmentManager(), "TimePickerDialog");
            }
        });

        if(plan.getDayOfWeek() != 0) {
            repeatCheckBox.setChecked(true);
            dayOfWeekContainer.setVisibility(View.VISIBLE);
            initializeDayOfWeek();

            for(int i = 0; i < 7; i++) {
                dayOfWeekCheckBoxes[i].setChecked((plan.getDayOfWeek() & (1 << i)) != 0);
            }
        }
        else {
            repeatCheckBox.setChecked(false);
            dayOfWeekContainer.setVisibility(View.GONE);
        }
        repeatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    initializeDayOfWeek();
                    int dayOfWeek = plan.getDayOfWeek();

                    if(dayOfWeek == 0) {
                        dayOfWeek = DayOfWeek.WORKDAY;
                        plan.setDayOfWeek(dayOfWeek);
                    }

                    for(int i = 0; i < 7; i++) {
                        dayOfWeekCheckBoxes[i].setChecked((dayOfWeek & (1 << i)) != 0);
                    }

                    dayOfWeekContainer.setVisibility(View.VISIBLE);
                }
                else {
                    dayOfWeekContainer.setVisibility(View.GONE);
                }
            }
        });

        alarmCheckBox.setChecked(plan.isAlarm());

        if(mode == MODE_EDIT) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    planBO.removePlan(plan);
                    callBack(MODE_DELETE);
                }
            });
        }
        else {
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void initializeDayOfWeek() {
        if(dayOfWeekCheckBoxes != null) {
            return;
        }

        String[] dayOfWeekText = {"S", "M", "T", "W", "T", "F", "S"};

        dayOfWeekCheckBoxes = new AppCompatCheckBox[7];
        for(int i = 0; i < 7; i++) {
            //adding check box to the day of week picker
            dayOfWeekCheckBoxes[i] = (AppCompatCheckBox) getLayoutInflater().inflate(R.layout.dayofweek_checkbox, dayOfWeekContainer, false);
            dayOfWeekCheckBoxes[i].setText(dayOfWeekText[i]);
            final int finalI = i;
            dayOfWeekCheckBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int dayOfWeek = plan.getDayOfWeek();

                    int mask = 1 << finalI;

                    if(isChecked) {
                        dayOfWeek |= mask;
                    }
                    else {
                        dayOfWeek &= ~mask;
                    }

                    plan.setDayOfWeek(dayOfWeek);

                    if(dayOfWeek == 0) {
                        repeatCheckBox.setChecked(false);
                        dayOfWeekContainer.setVisibility(View.GONE);
                    }
                }
            });
            dayOfWeekContainer.addView(dayOfWeekCheckBoxes[i]);

            //adding spacing between check boxes
            if (i != 6) {
                dayOfWeekContainer.addView(new Space(this), new LinearLayout.LayoutParams(0, 0, 1.0f));
            }
        }
    }

    private void confirm() {
        if(contentEditText.getText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Don't forget to write your plan", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        plan.setContent(contentEditText.getText().toString());

        if(!repeatCheckBox.isChecked()) {
            plan.setDayOfWeek(0);
        }

        plan.setAlarm(alarmCheckBox.isChecked());

        if(mode == MODE_EDIT) {
            CalendarDate current = plan.getDate();

            plan.setDate(new CalendarDate(pendingDate.getDateString() + " " + current.getTimeString()));

            boolean isRecycle = plan.isRecycle();

            planBO.updatePlan(plan);

            plan.setRecycle(plan.isRecycle() || isRecycle);

            callBack(MODE_UPDATE);
        }
        else {
            planBO.addPlan(plan);
            callBack(MODE_INSERT);
        }
    }

    private void callBack(int mode) {
        Intent data = new Intent();

        data.putExtra(ARG_PLAN_ID, plan.getId());
        data.putExtra(ARG_IS_RECYCLE, plan.isRecycle());

        switch (mode) {
            case MODE_INSERT:
                setResult(MODE_INSERT, data);
                break;

            case MODE_UPDATE:
                setResult(MODE_UPDATE, data);
                break;

            case MODE_DELETE:
                setResult(MODE_DELETE, data);
                break;
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(MODE_NONE, null);
        super.onBackPressed();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        CalendarDate current = plan.getDate();

        plan.setDate(new CalendarDate(current.year(), current.month(), current.day(),
                hourOfDay, minute));

        timeTextView.setText(plan.getDate().getTimeString());
    }
}
