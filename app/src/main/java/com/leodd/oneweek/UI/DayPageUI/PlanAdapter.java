package com.leodd.oneweek.UI.DayPageUI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.DayOfWeek;

import java.util.Date;
import java.util.List;

/**
 * Created by leodd on 2016/12/21.
 * the adapter for recycler view
 */

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(int position);
    }

    private List<Plan> plans;

    private Context mContext;

    private OnClickListener mListener;

    public PlanAdapter(Context context, List<Plan> plans) {
        this.mContext = context;
        this.plans = plans;
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View planView = layoutInflater.inflate(R.layout.plan_item, parent, false);

        ViewHolder planViewHolder = new ViewHolder(planView);

        return planViewHolder;
    }

    private String createWeekString(int dayOfWeek) {
        StringBuilder sb = new StringBuilder();

        if((dayOfWeek & DayOfWeek.SUN) != 0) {
            sb.append(" , SUN");
        }
        if((dayOfWeek & DayOfWeek.MON) != 0) {
            sb.append(" , MON");
        }
        if((dayOfWeek & DayOfWeek.TUE) != 0) {
            sb.append(" , TUE");
        }
        if((dayOfWeek & DayOfWeek.WED) != 0) {
            sb.append(" , WED");
        }
        if((dayOfWeek & DayOfWeek.THU) != 0) {
            sb.append(" , THU");
        }
        if((dayOfWeek & DayOfWeek.FRI) != 0) {
            sb.append(" , FRI");
        }
        if((dayOfWeek & DayOfWeek.SAT) != 0) {
            sb.append(" , SAT");
        }

        if(sb.length() < 1) {
            return "";
        }

        sb.delete(0, 3);

        return sb.toString();
    }

    private float computeAlpha(Date date) {
        Date now = new Date();
        long diff = now.getTime() - date.getTime();

        if(diff < 0) {
            return 0f;
        }

        float alpha;

        alpha = 1.0f - (float)diff / 10000000f;

        if(alpha < 0.2f || alpha > 1.0f) {
            return 0.2f;
        }

        return alpha;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Plan plan = plans.get(position);

        TextView content = holder.content;
        TextView dayOfWeek = holder.dayOfWeek;
        TextView time = holder.time;
        View indicator = holder.indicator;
        View alarmIndicator = holder.alarmIndicator;

        content.setText(plan.getContent());

        if(plan.getDayOfWeek() == 0) {
            dayOfWeek.setVisibility(View.GONE);
        }
        else {
            dayOfWeek.setText(createWeekString(plan.getDayOfWeek()));
            dayOfWeek.setVisibility(View.VISIBLE);
        }

        time.setText(plan.getDate().getTimeString());

        float alpha;
        alpha = computeAlpha(plan.getDate());
        indicator.setAlpha(alpha);

        if(plan.isAlarm()) {
            alarmIndicator.setVisibility(View.VISIBLE);
        }
        else {
            alarmIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView content;
        public TextView dayOfWeek;
        public TextView time;
        public View indicator;
        public View alarmIndicator;

        public ViewHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.plan_item_content);
            dayOfWeek = (TextView) itemView.findViewById(R.id.plan_item_week);
            time = (TextView) itemView.findViewById(R.id.plan_item_time);
            indicator = itemView.findViewById(R.id.plan_item_indicator);
            alarmIndicator = itemView.findViewById(R.id.plan_item_alarm_indicator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        int position = getAdapterPosition(); // gets item position
                        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted
                            mListener.onClick(position);
                        }
                    }
                }
            });
        }
    }
}
