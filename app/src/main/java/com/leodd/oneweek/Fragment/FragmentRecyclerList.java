package com.leodd.oneweek.Fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leodd.oneweek.Activity.CopyDialog;
import com.leodd.oneweek.Activity.NewPlanDialog;
import com.leodd.oneweek.BO.PlanBeanBO;
import com.leodd.oneweek.Beans.PlanBean;
import com.leodd.oneweek.R;
import com.leodd.oneweek.Service.NotificationService;
import com.leodd.oneweek.Utils.DateObject;
import com.leodd.oneweek.Utils.PopupList;
import com.leodd.oneweek.Utils.WeekUtil;
import com.leodd.oneweek.Views.WeekView;


import java.util.List;

/**
 * Created by leodd
 * on 2016/4/17.
 */
public class FragmentRecyclerList extends Fragment{
    RecyclerView mRecyclerView;
    RecyclerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    FragmentHandler mHandler;
    OnDataChangeListener mListener;
    private View mImageNothing;

    int mWeek;
    DateObject mDate = new DateObject(0, 0, 0);

    PlanBeanBO planBeanBO;
    List<PlanBean> mListData;

    public interface OnDataChangeListener {
        void onDataChange();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_recycler_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mImageNothing = rootView.findViewById(R.id.image_nothing);

        mHandler = new FragmentHandler();

        planBeanBO = new PlanBeanBO(getActivity());

        setDate(mDate);

        return rootView;
    }

    public void setDate(DateObject date) {
        mDate = date;

        if(planBeanBO == null){
            return;
        }

        mWeek = WeekUtil.getWeekByDate(mDate.getYear(), mDate.getMonth(), mDate.getDay());

        mListData = planBeanBO.getPlanBeansByDate(mDate.getYear(), mDate.getMonth(), mDate.getDay());

        refreshRecyclerList();
    }

    public void refreshData() {
        setDate(mDate);
    }

    private void refreshRecyclerList() {
        mHandler.sendEmptyMessage(FragmentHandler.REFRESH_RECYCLER_LIST);

        showImageNothing();
    }

    private void showImageNothing() {
        if(mListData.size() == 0) {
            mImageNothing.setVisibility(View.VISIBLE);
        }
        else {
            mImageNothing.setVisibility(View.GONE);
        }
    }

    public void setDataChangeListener(OnDataChangeListener listener) {
        mListener = listener;
    }

    /**
     * 添加计划
     * 添加计划到数据表及ui
     */
    public void addPlan(PlanBean item) {
        if(planBeanBO == null || mListData == null) {
            Log.e("FragmentRecyclerList", "addPlan()");
            return;
        }

        item.setDate(mDate);

        long id = planBeanBO.addPlanBeanToDataBase(item);

        item.setId(id);

        if((mWeek & item.getWeek()) == 0) {
            return;
        }

        mAdapter.addItem(item);

        showImageNothing();

        if(item.isRecycle() && mListener != null) {
            mListener.onDataChange();
        }

        Intent i = new Intent(getContext(), NotificationService.class);
        i.putExtra("NOTIFICATION_SERVICE_CONTROLLER", 1);
        getContext().startService(i);
    }

    /**
     * 清除指定数据
     * 从数据表和ui中清除指定数据
     *
     * @param item PlanBean实例，为ui中要删除的元素
     */
    public void removePlan(PlanBean item) {
        if(planBeanBO == null || mListData == null) {
            Log.e("FragmentRecyclerList", "removePlan()");
            return;
        }

        if(item.isRecycle() && mListener != null) {
            mListener.onDataChange();
        }

        planBeanBO.removePlanBeanFromDataBase(item);

        mAdapter.removeItem(mListData.indexOf(item));

        showImageNothing();
    }

