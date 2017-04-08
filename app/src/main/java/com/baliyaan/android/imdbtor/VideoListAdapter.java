package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baliyaan.android.uicomponents.ListAdapter;

import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/1/2017.
 */

public class VideoListAdapter extends ListAdapter{


    public VideoListAdapter(Context context, ArrayList<Object> itemsList, int itemLayoutID) {
        super(context, itemsList, itemLayoutID);
    }

    static private class ResultsViewHolder{
        public TextView title;
        public TextView category;
        public TextView size;
        public TextView seeds;
        public TextView leeches;
        public ImageView magnet;
        public ImageView url;
        public TextView provider;
    }

    @Override
    protected Object createViewHolder(int position, View view) {
        ResultsViewHolder viewHolder = new ResultsViewHolder();
        viewHolder.title = (TextView) view.findViewById(R.id.video_title);
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                String videoTitle = (String) itemsList().get(id);
                //Toast.makeText(context(),videoTitle,Toast.LENGTH_LONG).show();
                // Start search
                Event.SearchTorrent searchTorrentEvent = new Event.SearchTorrent();
                searchTorrentEvent.query = videoTitle;
                MainActivity.bus.post(searchTorrentEvent);
            }
        });
        return viewHolder;
    }

    @Override
    protected void setViewHolderParams(int position, Object holder) {
        ((ResultsViewHolder)holder).title.setText((CharSequence) itemsList().get(position));
        ((ResultsViewHolder)holder).title.setId(position);
    }
}
