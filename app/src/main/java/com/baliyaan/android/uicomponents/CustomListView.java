package com.baliyaan.android.uicomponents;

import android.content.Context;
import android.support.v7.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by Pulkit Singh on 2/4/2017.
 */

public class CustomListView extends ListViewCompat {

    public CustomListView(Context context) {
        super(context);
        InitCustomListView();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitCustomListView();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitCustomListView();
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    private void InitCustomListView(){
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                Log.d("CustomListView", String.valueOf(i));
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Log.d("CustomListView", String.valueOf(i));
            }
        });
    }
}
