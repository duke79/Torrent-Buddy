package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ListViewCompat;
import android.view.View;

import com.baliyaan.android.login.Event;
import com.baliyaan.android.login.Services;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class VideoListPresenter {
    private final Context mContext;
    private final String TAG = "VideoListPresenter";
    private final Services mLoginServices;
    private final SwipeRefreshLayout mSwipeRefreshVieoList;
    ListViewCompat mVideoList = null;
    VideoListAdapter mVideoListAdapter = null;
    ArrayList<String> mVideos = new ArrayList<>();

    public VideoListPresenter(Context context, View view, Services loginServices, SwipeRefreshLayout swipeRefreshVieoList) {
        mContext = context;
        mLoginServices = loginServices;
        mSwipeRefreshVieoList = swipeRefreshVieoList;
        setupVideoList(view);
        MainActivity.bus.register(this);
    }

    public void setupVideoList(View view) {
        mVideoList = (ListViewCompat) view;//(ListViewCompat) view.findViewById(R.id.Videos);
        mVideoListAdapter = new VideoListAdapter(mContext, (ArrayList<Object>)(ArrayList<?>)mVideos, R.layout.video);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mVideoList.setAdapter(mVideoListAdapter);
            }
        };
        handler.post(myRunnable);
    }

    @Subscribe
    public void OnVideosListUpdated(Event.User.VideoListUpdated event) {
        if(event.videos==null) return;
        if(event.videos.size()==0) return;
        ArrayList<String> videos = event.videos;
        if (mVideos.containsAll(videos)) return;
        mVideos.clear();
        mVideos.addAll(videos);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mVideoListAdapter.notifyDataSetChanged();
                if(mSwipeRefreshVieoList!=null)
                    mSwipeRefreshVieoList.setRefreshing(false);
            }
        };
        handler.post(myRunnable);
    }

    public void refreshVideoList(){
        mVideos.clear();
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mVideoListAdapter.notifyDataSetChanged();
            }
        };
        handler.post(myRunnable);
        mLoginServices.requestUpdateUserData();
    }
}
