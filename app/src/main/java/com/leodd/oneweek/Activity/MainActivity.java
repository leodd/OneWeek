package com.leodd.oneweek.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.leodd.oneweek.Beans.PlanBean;
import com.leodd.oneweek.Fragment.FragmentRecyclerList;
import com.leodd.oneweek.R;
import com.leodd.oneweek.Service.NotificationService;
import com.leodd.oneweek.Utils.DateObject;
import com.leodd.oneweek.Utils.DateUtil;
import com.leodd.oneweek.Utils.FastBlur;
import com.leodd.oneweek.Utils.FragmentStatePagerAdapter;
import com.leodd.oneweek.Utils.ImageFactory;
import com.leodd.oneweek.Utils.IniUtil;
import com.leodd.oneweek.Utils.WeekUtil;
import com.leodd.oneweek.Views.WeekPagerIndicator;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, AppBarLayout.OnOffsetChangedListener {
    private ViewPager mViewPager;
    private WeekPagerAdapter mAdapter;
    private DateObject mDateOfCurrentPage;
    private Button mButtonAddNormalPlan;
    private Button mButtonAddRecyclePlan;
    private AppBarLayout mAppBarLayout;
    private WeekPagerIndicator mWeekPagerIndicator;
    private ImageView mMainImage;
    private ImageView mMainImageBlur;
    private Bitmap image;
    private Bitmap image_compress;
    private Bitmap image_blur;
    private MainHandler mHandler;
    private ImageFactory mImageFactory;
    private boolean mRefreshFlag = true;
    private boolean isImageBlurVisible = false;

    private final int MAX_PAGE_NUM = 300;
    private final int MED_PAGE_INDEX = MAX_PAGE_NUM / 2 - 1;
    private final float PERCENTAGE_TO_HIDE_IMAGE_BLUR = 0.5f;
    private final int ALPHA_ANIMATIONS_DURATION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, NotificationService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("NOTIFICATION_SERVICE_CONTROLLER", 1);
        i.putExtras(bundle);
        startService(i);

        IniUtil.fistTimeInitialize(this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager_week);

        mAdapter = new WeekPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);

        mWeekPagerIndicator = (WeekPagerIndicator) findViewById(R.id.week_pager_indicator);

        mDateOfCurrentPage = DateUtil.getToday();

        mViewPager.setCurrentItem(MED_PAGE_INDEX);

        mWeekPagerIndicator.setViewPager(mViewPager);
        mWeekPagerIndicator.setCurrentDate(mDateOfCurrentPage, MED_PAGE_INDEX);

        mButtonAddNormalPlan = (Button) findViewById(R.id.button_add_normal_plan);
        mButtonAddRecyclePlan = (Button) findViewById(R.id.button_add_recycle_plan);

        mButtonAddNormalPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlan(false);
            }
        });

        mButtonAddRecyclePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlan(true);
            }
        });

        mMainImage = (ImageView) findViewById(R.id.main_image);
        mMainImageBlur = (ImageView) findViewById(R.id.main_image_blur);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_app_bar);
        mAppBarLayout.addOnOffsetChangedListener(this);

        setImage();
    }

    private void setImage() {

        mImageFactory = new ImageFactory();

        mHandler = new MainHandler();

        Runnable run1 = new Runnable() {
            @Override
            public void run() {
                Resources r = getResources();
                InputStream is = r.openRawResource(R.raw.test_pic);
                image = new BitmapDrawable(getResources(), is).getBitmap();

                image_compress = mImageFactory.ratio(image, 534, 300);
                mHandler.sendEmptyMessage(2);

                image_blur = mImageFactory.ratio(image_compress, 534/8, 300/8);
                image_blur = FastBlur.doBlur(image_blur, 5, false);
                mHandler.sendEmptyMessage(1);

                image.recycle();
                mImageFactory = null;
            }
        };

        new Thread(run1).start();
    }

    private class WeekPagerAdapter extends FragmentStatePagerAdapter {

        public WeekPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            FragmentRecyclerList fr = new FragmentRecyclerList();
            DateObject offsetDay = DateUtil.getOffsetDate(mDateOfCurrentPage, position - MED_PAGE_INDEX);

            fr.setDate(offsetDay);

            return fr;
        }

        @Override
        public int getCount() {
            return MAX_PAGE_NUM;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        FragmentRecyclerList fr = (FragmentRecyclerList) mAdapter.getItemByPosition(position);

        if(fr == null) {
            return;
        }

        if(mRefreshFlag) {
            fr.refreshData();

            mRefreshFlag = false;
        }

        fr.setDataChangeListener(new FragmentRecyclerList.OnDataChangeListener() {
            @Override
            public void onDataChange() {
                mRefreshFlag = true;
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnImageBlur(percentage);
    }

    private void handleAlphaOnImageBlur(float percentage) {
        if (percentage <= PERCENTAGE_TO_HIDE_IMAGE_BLUR) {
            if(isImageBlurVisible) {
                startAlphaAnimation(mMainImageBlur, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isImageBlurVisible = false;
            }

        } else {

            if (!isImageBlurVisible) {
                startAlphaAnimation(mMainImageBlur, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isImageBlurVisible = true;
            }
        }
    }

    private void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void newPlan(boolean isRecycle) {
        DateObject date = DateUtil.getOffsetDate(mDateOfCurrentPage, mViewPager.getCurrentItem() - MED_PAGE_INDEX);

        new NewPlanDialog(MainActivity.this)
                .setRecycle(isRecycle)
                .setDayOfRecycle(WeekUtil.getWeekByDate(date))
                .setOnClickListener(new NewPlanDialog.OnClickListener() {
                    @Override
                    public void onConfirm(PlanBean item) {
                        FragmentRecyclerList fr = (FragmentRecyclerList) mAdapter.getCurrentPrimaryItem();

                        if(fr != null) {
                            fr.addPlan(item);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show();
    }


    private class MainHandler extends Handler {
        public final int NOTIFY_BLUR_IMAGE_CHANGE = 1;
        public final int NOTIFY_MAIN_IMAGE_CHANGE = 2;

        public MainHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what) {
                case NOTIFY_BLUR_IMAGE_CHANGE:
                    if(image_blur == null) {
                        return;
                    }
                    mMainImageBlur.setImageBitmap(image_blur);
                    return;
                case NOTIFY_MAIN_IMAGE_CHANGE:
                    if(image_compress == null) {
                        return;
                    }
                    mMainImage.setImageBitmap(image_compress);
                    return;
            }
        }
    }
}