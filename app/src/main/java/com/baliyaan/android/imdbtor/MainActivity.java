package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity
        extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
                SearchResultsFragment.OnFragmentInteractionListener,
                VideoListFragment.OnFragmentInteractionListener {
    public Context mContext;
    SearchView mSearchView = null;
    SearchResultsFragment mSearchResultsFragment = null;
    LoginFragment mLoginFragment = null;
    VideoListFragment mVideoListFragment = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                StartSearch("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        setupLoginFragment();
        setupVideoListFragment();
    }

    private void setupLoginFragment() {
        if (mLoginFragment == null)
            mLoginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //if (mSearchResultsFragment != null)
          //  transaction.remove(mSearchResultsFragment);
        transaction.add(R.id.LoginFragmentContainer, mLoginFragment).commit();
    }

    private void setupVideoListFragment() {
        if (mVideoListFragment == null)
            mVideoListFragment = new VideoListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //if (mSearchResultsFragment != null)
          //  transaction.remove(mSearchResultsFragment);
        transaction.add(R.id.VideoListFragmentContainer, mVideoListFragment).commit();
    }

    public void StartSearch(String query) {
        if (mSearchResultsFragment == null) {
            mSearchResultsFragment = new SearchResultsFragment();
        }
        mSearchResultsFragment.SearchOnStart(query);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (mLoginFragment != null)
            transaction.remove(mLoginFragment);
        if(mVideoListFragment != null)
            transaction.remove(mVideoListFragment);
        transaction.add(R.id.SearchResultsFragmentContainer, mSearchResultsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        //fm.executePendingTransactions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Required only for communication among fragments
    }

    @Override
    public void onUserInfoUpdated(ArrayList<String> videosList) {
        if(null==videosList)return;
        if(videosList.size()>0)
            mVideoListFragment.OnVideosListUpdated(videosList);
    }
}
