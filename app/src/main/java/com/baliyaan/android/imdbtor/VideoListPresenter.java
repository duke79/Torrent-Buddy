package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.ListViewCompat;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/28/2017.
 */

public class VideoListPresenter {
    private final Context mContext;
    ListViewCompat mVideoList = null;
    VideoListAdapter mVideoListAdapter = null;
    ArrayList<String> mVideos = new ArrayList<>();

    public VideoListPresenter(Context context, View view) {
        mContext = context;
        setupVideoList(view);
    }

    public void setupVideoList(View view) {
        mVideoList = (ListViewCompat) view.findViewById(R.id.Videos);
        mVideoListAdapter = new VideoListAdapter(mContext, mVideos);
        mVideoList.setAdapter(mVideoListAdapter);
    }

    public void OnVideosListUpdated(ArrayList<String> videos) {
        if (mVideos == null) return;
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
