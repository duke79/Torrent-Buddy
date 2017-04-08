package com.baliyaan.android.imdbtor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baliyaan.android.torrents.Torrent;
import com.baliyaan.android.uicomponents.ListAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/1/2017.
 */

public class TorrentResultsAdapter extends ListAdapter{

    public TorrentResultsAdapter(Context context, ArrayList<Object> itemsList, int itemLayoutID) {
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
        viewHolder.title = (TextView) view.findViewById(R.id.torrent_title);
        viewHolder.category = (TextView) view.findViewById(R.id.torrent_category);
        viewHolder.leeches = (TextView) view.findViewById(R.id.torrent_leeches);
        viewHolder.provider = (TextView) view.findViewById(R.id.torrent_provider);
        viewHolder.provider.setId(position);
        viewHolder.provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                //Toast.makeText(context(),((Torrent)itemsList().get(id)).url,Toast.LENGTH_LONG).show();
            }
        });
        viewHolder.seeds = (TextView) view.findViewById(R.id.torrent_seeds);
        viewHolder.size = (TextView) view.findViewById(R.id.torrent_size);
        viewHolder.magnet = (ImageView) view.findViewById(R.id.icon_magnet);
        viewHolder.magnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if(BuildConfig.DEBUG) {
                    //Toast.makeText(context(),((Torrent)itemsList().get(id)).magnetLink,Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(((Torrent)itemsList().get(id)).magnetLink));
                try {
                    context().startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    //Toast.makeText(context(), R.string.TorrentAppNotFound,Toast.LENGTH_LONG).show();
                }
            }
        });
        viewHolder.url = (ImageView) view.findViewById(R.id.icon_url_link);
        viewHolder.url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if(BuildConfig.DEBUG) {
                    //Toast.makeText(context(), ((Torrent)itemsList().get(id)).url, Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(((Torrent)itemsList().get(id)).url));
                try {
                    context().startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(context(), R.string.BrowserAppNotFound,Toast.LENGTH_LONG).show();
                }

            }
        });
        return viewHolder;
    }

    @Override
    protected void setViewHolderParams(final int position, final Object holder) {
        ((ResultsViewHolder)holder).title.setText(((Torrent)itemsList().get(position)).title);
        ((ResultsViewHolder)holder).category.setText(((Torrent)itemsList().get(position)).category);
        ((ResultsViewHolder)holder).leeches.setText(((Torrent)itemsList().get(position)).leeches);
        ((ResultsViewHolder)holder).provider.setText(((Torrent)itemsList().get(position)).provider);
        ((ResultsViewHolder)holder).seeds.setText(((Torrent)itemsList().get(position)).seeds);
        ((ResultsViewHolder)holder).size.setText(String.valueOf(((Torrent)itemsList().get(position)).size));
        ((ResultsViewHolder)holder).magnet.setId(position);
        ((ResultsViewHolder)holder).url.setId(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImageFromURL(((Torrent)itemsList().get(position)).icon,((ResultsViewHolder)holder).url);
            }
        }).start();
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
