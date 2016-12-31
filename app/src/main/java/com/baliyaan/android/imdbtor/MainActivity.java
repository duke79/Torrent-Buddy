package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                WebScraper.mContext = mContext;
                ArrayList<Torrent> torrents = TorrentProviderServices.GetTorrents(mContext,"the mart");
            }
        }).start();
    }
}
