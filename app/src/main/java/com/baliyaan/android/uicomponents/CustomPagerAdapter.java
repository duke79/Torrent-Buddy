package com.baliyaan.android.uicomponents;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 10/15/2016.
 */

public class CustomPagerAdapter extends PagerAdapter{
    private Context mContext = null;
    private ArrayList<View> mViewList = new ArrayList<>();
    private ArrayList<String> mPageTitles = new ArrayList<>();

    public CustomPagerAdapter(Context context, ArrayList<View> viewList, ArrayList<String> pageTitles)
    {
        mContext = context;
        mViewList.addAll(viewList);
        mPageTitles.addAll(pageTitles);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final View itemView = mViewList.get(position);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = (ViewGroup) itemView.getParent();
                if(parent!=null)
                {
                    parent.removeView(itemView);
                }
                container.addView(itemView);
            }
        };
        handler.post(myRunnable);
        return itemView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}