    public void editPlan(final PlanBean item) {
        new NewPlanDialog(getContext())
                .setPlanBean(item)
                .setOnClickListener(new NewPlanDialog.OnClickListener() {
                    @Override
                    public void onConfirm(PlanBean item_new) {
                        removePlan(item);
                        addPlan(item_new);
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show();
    }

    public void copyPlan(PlanBean item) {
        final PlanBean item_new = new PlanBean();
        item_new.setRecycle(false);
        item_new.setTime(item.getTime());
        item_new.setContent(item.getContent());
        item_new.setAlarm(item.isAlarm());

        new CopyDialog(getContext())
                .setDate(mDate)
                .setOnClickListener(new CopyDialog.OnClickListener() {
                    @Override
                    public void onConfirm(DateObject date) {
                        item_new.setDate(date);
                        planBeanBO.addPlanBeanToDataBase(item_new);
                        if(mListener != null) {
                            mListener.onDataChange();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show();
    }

    private void showPopupOption(View anchor, final PlanBean item) {
        PopupList popupList = PopupList.getInstance();

        if(item.isRecycle()) {
            popupList.show(getActivity(),
                    new String[] {"编辑","删除"},
                    anchor,
                    anchor.getWidth() / 2,
                    anchor.getHeight() / 2);

            popupList.setOnSelectListener(new PopupList.OnSelectListener() {
                @Override
                public void onSelect(int position) {
                    switch (position) {
                        case 0: editPlan(item);
                            break;
                        case 1: removePlan(item);
                            break;
                        default: break;
                    }
                }
            });
        }
        else {
            popupList.show(getActivity(),
                    new String[] {"编辑","复制至","删除"},
                    anchor,
                    anchor.getWidth() / 2,
                    anchor.getHeight() / 2);

            popupList.setOnSelectListener(new PopupList.OnSelectListener() {
                @Override
                public void onSelect(int position) {
                    switch (position) {
                        case 0: editPlan(item);
                            break;
                        case 1: copyPlan(item);
                            break;
                        case 2: removePlan(item);
                            break;
                        default: break;
                    }
                }
            });
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<ListItemHolder> {

        @Override
        public ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.list_item, parent, false);

            ListItemHolder holder;
            holder = new ListItemHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(ListItemHolder holder, int position) {
            PlanBean data;

            data = mListData.get(position);
            holder.BindData(data);
        }

        @Override
        public int getItemCount() {
            if(mListData == null) {
                return 0;
            }

            return mListData.size();
        }

        public void addItem(PlanBean item) {
            int position = planBeanBO.insertByTime(item, mListData);
            notifyItemInserted(position);
        }

        public void removeItem(int position) {
            mListData.remove(position);
            notifyItemRemoved(position);
        }
    }

    private class ListItemHolder extends RecyclerView.ViewHolder {
        private View mRootView;
        private TextView mTime;
        private TextView mContent;
        private WeekView mWeekView;

        private PlanBean mData;

        public ListItemHolder(final View itemView) {
            super(itemView);

            mRootView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupOption(mRootView, mData);
                }
            });

            mTime = (TextView) mRootView.findViewById(R.id.list_item_time);
            mContent = (TextView) mRootView.findViewById(R.id.list_item_content);
            mWeekView = (WeekView) mRootView.findViewById(R.id.week_view_of_list_item);
        }

        public void BindData(PlanBean data) {
            mData = data;

            String timeStr = mData.getTime().getHour() + "时" + mData.getTime().getMinute() + "分";
            mTime.setText(timeStr);
            mContent.setText(mData.getContent());
            if (data.isRecycle()) {
                mWeekView.setVisibility(View.VISIBLE);
                mWeekView.setWeek(data.getWeek());
            }
            else {
                mWeekView.setVisibility(View.GONE);
            }
        }
    }

    private class FragmentHandler extends Handler {
        public static final int REFRESH_RECYCLER_LIST = 1;

        public FragmentHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what) {
                case REFRESH_RECYCLER_LIST:
                    mAdapter.notifyDataSetChanged();
                    return;
            }
        }
    }
}
