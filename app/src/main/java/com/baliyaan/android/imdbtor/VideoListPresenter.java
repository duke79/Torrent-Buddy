package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.ListViewCompat;
import android.view.View;

import com.baliyaan.android.login.Event;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class VideoListPresenter {
    private final Context mContext;
    private final String TAG = "VideoListPresenter";
    ListViewCompat mVideoList = null;
    VideoListAdapter mVideoListAdapter = null;
    ArrayList<String> mVideos = new ArrayList<>();

    public VideoListPresenter(Context context, View view) {
        mContext = context;
        setupVideoList(view);
        MainActivity.bus.register(this);
    }

    public void setupVideoList(View view) {
        mVideoList = (ListViewCompat) view;//(ListViewCompat) view.findViewById(R.id.Videos);
        mVideoListAdapter = new VideoListAdapter(mContext, mVideos);
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
            }
        };
        handler.post(myRunnable);
    }
}
