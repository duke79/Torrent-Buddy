package com.baliyaan.android.imdbtor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Boolean.TRUE;

/**
 * Created by Pulkit Singh on 1/1/2017.
 */

public class VideoListAdapter extends BaseAdapter{
    Context mContext = null;
    ArrayList<String> mVideos = null;
    Boolean mAnimate = TRUE;

    static class ResultsViewHolder{
        public TextView title;
        public TextView category;
        public TextView size;
        public TextView seeds;
        public TextView leeches;
        public ImageView magnet;
        public ImageView url;
        public TextView provider;
    }

    public VideoListAdapter(Context context, ArrayList<String> iVideos)
    {
        mContext = context;
        mVideos = iVideos;
    }

    @Override
    public int getCount() {
        int count = 0;
        if(null != mVideos)
            count = mVideos.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        return mVideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View resultView = convertView;
        if(resultView == null) {
            LayoutInflater layoutInflater = ((Activity)mContext).getLayoutInflater();
            resultView = layoutInflater.inflate(R.layout.video, parent, false);
            ResultsViewHolder viewHolder = new ResultsViewHolder();
            viewHolder.title = (TextView) resultView.findViewById(R.id.torrent_title);
            viewHolder.category = (TextView) resultView.findViewById(R.id.torrent_category);
            viewHolder.leeches = (TextView) resultView.findViewById(R.id.torrent_leeches);
            viewHolder.provider = (TextView) resultView.findViewById(R.id.torrent_provider);
            viewHolder.provider.setId(position);
            viewHolder.provider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    Toast.makeText(mContext,"url",Toast.LENGTH_LONG).show();
                }
            });
            viewHolder.seeds = (TextView) resultView.findViewById(R.id.torrent_seeds);
            viewHolder.size = (TextView) resultView.findViewById(R.id.torrent_size);
            viewHolder.magnet = (ImageView) resultView.findViewById(R.id.icon_magnet);
            viewHolder.url = (ImageView) resultView.findViewById(R.id.icon_url_link);
            resultView.setTag(viewHolder);
        }

        final ResultsViewHolder viewHolder = (ResultsViewHolder) resultView.getTag();
        viewHolder.magnet.setId(position);
        viewHolder.url.setId(position);
        viewHolder.title.setText(mVideos.get(position));
        if(mAnimate == true)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext,android.R.anim.slide_in_left);
            resultView.startAnimation(animation);
        }
        return resultView;
    }
}
