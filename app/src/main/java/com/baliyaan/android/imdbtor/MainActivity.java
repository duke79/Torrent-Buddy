package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Context mContext;
    ArrayList<Torrent> mTorrents = new ArrayList<>();
    SearchView mSearchView = null;
    ListViewCompat mResultsList = null;
    ResultListAdapter mResultListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        setupSearchView();
        setupResultsList();
    }

    private void setupResultsList() {
        mResultsList = (ListViewCompat) findViewById(R.id.Results);
        mResultListAdapter = new ResultListAdapter(mContext,mTorrents);
        mResultsList.setAdapter(mResultListAdapter);
        mResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Torrent torrent = (Torrent) mResultListAdapter.getItem(position);
                Toast.makeText(mContext,torrent.title,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSearchView() {
        mSearchView = (SearchView) findViewById(R.id.SearchBox);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                assert mSearchView != null;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mTorrents.clear();
                        mTorrents.addAll(TorrentProviderServices.GetTorrents(mContext, query));
                        int size = mTorrents.size();
                            runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mResultListAdapter.notifyDataSetChanged();
                            }
                        });
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
