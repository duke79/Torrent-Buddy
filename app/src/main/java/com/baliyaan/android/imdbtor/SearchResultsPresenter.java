package com.baliyaan.android.imdbtor;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baliyaan.android.torrents.Services;
import com.baliyaan.android.torrents.Torrent;
import com.squareup.otto.Bus;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Pulkit Singh on 1/29/2017.
 */

public class SearchResultsPresenter {
    private final Context mContext;
    private final String TAG = "SearchResultsPresenter";
    private View mSearchResultsPage;
    private Bus mBus;
    private ListViewCompat mSearchResults;
    private ArrayList<Torrent> mTorrents = new ArrayList<>();
    private ResultListAdapter mSearchResultsAdapter;
    private SearchView mSearchView;
    private String mQuery;
    private Handler mHandler;

    public SearchResultsPresenter(Context context, View searchResultsPage, Bus bus) {
        mContext = context;
        mSearchResultsPage = searchResultsPage;
        mBus = bus;
        mBus.register(this);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                setupBackIcon();
                setupSearchView();
                setupResultsList();
                configureVisibilityChange();
            }
        };
        handler.post(myRunnable);
    }

    private void setupBackIcon() {
        View backIcon = ((Activity)mContext).findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).onBackPressed();
            }
        });
    }

    private void configureVisibilityChange() {
        mSearchResultsPage.setTag(mSearchResultsPage.getVisibility());
        mSearchResultsPage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Toast.makeText(mContext,"Layout changed",Toast.LENGTH_SHORT).show();
                int newVis = mSearchResultsPage.getVisibility();
                if((int)mSearchResultsPage.getTag() != newVis)
                {
                    Toast.makeText(mContext,"SearchResults Visibility changed",Toast.LENGTH_SHORT).show();
                    if(mSearchResultsPage.getVisibility()==View.GONE)
                    {
                        Toast.makeText(mContext,"SearchResults GONE",Toast.LENGTH_SHORT).show();
                        mSearchResultsPage.setTag(mSearchResultsPage.getVisibility());
                        if(mQuery!=null)
                        {
                            mQuery="";
                            mSearchView.setQuery(mQuery,true);
                            Handler handler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    if(mTorrents!=null) {
                                        mTorrents.clear();
                                        mSearchResultsAdapter.notifyDataSetChanged();
                                    }
                                }
                            };
                            handler.post(myRunnable);
                        }
                    }
                }
            }
        });
    }

    private void setupSearchView() {
        mSearchView = (SearchView) ((Activity)mContext).findViewById(R.id.SearchBox);
        //mSearchView.setFocusable(true);
        mSearchView.setIconified(false);

        // Prevent Auto-Popping soft keyboard
        mSearchView.clearFocus();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                assert mSearchView != null;
                MakeSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    public void setupResultsList() {
        mSearchResults = (ListViewCompat) ((Activity)mContext).findViewById(R.id.Results);
        mSearchResultsAdapter = new ResultListAdapter(mContext, (ArrayList<Object>)(ArrayList) mTorrents,R.layout.torrent_result);
        mSearchResults.setAdapter(mSearchResultsAdapter);
        mSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Torrent torrent = (Torrent) mSearchResultsAdapter.getItem(position);
                Toast.makeText(mContext, torrent.title, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void MakeSearch(final String query) {
        mQuery = query;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTorrents.clear();
                if(mHandler==null)
                    mHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(mQuery.length()==0)
                        {
                            mSearchView.requestFocusFromTouch();
                        }
                        mSearchResultsAdapter.notifyDataSetChanged();
                    }
                };
                mHandler.post(myRunnable);

                InputStream inputStream = mContext.getResources().openRawResource(R.raw.meta);
                final ArrayList<Torrent> torrents = Services.GetTorrents(query,inputStream);
                int size = mTorrents.size();
                if(mHandler==null)
                    mHandler = new Handler(Looper.getMainLooper());
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mTorrents.addAll(torrents);
                        mSearchResultsAdapter.notifyDataSetChanged();
                    }
                };
                mHandler.post(myRunnable);
            }
        }).start();
    }
}
