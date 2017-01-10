package com.leodd.oneweek.UI;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.leodd.oneweek.BO.IPlanBO;
import com.leodd.oneweek.BO.PlanBO;
import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.R;
import com.leodd.oneweek.Service.AlarmServiceReceiver;
import com.leodd.oneweek.UI.DayPageUI.OneDayFragment;
import com.leodd.oneweek.Utils.CalendarDate;
import com.leodd.oneweek.Utils.CustomizeFragmentPagerAdapter;
import com.leodd.oneweek.Utils.DateStringUtil;
import com.leodd.oneweek.Widget.WeekPagerIndicator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private CalendarDate pivotDate;
    private CalendarDate currentDate;

    private ViewPager viewPager;
    private CustomizeFragmentPagerAdapter pagerAdapter;
    private WeekPagerIndicator pagerIndicator;
    private FloatingActionButton addButton;

    private TextView todoTextView;
    private TextView dateTextView;

    private IPlanBO planBO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pivotDate = new CalendarDate();
        currentDate = new CalendarDate();

        dateTextView = (TextView) findViewById(R.id.main_date);
        todoTextView = (TextView) findViewById(R.id.main_todo);

        planBO = new PlanBO(this);
        refreshTodoText();
        callAlarmService();

        addButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        pagerIndicator = (WeekPagerIndicator) findViewById(R.id.main_pager_indicator);

        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(SlidePagerAdapter.CENTER_POSITION, false);

        pagerIndicator.setViewPager(viewPager);
        pagerIndicator.setCurrentDate(pivotDate, SlidePagerAdapter.CENTER_POSITION);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneDayFragment oneDayFragment =
                        (OneDayFragment) pagerAdapter.getCurrentPrimaryItem();

                oneDayFragment.create();
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_locate:
                gotoDate(new Date());
                return true;

            case R.id.menu_calendar:
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                gotoDate(new CalendarDate(year, monthOfYear, dayOfMonth, 0, 0));
                            }
                        },
                        currentDate.year(),
                        currentDate.month(),
                        currentDate.day()
                );

                if(android.os.Build.VERSION.SDK_INT >= 23) {
                    dpd.setAccentColor(getResources().getColor(R.color.colorPrimary, null));
                }
                else {
                    dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                }
                dpd.setVersion(DatePickerDialog.Version.VERSION_1);
                dpd.show(getFragmentManager(), "DatePickerDialog");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoDate(Date date) {
        viewPager.setCurrentItem(SlidePagerAdapter.CENTER_POSITION, false);

        pivotDate = new CalendarDate(date);
        currentDate = new CalendarDate(date);

        pagerIndicator.setCurrentDate(pivotDate, SlidePagerAdapter.CENTER_POSITION);

        dateTextView.setText(DateStringUtil.getDateString(currentDate));

        pagerAdapter.update(true);
    }

    private void refreshTodoText() {
        Plan plan = planBO.getUpComingPlan();
        if(plan != null) {
            todoTextView.setVisibility(View.VISIBLE);
            todoTextView.setText(plan.getContent());
        }
        else {
            todoTextView.setVisibility(View.GONE);
        }
    }

    private void callAlarmService() {
        Intent alarmServiceIntent = new Intent(
                getApplicationContext(),
                AlarmServiceReceiver.class);
        getApplicationContext().sendBroadcast(alarmServiceIntent, null);
    }

    private class SlidePagerAdapter extends CustomizeFragmentPagerAdapter
            implements OneDayFragment.CallBackListener {

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pivotDate);

            calendar.add(Calendar.DAY_OF_MONTH, position - CENTER_POSITION);

            CalendarDate date = new CalendarDate();
            date.setTime(calendar.getTimeInMillis());

            OneDayFragment oneDayFragment = OneDayFragment.create(date);

            oneDayFragment.setCallBackListener(this);

            return oneDayFragment;
        }

        @Override
        public void onUpdate(Fragment fm, int position) {
            OneDayFragment oneDayFragment = (OneDayFragment) fm;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pivotDate);

            calendar.add(Calendar.DAY_OF_MONTH, position - CENTER_POSITION);

            CalendarDate date = new CalendarDate();
            date.setTime(calendar.getTimeInMillis());

            oneDayFragment.setDate(date);
            oneDayFragment.update();
        }

        @Override
        public void onChangeCurrentPrimaryItem(Fragment fm, int position) {
            //update the current date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pivotDate);

            calendar.add(Calendar.DAY_OF_MONTH, position - SlidePagerAdapter.CENTER_POSITION);

            currentDate.setTime(calendar.getTimeInMillis());

            //update the text
            dateTextView.setText(DateStringUtil.getDateString(currentDate));
        }

        @Override
        public void onRefresh(boolean updatePage) {
            refreshTodoText();
            callAlarmService();

            if(updatePage) {
                update(false);
            }
        }
    }
}