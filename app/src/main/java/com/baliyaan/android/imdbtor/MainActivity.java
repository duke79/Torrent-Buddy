package com.baliyaan.android.imdbtor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,SearchResultsFragment.OnFragmentInteractionListener {
    public Context mContext;
    SearchView mSearchView = null;
    SearchResultsFragment mSearchResultsFragment = null;
    LoginFragment mLoginFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        setupSearchView();
        setupLoginFragment();
    }

    private void setupSearchResultsFragment(String query) {
        if(mSearchResultsFragment==null) {
            mSearchResultsFragment = new SearchResultsFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mLoginFragment!=null)
            transaction.remove(mLoginFragment);
        transaction.add(R.id.activity_main,mSearchResultsFragment).commit();
        mSearchResultsFragment.Initiate(query);
    }

    private void setupLoginFragment() {
        if(mLoginFragment==null)
            mLoginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mSearchResultsFragment!=null)
            transaction.remove(mSearchResultsFragment);
        transaction.add(R.id.activity_main,mLoginFragment).commit();
    }

    private void setupSearchView() {
        mSearchView = (SearchView) findViewById(R.id.SearchBox);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                assert mSearchView != null;
                setupSearchResultsFragment(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Required only for communication among fragments
    }
}
