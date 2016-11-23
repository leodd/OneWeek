package com.leodd.oneweek.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.leodd.oneweek.R;

/**
 * Created by Leodd
 * on 2016/4/28.
 */
public class PopupList {
    public interface OnSelectListener {
        void onSelect(int position);
    }

    private static PopupList mPopupList;

    private PopupWindow mPopupWindow;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;

    private String[] mListData;
    private int numOfItemShow = 1;

    private OnSelectListener mOnSelectListener;

    private PopupList() {}

    public static PopupList getInstance() {
        if(mPopupList == null) {
            synchronized (PopupList.class) {
                mPopupList = new PopupList();
            }
        }

        return mPopupList;
    }

    /**
     * Set OnSelectListener
     * Should be called after calling show(), if admitted.
     *
     * @param listener listener
     */
    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    public void show(Context context, String[] nameOfSelection, View anchor, int xOff, int yOff) {
        mListData = nameOfSelection;

        if(mListData.length == 0) {return;}

        numOfItemShow = mListData.length;

        if(mPopupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.popup_option_of_list_item, null);

            mRecyclerView = (RecyclerView) view.findViewById(
                    R.id.recycler_view_of_popup_option);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(layoutManager);

            mAdapter = new RecyclerAdapter();
            mRecyclerView.setAdapter(mAdapter);

            int width = context.getResources().getDisplayMetrics().widthPixels -
                    convertDpToPixel(context, 16);
            mPopupWindow = new PopupWindow(view, width, convertDpToPixel(context, 50));
        }

        mAdapter.notifyDataSetChanged();

        mOnSelectListener = null;

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());

        int xPos = xOff - (mPopupWindow.getWidth() / 2);
        int yPos = - yOff - (mPopupWindow.getHeight() / 2);

        mPopupWindow.showAsDropDown(anchor, xPos, yPos);
    }

    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<ListItemHolder> {
        @Override
        public ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());

            int widthOfListItem;

            if(numOfItemShow < 4) {
                widthOfListItem = parent.getWidth() / numOfItemShow;
            }
            else {
                widthOfListItem = parent.getWidth() / 4;
            }

            textView.setLayoutParams(new RecyclerView.LayoutParams(
                    widthOfListItem,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            textView.setGravity(Gravity.CENTER);

            textView.setTextColor(Color.WHITE);

            parent.addView(textView);

            ListItemHolder holder;
            holder = new ListItemHolder(textView);

            return holder;
        }

        @Override
        public void onBindViewHolder(ListItemHolder holder, int position) {
            holder.BindData(mListData[position], position);
        }

        @Override
        public int getItemCount() {
            return mListData.length;
        }
    }

    private class ListItemHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private int position = 0;

        private int numOfTitle;

        public ListItemHolder(final View itemView) {
            super(itemView);

            numOfTitle = numOfItemShow;

            title = (TextView) itemView;

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnSelectListener != null) {
                        mOnSelectListener.onSelect(position);
                        mOnSelectListener = null;
                    }

                    if(mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            });
        }

        public void BindData(String data, int position) {
            this.title.setText(data);
            this.position = position;

            if(numOfTitle == numOfItemShow) {return;}

            numOfTitle = numOfItemShow;

            int widthOfContainer = mRecyclerView.getWidth();
            int widthOfListItem;

            if(numOfItemShow < 4) {
                widthOfListItem = widthOfContainer / numOfItemShow;
            }
            else {
                widthOfListItem = widthOfContainer / 4;
            }

            title.setLayoutParams(new RecyclerView.LayoutParams(
                    widthOfListItem,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
}
