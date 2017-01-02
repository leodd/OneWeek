package com.leodd.oneweek.UI.DayPageUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leodd.oneweek.BO.IPlanBO;
import com.leodd.oneweek.BO.PlanBO;
import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.R;
import com.leodd.oneweek.UI.PlanEditActivity;
import com.leodd.oneweek.Utils.CalendarDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leodd on 2016/12/21.
 * this class implements the operation for plans management
 */

public class OneDayFragment extends Fragment {

    public interface CallBackListener {
        void onRefresh(boolean updatePage);
    }

    private List<Plan> plans;

    private CalendarDate calendarDate;

    private IPlanBO planBO;

    private CallBackListener mListener;

    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private View mImageNone;

    //pending position is responsible for storing the position of the item
    //which is being editing
    private int pendingPosition;

    public static final String ARG_DATE = "date";

    private final int REQUEST_CODE = 40;

    public static OneDayFragment create(@NonNull CalendarDate date) {
        OneDayFragment fm = new OneDayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date.getDateString() + " 00:00");
        fm.setArguments(args);
        return fm;
    }

    public OneDayFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dateString = getArguments().getString(ARG_DATE);
        calendarDate = new CalendarDate(dateString);

        planBO = new PlanBO(getContext());
        plans = new ArrayList<>();

        pendingPosition = -1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.one_day_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.one_day_recycler_view);
        mImageNone = view.findViewById(R.id.one_day_image_nothing);

        adapter = new PlanAdapter(getContext(), plans);
        adapter.setOnClickListener(new PlanAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                pendingPosition = position;

                Intent intent = new Intent(getActivity(), PlanEditActivity.class);

                //pop up the plan edit panel, and set it to the create mode
                intent.putExtra(PlanEditActivity.ARG_MODE, PlanEditActivity.MODE_EDIT);
                intent.putExtra(PlanEditActivity.ARG_DATE, calendarDate.getDateString());
                intent.putExtra(PlanEditActivity.ARG_PLAN_ID, plans.get(position).getId());
                intent.putExtra(PlanEditActivity.ARG_IS_RECYCLE, plans.get(position).isRecycle());

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PlanDivider divider = new PlanDivider(null, LinearLayoutManager.VERTICAL);
        divider.setHeight(10);
        recyclerView.addItemDecoration(divider);

        update();
    }

    public void setCallBackListener(CallBackListener listener) {
        mListener = listener;
    }

    public void update() {
        plans.clear();
        plans.addAll(planBO.getPlanByDate(calendarDate));

        setImageNone();
        adapter.notifyDataSetChanged();
    }

    private void setImageNone() {
        if(plans.size() > 0) {
            mImageNone.setVisibility(View.GONE);
        }
        else {
            mImageNone.setVisibility(View.VISIBLE);
        }
    }

    public void setDate(CalendarDate date) {
        calendarDate = date;
    }

    public void create() {
        Intent intent = new Intent(getActivity(), PlanEditActivity.class);

        //pop up the plan edit panel, and set it to the create mode
        intent.putExtra(PlanEditActivity.ARG_MODE, PlanEditActivity.MODE_CREATE);
        intent.putExtra(PlanEditActivity.ARG_DATE, calendarDate.getDateString());

        startActivityForResult(intent, REQUEST_CODE);
    }

    public void insert(int id, boolean isRecycle) {
        //attain plan from the data base
        Plan plan;

        if(isRecycle) {
            plan = planBO.getRecyclePlanByID(id);
        }
        else {
            plan = planBO.getNormalPlanByID(id);
        }

        if(plan.isRecycle() && (plan.getDayOfWeek() & (1 << calendarDate.dayOfWeek())) == 0) {
            return;
        }

        //insert the plan to the list
        int index = plans.size();

        while(index > 0) {
            if(plan.compareTo(plans.get(index - 1)) >= 0) {
                break;
            }

            index--;
        }

        plans.add(index, plan);

        setImageNone();
        adapter.notifyItemInserted(index);
        recyclerView.scrollToPosition(index);
    }

    public void remove(int position) {
        plans.remove(position);

        setImageNone();
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != REQUEST_CODE || resultCode == PlanEditActivity.MODE_NONE) {
            return;
        }

        int id = data.getIntExtra(PlanEditActivity.ARG_PLAN_ID, -1);
        boolean isRecycle = data.getBooleanExtra(PlanEditActivity.ARG_IS_RECYCLE, true);

        if(resultCode == PlanEditActivity.MODE_INSERT) {
            insert(id, isRecycle);
        }
        else if(resultCode == PlanEditActivity.MODE_UPDATE && pendingPosition != -1) {
            plans.remove(pendingPosition);
            adapter.notifyItemRemoved(pendingPosition);

            insert(id, isRecycle);
        }
        else if(resultCode == PlanEditActivity.MODE_DELETE && pendingPosition != -1) {
            plans.remove(pendingPosition);
            adapter.notifyItemRemoved(pendingPosition);
        }

        if(mListener != null) {
            mListener.onRefresh(isRecycle);
        }

        pendingPosition = -1;
    }
}
