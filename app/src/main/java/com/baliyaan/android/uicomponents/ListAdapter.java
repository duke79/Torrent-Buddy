package com.baliyaan.android.uicomponents;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/31/2017.
 */

public abstract class ListAdapter extends BaseAdapter{
    private final int mItemLayoutID;
    private Context mContext = null;
    private ArrayList<Object> mItemsList = null;

    public ListAdapter(Context context, ArrayList<Object> itemsList, int itemLayoutID)
    {
        mContext = context;
        mItemsList = itemsList;
        mItemLayoutID = itemLayoutID;
    }

    @Override
    public int getCount() {
        int count = 0;
        if(null != mItemsList)
            count = mItemsList.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        return mItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View resultView = view;
        if(resultView == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            resultView = layoutInflater.inflate(mItemLayoutID, viewGroup, false);
            Object holder = createViewHolder(position,resultView);
            resultView.setTag(holder);
        }
        Object holder = resultView.getTag();
        setViewHolderParams(position,holder);
        return resultView;
    }

    protected abstract Object createViewHolder(int position, View view);

    protected abstract void setViewHolderParams(int position, Object holder);

    protected Context context(){
        return mContext;
    }

    protected ArrayList<Object> itemsList(){
        return mItemsList;
    }
}
