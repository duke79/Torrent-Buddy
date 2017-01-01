package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Context mContext;
    ArrayList<Torrent> mTorrents = null;
    SearchView mSearchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mSearchView = (SearchView) findViewById(R.id.SearchBox);
        setupSearchView();

    }

    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                assert mSearchView != null;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mTorrents = TorrentProviderServices.GetTorrents(mContext, query);
                        int size = mTorrents.size();
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }
}
