package com.baliyaan.android.imdbtor;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/1/2017.
 */

public class ResultListAdapter extends BaseAdapter{
    Context mContext = null;
    ArrayList<Torrent> torrents = null;

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

    public ResultListAdapter(Context context, ArrayList<Torrent> iTorrents)
    {
        mContext = context;
        torrents = iTorrents;
    }

    @Override
    public int getCount() {
        int count = 0;
        if(null != torrents)
            count = torrents.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        return torrents.get(position);
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
            resultView = layoutInflater.inflate(R.layout.torrent_result, parent, false);
            ResultsViewHolder viewHolder = new ResultsViewHolder();
            viewHolder.title = (TextView) resultView.findViewById(R.id.torrent_title);
            viewHolder.category = (TextView) resultView.findViewById(R.id.torrent_category);
            viewHolder.leeches = (TextView) resultView.findViewById(R.id.torrent_leeches);
            viewHolder.provider = (TextView) resultView.findViewById(R.id.torrent_provider);
            viewHolder.seeds = (TextView) resultView.findViewById(R.id.torrent_seeds);
            viewHolder.size = (TextView) resultView.findViewById(R.id.torrent_size);
            viewHolder.magnet = (ImageView) resultView.findViewById(R.id.icon_magnet);
            viewHolder.url = (ImageView) resultView.findViewById(R.id.icon_url_link);
            resultView.setTag(viewHolder);
        }

        final ResultsViewHolder viewHolder = (ResultsViewHolder) resultView.getTag();
        viewHolder.title.setText(torrents.get(position).title);
        viewHolder.category.setText(torrents.get(position).category);
        viewHolder.leeches.setText(torrents.get(position).leeches);
        viewHolder.provider.setText(torrents.get(position).provider);
        viewHolder.seeds.setText(torrents.get(position).seeds);
        viewHolder.size.setText(String.valueOf((torrents.get(position).size)));
        viewHolder.magnet.setId(position);
        viewHolder.url.setId(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImageFromURL(torrents.get(position).icon,viewHolder.url);
            }
        }).start();
        return resultView;
    }

    public boolean loadImageFromURL(String fileUrl,
                                    ImageView iv){
        try {

            URL myFileUrl = new URL (fileUrl);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            iv.setImageBitmap(BitmapFactory.decodeStream(is));

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
